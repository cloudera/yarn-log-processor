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
 * Events class is a {@link Command} class for the CLI command "events".
 * "events" command looks through the log folder and
 * lists every event happened with the given actor.
 */
public class Events extends ParameterizedSearch {

  private static final Logger LOG =
      LoggerFactory.getLogger(Events.class);

  private final String eventRegex =
      this.getContext().getConfig().getRegexes().getTimeStamp() + ".*"
          + RegexElements.REPLACEIT + ".*"
          + RegexElements.STATE_TRANSITION + ".*"
          + RegexElements.EVENT;
  private final Map<String, Grepper> formatOptionMap = new HashMap<>();

  /**
   * Constructor of Events.
   *
   * @param context contains searchEngine
   */
  public Events(Context context) {
    super(context);
    formatOptionMap.put("list", GrepperFactory.createGrepper(
        GrepperFactory.EVENT_COLUMN));
    formatOptionMap.put("verbose", GrepperFactory.createGrepper(
        GrepperFactory.TIME_COLUMN, GrepperFactory.EVENT_COLUMN,
        GrepperFactory.FROM_STATE_COLUMN, GrepperFactory.TO_STATE_COLUMN));
    formatOptionMap.put("raw", GrepperFactory.createGrepper(
        GrepperFactory.RAW_COLUMN));
    formatOptionMap.put("default", GrepperFactory.createGrepper(
        GrepperFactory.TIME_COLUMN, GrepperFactory.EVENT_COLUMN));
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
        .addOption("rm", "resourcemanager", false,
            "displays events of resourcemanager", false)
        .build();
  }

  @Override
  protected Executable createExecutable(OptionParser optionParser)
      throws MissingOptionException {
    final String appAttemptId = optionParser.getParameter("appattempt");
    final String appId = optionParser.getParameter("application");
    boolean ifCheckingRmEvents = optionParser.checkParameter("resourcemanager");
    String eventRegex;
    SingleExecutable.Builder execBuilder = new SingleExecutable.Builder();
    if (ifCheckingRmEvents) {
      LOG.debug("Search for events of the resourcemanager...");
      eventRegex = RegexElements.RM_EVENT_REGEX;
      execBuilder.separateBySourceFile();
    } else if (appAttemptId != null) {
      eventRegex = createRegexForAttempt(appAttemptId);
    } else if (appId != null) {
      eventRegex = createRegexForApp(appId);
    } else {
      throw new MissingOptionException(
          "No input parameter was provided for Events class");
    }
    LOG.debug("Using the following regular expression to find events"
        + "\n" + eventRegex);
    Pattern eventPattern = Pattern.compile(eventRegex);
    execBuilder.withPattern(eventPattern)
        .isCheckingRmLogs();
    execBuilder.withFormatter(
        evaluateFormatOptions(formatOptionMap, optionParser));
    return execBuilder.build();
  }

  private String createRegexForApp(String appId) {
    LOG.debug(
        "Received the following application ID to find events belonging to it: "
            + appId);
    return eventRegex.replace(RegexElements.REPLACEIT, appId);
  }

  private String createRegexForAttempt(String appAttemptId) {
    LOG.debug(
        "Received the following application attempt ID to find events " +
            "belonging to it: " + appAttemptId);
    return eventRegex.replace(RegexElements.REPLACEIT, appAttemptId);
  }

  @Override
  public String getName() {
    return "events";
  }

  @Override
  public String getDescription() {
    return "lists all events happened to a specified YARN entity";
  }
}
