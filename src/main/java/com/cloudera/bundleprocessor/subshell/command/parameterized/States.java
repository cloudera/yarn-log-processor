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

import com.cloudera.bundleprocessor.OptionParser;
import com.cloudera.bundleprocessor.subshell.command.Command;
import com.cloudera.bundleprocessor.subshell.command.util.RegexElements;
import com.cloudera.bundleprocessor.subshell.context.Context;
import com.cloudera.bundleprocessor.subshell.search.format.Grepper;
import com.cloudera.bundleprocessor.subshell.search.format.GrepperFactory;
import com.cloudera.bundleprocessor.subshell.search.request.Executable;
import com.cloudera.bundleprocessor.subshell.search.request.SingleExecutable;
import org.apache.commons.cli.MissingOptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * States class is a {@link Command} class for the CLI command "states".
 * "states" command looks through the log folder and
 * lists every statechange of the given actor.
 */
public class States extends ParameterizedSearch {

  private static final Logger LOG =
      LoggerFactory.getLogger(States.class);

  private final String eventRegex =
      this.getContext().getConfig().getRegexes().getTimeStamp() + ".*"
          + RegexElements.REPLACEIT + ".*"
          + RegexElements.STATE_TRANSITION + ".*"
          + RegexElements.EVENT;
  private final String nodeEventRegex =
      this.getContext().getConfig().getRegexes().getTimeStamp() + ".*"
          + RegexElements.RMNODEIMPL_CLASS + ": .*"
          + RegexElements.REPLACEIT + ".*"
          + RegexElements.NODE_TRANSITION
          + RegexElements.STATE_TRANSITION + ".*";
  private final Map<String, Grepper> formatOptionMap = new HashMap<>();

  /**
   * Constructor of States.
   *
   * @param context contains searchEngine
   */
  public States(Context context) {
    super(context);
    formatOptionMap.put("list", GrepperFactory.createGrepper(
        GrepperFactory.TO_STATE_COLUMN));
    formatOptionMap.put("verbose", GrepperFactory.createGrepper(
        GrepperFactory.TIME_COLUMN, GrepperFactory.EVENT_COLUMN,
        GrepperFactory.FROM_STATE_COLUMN, GrepperFactory.TO_STATE_COLUMN));
    formatOptionMap.put("raw", GrepperFactory.createGrepper(
        GrepperFactory.RAW_COLUMN));
    formatOptionMap.put("default", GrepperFactory.createGrepper(
        GrepperFactory.TIME_COLUMN, GrepperFactory.FROM_STATE_COLUMN,
        GrepperFactory.TO_STATE_COLUMN));
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
        .addOption("c", "container", true,
            "specify the container ID", false)
        .addOption("n", "node", true,
            "specify the node", false)
        .build();
  }

  @Override
  protected Executable createExecutable(OptionParser optionParser)
      throws MissingOptionException {
    final String nodeId = optionParser.getParameter("node");
    final String containerId = optionParser.getParameter("container");
    final String appAttemptId = optionParser.getParameter("appattempt");
    final String appId = optionParser.getParameter("application");
    SingleExecutable.Builder execBuilder;
    if (nodeId != null) {
      // there are no events in case of nodes,
      // so we don't support the --verbose option
      checkVerboseOption(optionParser);
      execBuilder = createBuilderForNode(nodeId);
    } else if (containerId != null) {
      checkVerboseOption(optionParser);
      Grepper grepper = GrepperFactory.createGrepper(
          GrepperFactory.TIME_COLUMN, GrepperFactory.ROLE_COLUMN,
          GrepperFactory.FROM_STATE_COLUMN, GrepperFactory.TO_STATE_COLUMN);
      return createBuilderForContainer(containerId)
          .withFormatter(grepper)
          .build();
    } else if (appAttemptId != null) {
      execBuilder = createExecutableBuilderForAttempt(appAttemptId);
    } else if (appId != null) {
      execBuilder = createExecutableBuilderForApp(appId);
    } else {
      throw new MissingOptionException(
          "No input parameter was provided for States class");
    }
    execBuilder.withFormatter(evaluateFormatOptions(
        formatOptionMap, optionParser));
    return execBuilder.build();
  }

  private SingleExecutable.Builder createExecutableBuilderForApp(String appId) {
    LOG.debug(
        "Received the following application ID to find states belonging to it: "
            + appId);
    final String stateRegex =
        eventRegex.replace(RegexElements.REPLACEIT, appId);
    final Pattern statePattern = Pattern.compile(stateRegex);
    return new SingleExecutable.Builder()
        .withPattern(statePattern)
        .isCheckingRmLogs();
  }

  private SingleExecutable.Builder createExecutableBuilderForAttempt(
      String appAttemptId) {
    LOG.debug(
        "Received the following application attempt ID to " +
            "find states belonging to it: " + appAttemptId);
    final String stateRegex = eventRegex.replace(
        RegexElements.REPLACEIT, appAttemptId);
    final Pattern statePattern = Pattern.compile(stateRegex);
    return new SingleExecutable.Builder()
        .withPattern(statePattern)
        .isCheckingRmLogs();
  }

  private SingleExecutable.Builder createBuilderForContainer(
      String containerId) {
    LOG.debug(
        "Received the following container ID to find states belonging to it: "
            + containerId);
    final String stateRegex =
        this.getContext().getConfig().getRegexes().getTimeStamp() + ".*"
            + RegexElements.ROLE + ".*"
            + containerId + ".*"
            + RegexElements.STATE_TRANSITION + ".*";
    final Pattern statePattern = Pattern.compile(stateRegex);
    return new SingleExecutable.Builder()
        .withPattern(statePattern)
        .isCheckingRmLogs()
        .isCheckingNmLogs();
  }

  private SingleExecutable.Builder createBuilderForNode(String nodeId) {
    LOG.debug("Received the following node ID to find states belonging to it: "
        + nodeId);
    final String stateRegex =
        nodeEventRegex.replace(RegexElements.REPLACEIT, nodeId);
    final Pattern statePattern = Pattern.compile(stateRegex);
    return new SingleExecutable.Builder()
        .withPattern(statePattern)
        .isCheckingRmLogs();
  }

  private void checkVerboseOption(OptionParser optionParser) {
    if (optionParser.checkParameter("verbose")) {
      throw new UnsupportedOperationException(
          "--verbose is not supported with this option");
    }
  }

  @Override
  public String getName() {
    return "states";
  }

  @Override
  public String getDescription() {
    return "lists all states happened " +
        "to a specified YARN object (app/attempt/container)";
  }
}
