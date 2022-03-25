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
 * Resources class is a {@link Command} class for the CLI command "resources".
 * "resources" command looks through the log folder and lists
 * every YARN node and their resource capabilities.
 */
public class Resources extends ParameterizedSearch {

  private final String resourceRegex =
      this.getContext().getConfig().getRegexes().getTimeStamp() + ".*"
          + RegexElements.RESOURCE_REGEX;
  private final String allResourcesRegex =
      this.getContext().getConfig().getRegexes().getTimeStamp() + ".*"
          + RegexElements.CUSTOM_RESOURCE_TYPES_REGEX;
  private final Pattern resourcePattern = Pattern.compile(resourceRegex);
  private final Pattern allResourcesPattern =
      Pattern.compile(allResourcesRegex);
  private final Map<String, Grepper> formatOptionMap = new HashMap<>();
  private final Map<String, Grepper> allResFormatOptionMap = new HashMap<>();

  /**
   * Constructor for Grep.
   *
   * @param context contains searchEngine
   */
  public Resources(Context context) {
    super(context);
    formatOptionMap.put("verbose", GrepperFactory.createGrepper(
        GrepperFactory.TIME_COLUMN, GrepperFactory.NODE_COLUMN,
        GrepperFactory.MEMORY_COLUMN, GrepperFactory.V_CORES_COLUMN));
    formatOptionMap.put("default", GrepperFactory.createGrepper(
        GrepperFactory.NODE_COLUMN, GrepperFactory.MEMORY_COLUMN,
        GrepperFactory.V_CORES_COLUMN));
    allResFormatOptionMap.put("verbose", GrepperFactory.createGrepper(
        GrepperFactory.TIME_COLUMN, GrepperFactory.NODE_COLUMN,
        GrepperFactory.ALL_RESOURCES_COLUMN));
    allResFormatOptionMap.put("default", GrepperFactory.createGrepper(
        GrepperFactory.NODE_COLUMN, GrepperFactory.ALL_RESOURCES_COLUMN));
  }

  @Override
  protected OptionParser createOptionParser() {
    OptionParser.Builder optionParserBuilder = new OptionParser.Builder();
    return optionParserBuilder.setCommandName(getName())
        .addOption("v", "verbose", false,
            "specifies if all the resource allocation should be displayed",
            false)
        .addOption("h", "help", false,
            "display the valid subcommands of the command", false)
        .addOption("a", "all", false,
            "displays all resource types, including GPU and FPGA", false)
        .build();
  }

  @Override
  protected Executable createExecutable(OptionParser optionParser) {
    SingleExecutable.Builder execBuilder = new SingleExecutable.Builder();
    boolean all = optionParser.checkParameter("all");
    Map<String, Grepper> options = allResFormatOptionMap;
    Pattern resourcesPattern = allResourcesPattern;
    if (!all) {
      options = formatOptionMap;
      resourcesPattern = resourcePattern;
    }
    if (!optionParser.checkParameter("verbose")) {
      execBuilder.removeDuplicationOfParameter("node");
    }
    return execBuilder
        .withPattern(resourcesPattern)
        .withFormatter(evaluateFormatOptions(options, optionParser))
        .isCheckingRmLogs()
        .build();
  }

  @Override
  public String getName() {
    return "resources";
  }

  @Override
  public String getDescription() {
    return "lists all nodes and their resource capabilities";
  }
}
