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

import com.cloudera.bundleprocessor.console.ConsoleWriter;
import com.cloudera.bundleprocessor.subshell.context.SearchIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class Subshell {

  private static final Logger LOG =
      LoggerFactory.getLogger(Subshell.class);

  private final CommandLine commandLine;
  private final CommandExecutor commandExecutor;
  private SearchIntent searchIntent;

  /**
   * Subshell parses from the CLI, executes them and writes their output on CLI.
   *
   * @param commandLine     for parsing commands
   * @param commandExecutor for executing commands
   */
  public Subshell(CommandLine commandLine, CommandExecutor commandExecutor) {
    this.commandLine = commandLine;
    this.commandExecutor = commandExecutor;
  }

  public void init(SearchIntent searchIntent) throws IOException {
    this.searchIntent = searchIntent;
    commandLine.init();
  }

  /**
   * The run function opens the subshell.
   * Multiple line of commands can be executed in the subshell
   * Subshell terminates after the "exit" command
   */
  public void run() {
    String command = this.searchIntent.getCommand();
    boolean launchingSubshell = this.searchIntent.isLaunchingShell();
    if (command != null) {
      executeCommand(command,
          "The command after parameter --command was invalid");
    }
    if (launchingSubshell) {
      runSubshell();
    }
  }

  private void runSubshell() {
    LOG.info("Subshell was launched");
    while (commandExecutor.isReadingMore()) {
      String command = commandLine.readLine();
      executeCommand(command,
          "For listing the valid commands press tab or type " +
              "\\\"help\\\" in the CLI.");
    }
    LOG.info("Subshell was closed");
  }

  private void executeCommand(String command, String errorMessage) {
    // LOG.info("Executing command: " + command);
    try {
      String commandOutput = commandExecutor.executeCommand(command);
      ConsoleWriter.CONSOLE.info(commandOutput);
    } catch (UnknownCommandException e) {
      // ConsoleWriter.error(e.getMessage());
      ConsoleWriter.CONSOLE.info(errorMessage);
    }
  }
}
