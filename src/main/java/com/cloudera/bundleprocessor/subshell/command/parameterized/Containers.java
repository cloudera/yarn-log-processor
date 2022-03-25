/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudera.bundleprocessor.subshell.command.parameterized;

import com.cloudera.bundleprocessor.Constants;
import com.cloudera.bundleprocessor.OptionParser;
import com.cloudera.bundleprocessor.subshell.command.Command;
import com.cloudera.bundleprocessor.subshell.command.util.RegexElements;
import com.cloudera.bundleprocessor.subshell.context.Context;
import com.cloudera.bundleprocessor.subshell.search.format.Grepper;
import com.cloudera.bundleprocessor.subshell.search.format.GrepperFactory;
import com.cloudera.bundleprocessor.subshell.search.request.Executable;
import com.cloudera.bundleprocessor.subshell.search.request.SingleExecutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Containers class is a {@link Command} class for the CLI command "containers".
 * The execution of "containers" command lists every launched containers
 * belonging to an application or application attempt.
 */
public class Containers extends ParameterizedSearch {

  private static final Logger LOG =
      LoggerFactory.getLogger(Containers.class);

  private final String regexForKilledContainers =
      this.getContext().getConfig().getRegexes().getTimeStamp() + ".*"
          + RegexElements.CONTAINERID
          + RegexElements.TRANSITIONED_TO_KILLING;
  private final String regexForEveryContainer =
      this.getContext().getConfig().getRegexes().getTimeStamp() + ".*"
          + RegexElements.USER + ".*"
          + RegexElements.SUBMIT_CONTAINER_REQUEST + ".*"
          + RegexElements.SUCCESS + ".*"
          + RegexElements.APPID + ".*"
          + RegexElements.CONTAINERID;

  private final Map<String, Grepper> formatOptionMap = new HashMap<>();

  /**
   * Constructor of Containers.
   *
   * @param context contains searchEngine
   */
  public Containers(Context context) {
    super(context);
    formatOptionMap.put("list", GrepperFactory.createGrepper(
        GrepperFactory.CONTAINER_COLUMN));
    formatOptionMap.put("verbose", GrepperFactory.createGrepper(
        GrepperFactory.TIME_COLUMN, GrepperFactory.CONTAINER_COLUMN));
    formatOptionMap.put("raw", GrepperFactory.createGrepper(
        GrepperFactory.RAW_COLUMN));
    formatOptionMap.put("default", GrepperFactory.createGrepper(
        GrepperFactory.TIME_COLUMN, GrepperFactory.CONTAINER_COLUMN));
  }

  @Override
  protected OptionParser createOptionParser() {
    OptionParser.Builder optionParserBuilder = new OptionParser.Builder();
    return optionParserBuilder.setCommandName(getName())
        .addDefaultOptions() // adds --raw, --verbose, --list and --help options
        .addOption("app", "application", true,
            "specify the application ID", false)
        .addOption("att", "appattempt", true,
            "specify the appattempt ID", false)
        .addOption("con", "container", true,
            "specify the container ID", false)
        .addOption("k", "killed", false,
            "given if searching for killed containers", false)
        .addOption("ce", "ce-killed", false,
            "given if searching for exiting containers", false)
        .addOption("ec", "ce-exitcode", true,
            "searches for containers exiting with a specific exitcode", false)
        .addOption("pe", "preempted", false,
            "searches for preempted containers", false)
        .build();
  }

  @Override
  protected Executable createExecutable(OptionParser optionParser) {
    SingleExecutable.Builder execBuilder = new SingleExecutable.Builder();
    if (optionParser.checkParameter("preempted")) {
      execBuilder.isCheckingRmLogs();
    } else {
      execBuilder.isCheckingNmLogs();
    }
    String regex = createRegexForContainerStatus(optionParser, execBuilder);
    regex = modifyRegexForIdentifiers(optionParser, regex);
    LOG.info("Using the following regular expression " +
        "to find containers \n" + regex);
    return execBuilder.withPattern(Pattern.compile(regex))
        .build();
  }

