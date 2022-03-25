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
import com.cloudera.bundleprocessor.subshell.command.util.TableChecker;
import com.cloudera.bundleprocessor.subshell.format.Printable;
import com.cloudera.bundleprocessor.util.ExpandableLinesOfLogs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class TestExceptions extends TestCommand {

  /**
   * Instantiate the command object and clears up files from the filesystem.
   *
   * @throws IOException if we don't have right to delete from filesystem
   */
  @Before
  public void setUp() throws IOException {
    prepareWorkspace();
    command = new Exceptions(context);
    header = new String[]{"TIME", "EXCEPTION"};
  }

  @Test
  public void testSimpleFields() {
    testSimpleFields("exceptions");
  }

  @Test
  public void testEmpty() {
    String[] parameters = new String[]{};
    testEmpty(parameters);
  }

  @Test
  public void testOutput() {
    String[] expectedHeader = new String[]{"TIME", "EXCEPTION"};
    String timeStamp = "2020-09-10 13:47:23";
    String exception = "org.apache.curator.CuratorConnectionLossException";
    String exceptionText = String.format(
        "%s,093 ERROR org.apache.curator.ConnectionState:" +
            " Connection timed out for connection string " +
            "(quasar-twhzbx-1.quasar-twhzbx.root.hwx.site:2181)" +
            " and timeout (15000) / elapsed (18072)\n" +
            "%s: KeeperErrorCode = ConnectionLoss\n\t" +
            "at org.apache.curator.ConnectionState." +
            "checkTimeouts(ConnectionState.java:225)",
        timeStamp, exception);
    logFolder.addResourceManager(new ExpandableLinesOfLogs()
        .addAString(exceptionText));
    context.setupSearchEngine(workspace);
    Printable printable = command.generatePrintable(new String[]{});
    List<String[]> expectedRows = Collections.singletonList(
        new String[]{timeStamp, exception});
    TableChecker.check(printable, expectedHeader, expectedRows);
  }

  @After
  public void clearUpFiles() throws IOException {
    emptyWorkspace();
  }
}
