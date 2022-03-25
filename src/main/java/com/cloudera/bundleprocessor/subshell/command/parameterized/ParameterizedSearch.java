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

import com.cloudera.bundleprocessor.OptionParser;
import com.cloudera.bundleprocessor.console.ConsoleWriter;
import com.cloudera.bundleprocessor.subshell.command.Command;
import com.cloudera.bundleprocessor.subshell.command.ParsingSearchCommand;
import com.cloudera.bundleprocessor.subshell.context.Context;
import com.cloudera.bundleprocessor.subshell.format.EmptyPrintable;
import com.cloudera.bundleprocessor.subshell.format.Printable;
import com.cloudera.bundleprocessor.subshell.search.request.Executable;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code ParameterizedSearch} abstract class is a {@link Command} class
 * for the CLI command with parameter(s).
 * {@code ParameterizedSearch} objects are parsing parameters
 * trough a {@link OptionParser} object
 * and creating output depending on these parameters
 */
public abstract class ParameterizedSearch extends ParsingSearchCommand {

  private static final Logger LOG =
      LoggerFactory.getLogger(ParameterizedSearch.class);

  protected ParameterizedSearch(Context context) {
    super(context);
  }

  @Override
  public Printable generatePrintable(String[] parameters) {
    OptionParser optionParser;
    try {
      optionParser = initializeOptionParser(parameters);
    } catch (ParseException e) {
      return new EmptyPrintable();
    }
    if (optionParser.checkParameter("help")) {
      optionParser.printHelp();
      return new EmptyPrintable();
    } else {
      return execute(optionParser);
    }
  }

  private Printable execute(OptionParser optionParser) {
    try {
      Executable executable = createExecutable(optionParser);
      return execute(executable);
    } catch (MissingOptionException | IllegalArgumentException e) {
      ConsoleWriter.CONSOLE.error("The parameters specified " +
          "for the command were invalid", e);
      LOG.error("Error during processing", e);
      optionParser.printHelp();
      return new EmptyPrintable();
    }
  }

  /**
   * {@code createExecutable()} creates a SingleExecutable object
   * depending on an OptionParser.
   *
   * @param optionParser contains the user defined parameters
   * @return SingleExecutable contains the details how we will find
   * the information in the log files
   */
  protected abstract Executable createExecutable(OptionParser optionParser)
      throws MissingOptionException;

}
