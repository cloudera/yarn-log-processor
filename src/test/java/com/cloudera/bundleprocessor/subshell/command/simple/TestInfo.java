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

import com.cloudera.bundleprocessor.subshell.command.TestCommand;
import com.cloudera.bundleprocessor.util.ExpandableLinesOfLogs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestInfo extends TestCommand {

  /**
   * Instantiate the command object and clears up files from the filesystem.
   *
   * @throws IOException if we don't have right to delete from filesystem
   */
  @Before
  public void setUp() throws IOException {
    prepareWorkspace();
    command = new Info(context);
  }

  @Test
  public void testSimpleFields() {
    testSimpleFields("info");
  }

  @Test
  public void testEmpty() {
    expectValues(null, null, null, null, null);
  }

  @Test
  public void testNodeManagerLog() {
    logFolder.addNodeManager(new ExpandableLinesOfLogs()
        .addLineWithScheduler("FairScheduler")
        .addLineWithResources("inNM", 1, 1000));
    expectValues(0, 1, null, null, null);
  }

  @Test
  public void testSingleRMLog() {
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addLineWithScheduler("FairScheduler")
        .addLineWithResources("node1", 2000, 2)
        .addLineWithResources("node2", 8000, 8));
    expectValues(1, 0, 10000, 10, "FairScheduler");
  }

  @Test
  public void testMultipleRMLogs() {
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addLineWithScheduler("FairScheduler")
        .addLineWithResources("node1", 2000, 2)
        .addLineWithResources("node2", 8000, 8));
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addLineWithScheduler("FairScheduler")
        .addLineWithResources("node3", 4000, 4));
    expectValues(2, 0, 14000, 14, "FairScheduler");
  }

  @Test
  public void testNodeMultipleTimes() {
    // The resources linked to a specific node should only count once,
    // even if they appear several times in the logs
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addLineWithScheduler("FairScheduler")
        .addLineWithResources("node1", 2000, 2)
        .addLineWithResources("node1", 2000, 2)
        .addLineWithResources("node2", 8000, 8));
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addLineWithResources("node1", 2000, 2)
        .addLineWithResources("node2", 8000, 8));
    expectValues(2, 0, 10000, 10, "FairScheduler");
  }

  private void expectValues(Integer numbOfRM, Integer numbOfNM, Integer memory,
                            Integer numbOfCores, String scheduler) {
    context.setupSearchEngine(workspace);
    String expectedOutput =
        generateInfoOutput(numbOfRM, numbOfNM, memory, numbOfCores, scheduler);
    String actualOutput = command.generatePrintable(new String[]{""}).print();
    assertEquals("The output differs from expected.",
        expectedOutput, actualOutput);
  }

  private String generateInfoOutput(Integer numbOfRM, Integer numbOfNM,
                                    Integer memory, Integer numbOfCores,
                                    String scheduler) {
    String schedulerInfo = "SCHEDULER\n"
        + scheduler + "\n";
    if (scheduler == null) {
      schedulerInfo = "Nothing to display\n";
    }
    return String.format("NODES\n"
        + "  - RESOURCEMANAGER: %s\n"
        + "  - NODEMANAGER: %s\n"
        + "RESOURCE\n"
        + "  - CPU: %s\n"
        + "  - MEMORY: %s\n"
        + "%s", numbOfRM, numbOfNM, numbOfCores, memory, schedulerInfo);
  }

  @After
  public void clearUpFiles() throws IOException {
    emptyWorkspace();
  }
}
