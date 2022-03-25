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

import com.cloudera.bundleprocessor.subshell.command.TestCommand;
import com.cloudera.bundleprocessor.subshell.command.util.ColumnChecker;
import com.cloudera.bundleprocessor.subshell.command.util.RegexElements;
import com.cloudera.bundleprocessor.subshell.format.Printable;
import com.cloudera.bundleprocessor.util.ExpandableLinesOfLogs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestAppAttempts extends TestCommand {

  private static final String[] APPLICATION_NUMBERS =
      new String[]{"1599744294941_0001", "1599742435441_0012"};
  private static final String HEADER_STRING = "ATTEMPT ID";

  /**
   * Instantiate the command object and clears up files from the filesystem.
   *
   * @throws IOException if we don't have right to delete from filesystem
   */
  @Before
  public void setUp() throws IOException {
    prepareWorkspace();
    command = new AppAttempts(context);
    header = new String[]{HEADER_STRING};
  }

  @Test
  public void testSimpleFields() {
    testSimpleFields("appattempts");
  }

  @Test
  public void testEmpty() {
    String[] parameters = new String[]{generateAppName(0)};
    testEmpty(parameters);
  }


  @Test
  public void testSingleMatchingAttempt() {
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addLineWithAppAttempt(generateAppAttemptName(0, 1)));
    context.setupSearchEngine(workspace);
    Printable printable = command.generatePrintable(
        new String[]{generateAppName(0)});
    String[] expectedRows = new String[]{generateAppAttemptName(0, 1)};
    ColumnChecker.check(printable, HEADER_STRING, expectedRows);
  }

  @Test
  public void testSingleNonMatchingAttempt() {
    // The appattempt found in the logs belongs to another application
    //  It should be neglected
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addLineWithAppAttempt(generateAppAttemptName(0, 1)));
    context.setupSearchEngine(workspace);
    Printable printable = command.generatePrintable(
        new String[]{generateAppName(1)});
    String[] expectedRows = new String[]{};
    ColumnChecker.check(printable, HEADER_STRING, expectedRows);
  }

  @Test
  public void testMultipleFoundingAndNeglecting() {
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addLineWithAppAttempt(generateAppAttemptName(0, 1))
        .addLineWithAppAttempt(generateAppAttemptName(0, 2))
        .addLineWithAppAttempt(generateAppAttemptName(1, 1))
        .addLineWithAppAttempt(generateAppAttemptName(1, 2)));
    context.setupSearchEngine(workspace);
    Printable printable = command.generatePrintable(
        new String[]{generateAppName(0)});
    String[] expectedRows = new String[]{generateAppAttemptName(0, 1),
        generateAppAttemptName(0, 2)};
    ColumnChecker.check(printable, HEADER_STRING, expectedRows);
  }

  @Test
  public void testMultipleFiles() {
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addLineWithAppAttempt(generateAppAttemptName(0, 1)));
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addLineWithAppAttempt(generateAppAttemptName(0, 2)));
    context.setupSearchEngine(workspace);
    Printable printable = command.generatePrintable(
        new String[]{generateAppName(0)});
    String[] expectedRows = new String[]{generateAppAttemptName(0, 1),
        generateAppAttemptName(0, 2)};
    ColumnChecker.check(printable, HEADER_STRING, expectedRows);
  }

  public String generateAppName(int appIndex) {
    return RegexElements.generateAppName(APPLICATION_NUMBERS[appIndex]);
  }

  public String generateAppAttemptName(int appIndex, int attemptIndex) {
    return RegexElements.generateAppAttemptName(
        APPLICATION_NUMBERS[appIndex], attemptIndex);
  }

  @After
  public void clearUpFiles() throws IOException {
    emptyWorkspace();
  }
}
