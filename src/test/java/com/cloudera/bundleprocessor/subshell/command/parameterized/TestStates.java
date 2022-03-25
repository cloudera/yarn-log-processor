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

import com.cloudera.bundleprocessor.subshell.command.TestCommand;
import com.cloudera.bundleprocessor.subshell.command.util.RegexElements;
import com.cloudera.bundleprocessor.subshell.command.util.TableChecker;
import com.cloudera.bundleprocessor.subshell.format.Printable;
import com.cloudera.bundleprocessor.util.ExpandableLinesOfLogs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class TestStates extends TestCommand {

  /**
   * Instantiate the command object and clears up files from the filesystem.
   *
   * @throws IOException if we don't have right to delete from filesystem
   */
  @Before
  public void setUp() throws IOException {
    prepareWorkspace();
    command = new States(context);
    header = new String[]{"TIME", "FROM STATE", "TO STATE"};
  }

  @Test
  public void testSimpleFields() {
    testSimpleFields("states");
  }

  @Test
  public void testEmpty() {
    String firstApp = RegexElements.generateAppName("1599745644179_0001");
    String[] parameters = new String[]{"--application", firstApp};
    testEmpty(parameters);
  }

  @Test
  public void testOutput() {
    String[] expectedHeader = new String[]{"TIME", "FROM STATE", "TO STATE"};
    String firstApp = RegexElements.generateAppName("1599745644179_0001");
    String fromState = "FROM";
    String toState = "TO";
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addLineWithAppEvent(firstApp, fromState, toState, "RANDOM"));
    context.setupSearchEngine(workspace);
    Printable printable = command.generatePrintable(
        new String[]{"--application", firstApp});
    List<String[]> expectedRows = Collections.singletonList(
        new String[]{ExpandableLinesOfLogs.getTIMESTAMP(), fromState, toState});
    TableChecker.check(printable, expectedHeader, expectedRows);
  }

  @After
  public void clearUpFiles() throws IOException {
    emptyWorkspace();
  }
}
