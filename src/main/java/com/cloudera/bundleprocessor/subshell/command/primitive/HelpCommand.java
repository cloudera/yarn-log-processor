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
import com.cloudera.bundleprocessor.subshell.command.util.AutoCompleterWrapper;
import com.cloudera.bundleprocessor.subshell.format.RowList;
import com.cloudera.bundleprocessor.subshell.format.RowListFactory;

import java.util.Map;

/**
 * HelpCommand is a {@link Command} class for the command ("help").
 */
public class HelpCommand implements Command {

  private Map<String, Command> commandMap;

  public void setCommands(Map<String, Command> commandMap) {
    this.commandMap = commandMap;
  }

  @Override
  public RowList generatePrintable(String[] parameters) {
    String[] header = new String[]{"COMMAND", "DESCRIPTION"};
    RowList rowList = RowListFactory.createRowList(header);
    for (Map.Entry<String, Command> entry : commandMap.entrySet()) {
      String[] row = new String[]{entry.getKey(),
          entry.getValue().getDescription()};
      rowList.addRow(row);
    }
    return rowList;
  }

  @Override
  public AutoCompleterWrapper createAutoCompleterWrapper() {
    return new AutoCompleterWrapper(this.getName());
  }

  @Override
  public String getName() {
    return "help";
  }

  @Override
  public String getDescription() {
    return "lists all valid commands operating in the subshell and" +
        " their expected behaviour";
  }

  @Override
  public boolean readMore() {
    return true;
  }
}
