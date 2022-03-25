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
import com.cloudera.bundleprocessor.subshell.context.Context;
import com.cloudera.bundleprocessor.subshell.search.format.Grepper;
import com.cloudera.bundleprocessor.subshell.search.format.GrepperFactory;
import com.cloudera.bundleprocessor.subshell.search.request.Executable;
import com.cloudera.bundleprocessor.subshell.search.request.SingleExecutable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Grep class is a {@link Command} class for the CLI command "grep".
 * The execution of "grep" command searches for a given expression in the logs.
 */
public class Grep extends HybridSearch {

  private final Map<String, Grepper> formatOptionMap = new HashMap<>();

  /**
   * Constructor for Grep.
   *
   * @param context contains searchEngine
   */
  public Grep(Context context) {
    super(context);
    formatOptionMap.put("default", GrepperFactory.createGrepper(
        GrepperFactory.RAW_COLUMN));
    formatOptionMap.put("short", GrepperFactory.createGrepper(
        GrepperFactory.EXPRESSION_COLUMN));
  }

  @Override
  protected OptionParser createOptionParser() {
    OptionParser.Builder optionParserBuilder = new OptionParser.Builder();
    return optionParserBuilder.setCommandName(getName())
        .addDefaultOptions() // adds --raw, --verbose, --list and --help options
        .addOption("rm", "resourcemanager", false,
            "search in resourcemanager logs", false)
        .addOption("nm", "nodemanager", false,
            "search in nodemanager logs", false)
        .addOption("s", "short", false,
            "only displays the specified expression not the row", false)
        .build();
  }

  @Override
  protected Executable createExecutable(String expression,
                                        OptionParser optionParser) {
    final String searchedExpression = "(?<line>"
        + this.getContext().getConfig().getRegexes().getTimeStamp()
        + ".*(?<expression>" + expression + ").*)";
    final Pattern searchedPattern = Pattern.compile(searchedExpression);
    SingleExecutable.Builder execBuilder = new SingleExecutable.Builder()
        .withPattern(searchedPattern);
    if (optionParser.checkParameter("resourcemanager")
        && !optionParser.checkParameter("nodemanager")) {
      execBuilder.isCheckingRmLogs();
    } else if (optionParser.checkParameter("nodemanager")
        && !optionParser.checkParameter("resourcemanager")) {
      execBuilder.isCheckingNmLogs();
    } else {
      execBuilder.isCheckingRmLogs();
      execBuilder.isCheckingNmLogs();
    }
    execBuilder.withFormatter(evaluateFormatOptions(
        formatOptionMap, optionParser));
    return execBuilder.build();
  }

  @Override
  public String getName() {
    return "grep";
  }

  @Override
  public String getDescription() {
    return "Searches for a specific String in the document and" +
        " gives back every row where it is found";
  }
}
