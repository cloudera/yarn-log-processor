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


package com.cloudera.bundleprocessor.subshell.command.primitive;

import com.cloudera.bundleprocessor.subshell.command.Command;
import com.cloudera.bundleprocessor.subshell.command.TestCommand;
import com.cloudera.bundleprocessor.subshell.command.parameterized.Applications;
import com.cloudera.bundleprocessor.subshell.command.util.TableChecker;
import com.cloudera.bundleprocessor.subshell.context.Context;
import com.cloudera.bundleprocessor.subshell.format.Printable;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestHelp extends TestCommand {

  private static final String[] EXPECTED_HEADER =
      new String[]{"COMMAND", "DESCRIPTION"};
  private final HelpCommand help = new HelpCommand();

  @Before
  public void setUp() {
    command = help;
  }

  @Test
  public void testSimpleFields() {
    testSimpleFields("help");
  }

  @Test
  public void testOutput() {
    Context context = new Context();
    Map<String, Command> commandMap = new HashMap<>();
    Command applications = new Applications(context);
    commandMap.put(applications.getName(), applications);
    help.setCommands(commandMap);
    Printable printable = help.generatePrintable(new String[]{});
    String[] expectedRows =
        new String[]{applications.getName(), applications.getDescription()};
    TableChecker.check(
        printable, EXPECTED_HEADER, Collections.singletonList(expectedRows));
  }
}
