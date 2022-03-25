// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.cloudera.bundleprocessor.subshell.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.cloudera.bundleprocessor.Constants;
import com.cloudera.bundleprocessor.preprocessor.fileprocessor.FileUtils;
import com.cloudera.bundleprocessor.subshell.command.util.AutoCompleterWrapper;
import com.cloudera.bundleprocessor.subshell.command.util.ColumnChecker;
import com.cloudera.bundleprocessor.subshell.command.util.TableChecker;
import com.cloudera.bundleprocessor.subshell.context.Context;
import com.cloudera.bundleprocessor.subshell.context.SearchIntent;
import com.cloudera.bundleprocessor.subshell.format.Printable;
import com.cloudera.bundleprocessor.util.LogFolder;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class TestCommand {

  protected File workspace;
  protected Context context;
  protected LogFolder logFolder;
  protected Command command;
  protected String[] header;

  protected void prepareWorkspace() throws IOException {
    workspace = new File(Constants.TEMPORARY_FOLDER, "testCommand");
    context = new Context();
    File logDir = context.getConfig().getLogDir(workspace);
    logFolder = new LogFolder(logDir);
    FileUtils.emptyDirectory(logDir.getAbsolutePath());
    context.setSearchIntent(
        new SearchIntent.Builder().withLaunchingShell(true).build());
    logFolder.emptyDir();
  }

  protected void testSimpleFields(String commandName) {
    assertEquals(commandName, command.getName());
    assertNotNull("expected a non null message for the description",
        command.getDescription());
    assertTrue(command.readMore());

    AutoCompleterWrapper autoCompleter = command.createAutoCompleterWrapper();
    assertNotNull(autoCompleter);
    assertEquals(commandName, autoCompleter.getCommandName());
  }

  protected void testEmpty(String[] parameters) {
    Printable printable = generateOutput(parameters);
    if (header.length == 1) { // If output is a Column
      String expectedHeader = "Nothing to display";
      String[] expectedRows = new String[]{};
      ColumnChecker.check(printable, expectedHeader, expectedRows);
    } else { // If output is a Table
      List<String[]> expectedRows = Collections.emptyList();
      TableChecker.check(printable, header, expectedRows);
    }
  }

  protected Printable generateOutput(String[] parameters) {
    context.setupSearchEngine(workspace);
    return command.generatePrintable(parameters);
  }

  protected void emptyWorkspace() throws IOException {
    logFolder.emptyDir();
  }
}