  private String createRegexForContainerStatus(
      OptionParser optionParser, SingleExecutable.Builder execBuilder) {
    boolean killed = optionParser.checkParameter("killed");
    boolean ceKilled = optionParser.checkParameter("ce-killed");
    String ceExitcode = optionParser.getParameter("ce-exitcode");
    boolean preempted = optionParser.checkParameter("preempted");
    String regexForExitingContainers =
        RegexElements.START_OF_CONTAINER_ERROR_LOG
            + RegexElements.LINES_WITHOUT_TIMESTAMP
            + RegexElements.COMMAND_ARRAY
            + RegexElements.END_OF_CONTAINER_ERROR_LOG;
    if (killed) {
      execBuilder.withFormatter(formatOptionMap.get("default"));
      return regexForKilledContainers;
    } else if (ceKilled) {
      execBuilder.withFormatter(formatOptionMap.get("raw"));
      return regexForExitingContainers;
    } else if (ceExitcode != null) {
      execBuilder.withFormatter(formatOptionMap.get("raw"));
      return modifyRegexForExitCode(regexForExitingContainers, ceExitcode);
    } else if (preempted) {
      execBuilder.withFormatter(
          evaluateFormatOptions(formatOptionMap, optionParser));
      return RegexElements.PREEMPTED_CONTAINER;
    } else {
      execBuilder.withFormatter(
          evaluateFormatOptions(formatOptionMap, optionParser));
      return regexForEveryContainer;
    }
  }

  private String modifyRegexForIdentifiers(
      OptionParser optionParser, String regex) {
    String appId = optionParser.getParameter("application");
    String appAttemptId = optionParser.getParameter("appattempt");
    String containerId = optionParser.getParameter("container");
    if (containerId != null) {
      return modifyRegexForContainer(regex, containerId);
    } else if (appAttemptId != null) {
      return modifyRegexForAttempt(regex, appAttemptId);
    } else if (appId != null) {
      return modifyRegexForApp(regex, appId);
    } else {
      return regex;
    }
  }


  private String modifyRegexForApp(String regex, String appId) {
    LOG.debug("Received the following application ID " +
        "to find containers belonging to it: " + appId);
    String appNumber = createAppNumber(appId);
    regex = regex.replace(
        RegexElements.APPLICATION_NUMBER_INSIDE_CONTAINERID, appNumber);
    return regex;
  }

  private String modifyRegexForAttempt(String regex, String appAttemptId) {
    LOG.debug("Received the following application attempt ID " +
        "to find containers belonging to it: "
        + appAttemptId);
    String appAttemptNumber = createAppAttemptNumber(appAttemptId);
    regex = regex.replace(
        RegexElements.APPATTEMPT_NUMBER_INSIDE_CONTAINERID, appAttemptNumber);
    return regex;
  }

  private String modifyRegexForContainer(String regex, String containerId) {
    LOG.info("Received the following container ID to search for: "
        + containerId);
    String containerIdInGroup =
        RegexElements.wrapInCapturingGroup("containerid", containerId);
    regex = regex.replace(RegexElements.CONTAINERID, containerIdInGroup);
    return regex;
  }

  private String modifyRegexForExitCode(String regex, String exitCode) {
    LOG.debug(
        "Received the following application attempt ID " +
            "to find containers belonging to it: " + exitCode);
    regex = regex.replace(RegexElements.EXIT_CODE, exitCode);
    return regex;
  }

  private String createAppNumber(String appId) {
    int prefixLength = RegexElements.APP_PREFIX.length();
    if (appId.startsWith(RegexElements.APP_PREFIX)) {
      return appId.substring(prefixLength);
    } else {
      throw new IllegalArgumentException("Argument provided for Containers"
          + " is not a valid application id");
    }
  }

  private String createAppAttemptNumber(String appAttemptId) {
    int prefixLength = RegexElements.APPATTEMPT_PREFIX.length();
    if (appAttemptId.startsWith(RegexElements.APPATTEMPT_PREFIX)) {
      String firstPart = appAttemptId.substring(
          prefixLength, appAttemptId.length() -
              Constants.RANGE_OF_UNDISPLAYED_CHARS_IN_CONTAINERID[1]);
      String secondPart = appAttemptId.substring(
          appAttemptId.length() -
              Constants.RANGE_OF_UNDISPLAYED_CHARS_IN_CONTAINERID[0]);
      return firstPart + secondPart;
    } else {
      throw new IllegalArgumentException("Argument provided for Containers"
          + " is not a valid application attempt id");
    }
  }

  @Override
  public String getName() {
    return "containers";
  }

  @Override
  public String getDescription() {
    return "lists all containers fit for the user-defined parameters";
  }
}
