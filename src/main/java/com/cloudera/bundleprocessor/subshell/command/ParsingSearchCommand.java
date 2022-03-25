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

package com.cloudera.bundleprocessor.subshell.command;

import com.cloudera.bundleprocessor.OptionParser;
import com.cloudera.bundleprocessor.console.ConsoleWriter;
import com.cloudera.bundleprocessor.subshell.command.util.AutoCompleterWrapper;
import com.cloudera.bundleprocessor.subshell.context.Context;
import org.apache.commons.cli.ParseException;

public abstract class ParsingSearchCommand extends SearchCommand {

  protected ParsingSearchCommand(Context context) {
    super(context);
  }

  /**
   * ParsingSearchCommand expect parameter on CLI and
   * this function parses those parameters.
   *
   * @param parameters user-defined parameters
   * @return OptionParser initialized with the user-defined parameters
   * @throws ParseException if an exception occurs
   */
  protected OptionParser initializeOptionParser(String[] parameters)
      throws ParseException {
    OptionParser optionParser = createOptionParser();
    try {
      optionParser.parse(parameters);
    } catch (ParseException e) {
      ConsoleWriter.CONSOLE.error(
          "Exception occurred during the parsing process", e);
      optionParser.printHelp();
      throw new ParseException(e.getMessage());
    }
    return optionParser;
  }

  @Override
  public AutoCompleterWrapper createAutoCompleterWrapper() {
    return new AutoCompleterWrapper(createOptionParser());
  }

  protected abstract OptionParser createOptionParser();
}
