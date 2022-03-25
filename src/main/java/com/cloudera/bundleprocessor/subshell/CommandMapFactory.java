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
import com.cloudera.bundleprocessor.subshell.command.hybrid.AppAttempts;
import com.cloudera.bundleprocessor.subshell.command.hybrid.Grep;
import com.cloudera.bundleprocessor.subshell.command.parameterized.Applications;
import com.cloudera.bundleprocessor.subshell.command.parameterized.Containers;
import com.cloudera.bundleprocessor.subshell.command.parameterized.Events;
import com.cloudera.bundleprocessor.subshell.command.parameterized.Exceptions;
import com.cloudera.bundleprocessor.subshell.command.parameterized.Resources;
import com.cloudera.bundleprocessor.subshell.command.parameterized.States;
import com.cloudera.bundleprocessor.subshell.command.primitive.ExitCommand;
import com.cloudera.bundleprocessor.subshell.command.primitive.HelpCommand;
import com.cloudera.bundleprocessor.subshell.command.simple.Info;
import com.cloudera.bundleprocessor.subshell.command.simple.Roles;
import com.cloudera.bundleprocessor.subshell.context.Context;

import java.util.HashMap;
import java.util.Map;

public final class CommandMapFactory {

  private CommandMapFactory() {
  }

  /**
   * Creates a Map with keys of the String representation
   * of commands and with values of Command classes.
   * String representation is what should be written in the CLI
   * to execute the command.
   * Command class is the java object
   * which will be executed if the String is written in the CLI.
   *
   * @param context defines global constant values
   * @return the Command Map
   */
  public static Map<String, Command> createCommandMap(Context context) {
    if (context.getSearchEngine() == null) {
      throw new RuntimeException("No search engine was provided.");
    }
    HelpCommand help = new HelpCommand();
    Map<String, Command> commandMap = createCommandMap(help,
        new ExitCommand(),
        new Roles(context),
        new Applications(context),
        new AppAttempts(context),
        new Containers(context),
        new Events(context),
        new States(context),
        new Grep(context),
        new Resources(context),
        new Exceptions(context),
        new Info(context));
    help.setCommands(commandMap);
    return commandMap;
  }

  static Map<String, Command> createCommandMap(Command... commands) {
    Map<String, Command> commandMap = new HashMap<>();
    for (Command command : commands) {
      commandMap.put(command.getName(), command);
    }
    return commandMap;
  }
}
