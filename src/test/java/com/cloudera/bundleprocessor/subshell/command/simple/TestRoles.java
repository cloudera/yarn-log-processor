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

package com.cloudera.bundleprocessor.subshell.command.simple;

import com.cloudera.bundleprocessor.Constants;
import com.cloudera.bundleprocessor.subshell.command.TestCommand;
import com.cloudera.bundleprocessor.subshell.command.util.TableChecker;
import com.cloudera.bundleprocessor.subshell.format.Printable;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestRoles extends TestCommand {


  private static final String[] EXPECTED_HEADER = new String[]{"ROLE", "HOST"};
  private static final String NM_HOST = "NM_HOST";
  private static final String RM_HOST_1 = "RM_HOST_1";
  private static final String RM_HOST_2 = "RM_HOST_2";
  private static final String RM_HOST_3 = "RM_HOST_3";

  /**
   * Instantiate the command object and clears up files from the filesystem.
   *
   * @throws IOException if we don't have right to delete from filesystem
   */
  @Before
  public void setUp() throws IOException {
    prepareWorkspace();
    command = new Roles(context);
  }

  @Test
  public void testSimpleFields() {
    testSimpleFields("roles");
  }

  @Test
  public void testGeneratePrintable() {

    List<String[]> expectedRows = new ArrayList<>();
    // expect empty output with empty log folder
    expectRows(expectedRows);

    // Create a new file in every code block and expect difference outputs

    logFolder.addNodeManager(NM_HOST, null, null);
    expectedRows.add(new String[]{Constants.NODEMANAGER, NM_HOST});
    expectRows(expectedRows);

    logFolder.addResourceManager(RM_HOST_1, null, null);
    expectedRows.add(new String[]{Constants.RESOURCEMANAGER, RM_HOST_1});
    expectRows(expectedRows);

    logFolder.addResourceManager(RM_HOST_2, null, null);
    expectedRows.add(new String[]{Constants.RESOURCEMANAGER, RM_HOST_2});
    expectRows(expectedRows);

    logFolder.addResourceManager(RM_HOST_3, null, null);
    expectedRows.add(new String[]{Constants.RESOURCEMANAGER, RM_HOST_3});
    expectRows(expectedRows);
  }

  private void expectRows(List<String[]> expectedRows) {
    context.setupSearchEngine(workspace);
    Printable printable = command.generatePrintable(new String[]{""});
    TableChecker.check(printable, EXPECTED_HEADER, expectedRows);
  }
}
