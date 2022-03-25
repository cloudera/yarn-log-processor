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

import com.cloudera.bundleprocessor.OptionParser;
import com.cloudera.bundleprocessor.console.ConsoleWriter;
import com.cloudera.bundleprocessor.subshell.command.Command;
import com.cloudera.bundleprocessor.subshell.command.ParsingSearchCommand;
import com.cloudera.bundleprocessor.subshell.context.Context;
import com.cloudera.bundleprocessor.subshell.format.EmptyPrintable;
import com.cloudera.bundleprocessor.subshell.format.Printable;
import com.cloudera.bundleprocessor.subshell.search.request.Executable;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;

/**
 * {@code HybridSearch} abstract class is a {@link Command} class
 * for the CLI command with parameter(s).
 * {@code HybridSearch} objects read the first parameter on the CLI
 * and parse the other parameters trough a {@code OptionParser} object
 * their output is depending on both the first parameter and
 * the ones read trough {@code OptionParser}
 */
public abstract class HybridSearch extends ParsingSearchCommand {

  protected HybridSearch(Context context) {
    super(context);
  }

  @Override
  public Printable generatePrintable(String[] parameters) {
    if (parameters.length < 1) {
      ConsoleWriter.CONSOLE.error(
          "No input parameter was provided for the Command");
      return new EmptyPrintable();
    }
    String firstParameter = parameters[0];
    parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
    OptionParser optionParser;
    try {
      optionParser = initializeOptionParser(parameters);
    } catch (ParseException e) {
      return new EmptyPrintable();
    }
    if (firstParameter.equals("--help") || firstParameter.equals("-h")) {
      optionParser.printHelp();
      return new EmptyPrintable();
    } else {
      return createAndExecute(firstParameter, optionParser);
    }
  }

  private Printable createAndExecute(
      String firstParameter, OptionParser optionParser) {
    try {
      Executable executable = createExecutable(firstParameter, optionParser);
      return execute(executable);
    } catch (IllegalArgumentException e) {
      ConsoleWriter.CONSOLE.error(
          "The parameters specified for the command were invalid");
      optionParser.printHelp();
      return new EmptyPrintable();
    }
  }

  /**
   * {@code createExecutable()} creates a Executable object depending
   * on an OptionParser and the first parameter.
   *
   * @param firstParameter is parsed outside the optionParser,
   *                       is essential information
   * @param optionParser   contains the user defined parameters
   * @return Executable contains the details how we will find
   * the information in the log files
   */
  protected abstract Executable createExecutable(
      String firstParameter, OptionParser optionParser);
}
