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

package com.cloudera.bundleprocessor.subshell.command.util;

import com.cloudera.bundleprocessor.OptionParser;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.cli.Option;
import org.jline.builtins.Completers.TreeCompleter;
import org.jline.builtins.Completers.TreeCompleter.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class AutoCompleterWrapper {

  private final String commandName;
  private final List<String> options = new ArrayList<>();

  /**
   * TreeCompleterWrapper is initialized with an {@link OptionParser} object.
   * It reads the possible options to complete them for user in CLI
   *
   * @param optionParser is containing the valid options for 1 command
   */
  public AutoCompleterWrapper(OptionParser optionParser) {
    this.commandName = optionParser.getCommandName();
    Collection<Option> optionsCollection =
        optionParser.getOptionsObject().getOptions();
    for (Option option : optionsCollection) {
      this.options.add("-" + option.getOpt());
      this.options.add("--" + option.getLongOpt());
    }
  }

  public AutoCompleterWrapper(String commandName) {
    this.commandName = commandName;
  }

  @VisibleForTesting
  public String getCommandName() {
    return commandName;
  }

  @VisibleForTesting
  List<String> getOptions() {
    return options;
  }

  /**
   * {code generateTreeCompleterNode} creates the TreeCompleter node
   * depending on the valid options in CLI.
   *
   * @return Completers.TreeCompleter.Node containing the valid options
   * for a specific command
   */
  public Node generateTreeCompleterNode() {
    TreeCompleter commandNode = new TreeCompleter(node(commandName));
    List<Node> optionNodes = new ArrayList<>();
    for (String option : options) {
      optionNodes.add(node(option));
    }
    return new org.jline.builtins.Completers.TreeCompleter.Node(
        commandNode, optionNodes);
  }
}
