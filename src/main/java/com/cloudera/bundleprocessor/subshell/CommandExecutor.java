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
import com.cloudera.bundleprocessor.subshell.format.Printable;

import java.util.Arrays;
import java.util.Map;

public class CommandExecutor {

  private final Map<String, Command> commandMap;
  private boolean readingMore = true;

  public CommandExecutor(Map<String, Command> commandMap) {
    this.commandMap = commandMap;
  }

  /**
   * Executes the commandStr read from the CLI
   * and returns the answer of the program.
   *
   * @param commandStr string read from CLI
   * @return answer of the program
   * @throws NullPointerException if there is no command
   * matching with commandStr
   */
  public String executeCommand(String commandStr)
      throws UnknownCommandException {
    String[] commands = splitCommand(commandStr);
    Command command = commandMap.get(commands[0]);
    if (command == null) {
      throw new UnknownCommandException();
    }
    Printable printable =
        command.generatePrintable(Arrays.copyOfRange(
            commands, 1, commands.length));
    this.readingMore = command.readMore();
    return printable.print();
  }

  public boolean isReadingMore() {
    return readingMore;
  }

  public Map<String, Command> getCommandMap() {
    return commandMap;
  }

  private String[] splitCommand(String command) {
    command = command.trim();
    return command.split("\\s+");
  }
}
