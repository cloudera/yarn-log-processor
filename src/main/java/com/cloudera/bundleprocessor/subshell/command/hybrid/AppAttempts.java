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

package com.cloudera.bundleprocessor.subshell.command.hybrid;

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
 * AppAttempts class is a {@link Command} class for the CLI command
 * "appattempts". The execution of "appattempts" command lists
 * every registered application attempt
 * belonging to a specific application.
 */
public class AppAttempts extends HybridSearch {

  private static final Logger LOG =
      LoggerFactory.getLogger(AppAttempts.class);

  private final Map<String, Grepper> formatOptionMap = new HashMap<>();

  /**
   * Constructor of AppAttempts.
   *
   * @param context contains searchEngine
   */
  public AppAttempts(Context context) {
    super(context);
    formatOptionMap.put("list", GrepperFactory.createGrepper(
        GrepperFactory.ATTEMPT_ID_COLUMN));
    formatOptionMap.put("verbose", GrepperFactory.createGrepper(
        GrepperFactory.TIME_COLUMN, GrepperFactory.ATTEMPT_ID_COLUMN));
    formatOptionMap.put("raw", GrepperFactory.createGrepper(
        GrepperFactory.RAW_COLUMN));
    formatOptionMap.put("default", GrepperFactory.createGrepper(
        GrepperFactory.ATTEMPT_ID_COLUMN));
  }

  @Override
  protected OptionParser createOptionParser() {
    OptionParser.Builder optionParserBuilder = new OptionParser.Builder();
    return optionParserBuilder.setCommandName(getName())
        .addDefaultOptions() // adds --raw, --verbose, --list and --help options
        .build();
  }

  @Override
  protected Executable createExecutable(String appId,
                                        OptionParser optionParser) {
    LOG.debug("Received the following application ID to find attempts " +
        "belonging to it: " + appId);
    final String attemptRegex = createRegex(appId);
    final Pattern attemptPattern = Pattern.compile(attemptRegex);
    SingleExecutable.Builder execBuilder = new SingleExecutable.Builder()
        .withPattern(attemptPattern)
        .isCheckingRmLogs()
        .withFormatter(evaluateFormatOptions(formatOptionMap, optionParser));
    return execBuilder.build();
  }

  private String createRegex(String appId) {
    int prefixLength = RegexElements.APP_PREFIX.length();
    if (appId.length() > prefixLength
        && appId.substring(0, prefixLength).equals(RegexElements.APP_PREFIX)) {
      final String appNumber = appId.substring("application_".length());
      final String attemptRegex =
          this.getContext().getConfig().getRegexes().getTimeStamp() + ".*" +
              RegexElements.ATTEMPTID_REGISTERED.replace(
                  "(?<appnumber>\\d+_\\d+)", appNumber);
      LOG.debug("Using the following regular expression " +
          "to find application attempt: \n" + attemptRegex);
      return attemptRegex;
    } else {
      throw new IllegalArgumentException("The first argument " +
          "after appattempt command needs to be a valid application ID");
    }
  }

  @Override
  public String getName() {
    return "appattempts";
  }

  @Override
  public String getDescription() {
    return "lists all application attempts connected to a specific application";
  }
}
