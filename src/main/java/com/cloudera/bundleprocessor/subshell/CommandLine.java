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

package com.cloudera.bundleprocessor.subshell;

import com.cloudera.bundleprocessor.subshell.command.Command;
import org.jline.builtins.Completers;
import org.jline.builtins.Widgets;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandLine {

  private final String prompt;
  private final Map<String, Command> commandMap;
  private LineReader reader;

  public CommandLine(String name, Map<String, Command> commandMap) {
    this.prompt = name + "> ";
    this.commandMap = commandMap;
  }

  /**
   * Creates a LineReader object to autocomplete
   * and read commands written in CLI.
   *
   * @throws IOException if the terminal cant be launched
   */
  public void init() throws IOException {
    Terminal terminal;
    terminal = TerminalBuilder.terminal();
    Completers.TreeCompleter completer = createCompleter();
    DefaultParser parser = new DefaultParser();
    this.reader = LineReaderBuilder.builder()
        .terminal(terminal)
        .completer(completer)
        .parser(parser)
        .build();
    Widgets.AutopairWidgets autopairWidgets =
        new Widgets.AutopairWidgets(reader);
    autopairWidgets.enable();
  }

  private Completers.TreeCompleter createCompleter() {
    List<Completers.TreeCompleter.Node> nodeList = commandMap.values()
        .stream().map(command ->
            command.createAutoCompleterWrapper().generateTreeCompleterNode())
        .collect(Collectors.toList());
    return new Completers.TreeCompleter(nodeList);
  }

  public String readLine() {
    return reader.readLine(prompt);
  }
}
