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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Applications class is a {@link Command} class
 * for the CLI command "applications".
 * The execution of "applications" command lists
 * every launched application in the cluster.
 */
public class Applications extends ParameterizedSearch {

  private final String appRegex =
      this.getContext().getConfig().getRegexes().getTimeStamp() + ".*"
          + RegexElements.USER + ".*"
          + RegexElements.SUBMIT_APPLICATION_REQUEST + ".*"
          + RegexElements.SUCCESS + ".*"
          + RegexElements.APPID + ".*";
  private final Pattern appPattern = Pattern.compile(appRegex);
  private final Map<String, Grepper> formatOptionMap = new HashMap<>();

  /**
   * Constructor of Applications.
   *
   * @param context contains searchEngine
   */
  public Applications(Context context) {
    super(context);
    formatOptionMap.put("list", GrepperFactory.createGrepper(
        GrepperFactory.APP_ID_COLUMN));
    formatOptionMap.put("verbose", GrepperFactory.createGrepper(
        GrepperFactory.TIME_COLUMN, GrepperFactory.APP_ID_COLUMN,
        GrepperFactory.USER_COLUMN));
    formatOptionMap.put("raw", GrepperFactory.createGrepper(
        GrepperFactory.RAW_COLUMN));
    formatOptionMap.put("default", GrepperFactory.createGrepper(
        GrepperFactory.TIME_COLUMN, GrepperFactory.APP_ID_COLUMN));
  }

  @Override
  protected OptionParser createOptionParser() {
    OptionParser.Builder optionParserBuilder = new OptionParser.Builder();
    return optionParserBuilder.setCommandName(getName())
        .addDefaultOptions() // adds --raw, --verbose, --list and --help options
        .build();
  }

  @Override
  protected Executable createExecutable(OptionParser optionParser) {
    SingleExecutable.Builder execBuilder = new SingleExecutable.Builder()
        .withPattern(appPattern)
        .isCheckingRmLogs()
        .withFormatter(evaluateFormatOptions(formatOptionMap, optionParser));
    return execBuilder.build();
  }

  @Override
  public String getName() {
    return "applications";
  }

  @Override
  public String getDescription() {
    return "lists all applications in the cluster and " +
        "their owners and submission time";
  }
}
