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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cloudera.bundleprocessor.subshell.command.Command;
import com.cloudera.bundleprocessor.subshell.command.simple.Info;
import com.cloudera.bundleprocessor.subshell.context.SearchIntent;
import com.cloudera.bundleprocessor.subshell.format.Printable;
import com.cloudera.bundleprocessor.subshell.format.StringPrintable;
import java.util.HashMap;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class TestSubshell {

  private static final CommandLine COMMAND_LINE = mock(CommandLine.class);
  private static final String OUTPUT_OF_INFO = "Output of info";
  private static Subshell subshell;
  private final Appender appender = mock(Appender.class);
  private final Logger logger = Logger.getRootLogger();

  /**
   * Setting up Subshell with a mocked command class.
   */
  @BeforeClass
  public static void setUp() {
    Info infoCommand = mock(Info.class);
    Printable output = new StringPrintable(OUTPUT_OF_INFO);
    when(infoCommand.generatePrintable(any())).thenReturn(output);
    CommandExecutor commandExecutor =
        new CommandExecutor(new HashMap<String, Command>() {
          {
            put("info", infoCommand);
          }
        });
    subshell = new Subshell(COMMAND_LINE, commandExecutor);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidSearchIntent() {
    //This is already tested in testSearchIntent,
    // so we might as well remove it from here
    SearchIntent invalidSearchIntent = new SearchIntent.Builder()
        .withCommand("some command")
        .withLaunchingShell(true)
        .build();
  }

  @Test
  public void testInvalidSingleCommand() throws Exception {
    logger.addAppender(appender);
    SearchIntent searchIntent = new SearchIntent.Builder()
        .withCommand("invalid command")
        .build();
    subshell.init(searchIntent);
    subshell.run();

    ArgumentCaptor<LoggingEvent> argument =
        ArgumentCaptor.forClass(LoggingEvent.class);
    verify(appender).doAppend(argument.capture());
    assertEquals("Error message differs",
        "The command after parameter --command was invalid",
        argument.getValue().getMessage());
  }

  @Test
  public void testValidSingleCommand() throws Exception {
    logger.addAppender(appender);
    SearchIntent searchIntent = new SearchIntent.Builder()
        .withCommand("info")
        .build();
    subshell.init(searchIntent);
    subshell.run();

    ArgumentCaptor<LoggingEvent> argument =
        ArgumentCaptor.forClass(LoggingEvent.class);
    verify(appender).doAppend(argument.capture());
    assertEquals("Error message differs", OUTPUT_OF_INFO,
        argument.getValue().getMessage());
  }
}
