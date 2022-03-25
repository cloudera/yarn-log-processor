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
 * Exceptions class is a {@link Command} class for the CLI command "exceptions".
 * "exceptions" command looks through the log folder and lists every exception.
 */
public class Exceptions extends ParameterizedSearch {

  private static final String EXCEPTION_REGEX = RegexElements.ANY_LINE
      + RegexElements.EXCEPTION_WITH_STACKTRACE;
  private static final Pattern EXCEPTION_PATTERN =
      Pattern.compile(EXCEPTION_REGEX);

  private final Map<String, Grepper> formatOptionMap = new HashMap<>();

  /**
   * Constructor of Exceptions.
   *
   * @param context contains searchEngine
   */
  public Exceptions(Context context) {
    super(context);
    formatOptionMap.put("default", GrepperFactory.createGrepper(
        GrepperFactory.TIME_COLUMN, GrepperFactory.EXCEPTION_COLUMN));
    formatOptionMap.put("verbose",
        GrepperFactory.createGrepper(GrepperFactory.RAW_COLUMN));
  }

  @Override
  protected OptionParser createOptionParser() {
    OptionParser.Builder optionParserBuilder = new OptionParser.Builder();
    return optionParserBuilder.setCommandName(getName())
        .addOption("v", "verbose", false,
            "displays whole stacktrace", false)
        .addOption("h", "help", false,
            "display the valid subcommands of the command", false)
        .build();
  }

  @Override
  protected Executable createExecutable(OptionParser optionParser) {
    SingleExecutable.Builder execBuilder = new SingleExecutable.Builder();
    execBuilder.withFormatter(
        evaluateFormatOptions(formatOptionMap, optionParser));
    return execBuilder
        .withPattern(EXCEPTION_PATTERN)
        .isCheckingRmLogs()
        .build();
  }

  @Override
  public String getName() {
    return "exceptions";
  }

  @Override
  public String getDescription() {
    return "lists all exceptions in the logs";
  }
}
