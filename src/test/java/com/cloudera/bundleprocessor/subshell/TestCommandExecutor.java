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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cloudera.bundleprocessor.subshell.command.Command;
import com.cloudera.bundleprocessor.subshell.format.StringPrintable;
import java.util.HashMap;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCommandExecutor {

  private static final String[] PARAMETERS = new String[]{"param1", "param2"};
  private static CommandExecutor commandExecutor;
  private static Command firstCommand;
  private static Command secondCommand;

  /**
   * Setting up CommandExecutor with 2 Commands.
   */
  @BeforeClass
  public static void setUp() {
    firstCommand = createCommand("first", true);
    secondCommand = createCommand("second", false);
    commandExecutor = createExecutor(firstCommand, secondCommand);
  }

  private static CommandExecutor createExecutor(Command... commands) {
    Map<String, Command> commandMap = new HashMap<>();
    for (Command command : commands) {
      commandMap.put(command.getName(), command);
    }
    return new CommandExecutor(commandMap);
  }

  private static Command createCommand(String name, boolean readMore) {
    Command command = mock(Command.class);
    when(command.getName()).thenReturn(name);
    when(command.readMore()).thenReturn(readMore);
    when(command.generatePrintable(any())).thenReturn(
        new StringPrintable(name));
    return command;
  }

  @Test
  public void testNonExistentCommand() {
    try {
      String cliInput = "non-existent command param1 param2";
      commandExecutor.executeCommand(cliInput);
      fail("CommandExecutor should have failed with non-existent command.");
    } catch (UnknownCommandException expected) {
    }
  }

  @Test
  public void testFirstCommand() {
    try {
      String cliInput = firstCommand.getName() + "   param1 param2";
      String actualOutput = commandExecutor.executeCommand(cliInput);
      verify(firstCommand).generatePrintable(PARAMETERS);
      String expectedOutput =
          firstCommand.generatePrintable(PARAMETERS).print();
      assertEquals(expectedOutput, actualOutput);
      assertEquals(firstCommand.readMore(), commandExecutor.isReadingMore());
    } catch (UnknownCommandException e) {
      fail("CommandExecutor failed to recognize the first command.");
    }
  }

  @Test
  public void testSecondCommand() {
    try {
      String cliInput = secondCommand.getName() + " param1   param2";
      String actualOutput = commandExecutor.executeCommand(cliInput);
      verify(secondCommand).generatePrintable(PARAMETERS);
      String expectedOutput =
          secondCommand.generatePrintable(PARAMETERS).print();
      assertEquals(expectedOutput, actualOutput);
      assertEquals(secondCommand.readMore(), commandExecutor.isReadingMore());
    } catch (UnknownCommandException e) {
      fail("CommandExecutor failed to recognize the second command.");
    }
  }
}
