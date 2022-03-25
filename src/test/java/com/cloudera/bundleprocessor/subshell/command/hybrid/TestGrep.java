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
import com.cloudera.bundleprocessor.subshell.format.Printable;
import com.cloudera.bundleprocessor.util.ExpandableLinesOfLogs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestGrep extends TestCommand {

  private static final String HEADER_STRING = "MATCHING EXPRESSIONS";

  /**
   * Instantiate the command object and clears up files from the filesystem.
   *
   * @throws IOException if we don't have right to delete from filesystem
   */
  @Before
  public void setUp() throws IOException {
    prepareWorkspace();
    command = new Grep(context);
    header = new String[]{HEADER_STRING};
  }

  @Test
  public void testSimpleFields() {
    testSimpleFields("grep");
  }

  @Test
  public void testEmpty() {
    String[] parameters = new String[]{"stringToGrep", "--short"};
    testEmpty(parameters);
  }

  @Test
  public void testOutput() {
    String stringToGrep = "stringToGrep";
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addLineWithAString(stringToGrep));
    String[] parameters = new String[]{stringToGrep, "--short"};
    Printable printable = generateOutput(parameters);
    String[] expectedRows = new String[]{stringToGrep};
    ColumnChecker.check(printable, HEADER_STRING, expectedRows);
  }

  @After
  public void clearUpFiles() throws IOException {
    emptyWorkspace();
  }
}
