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

package com.cloudera.bundleprocessor;

import com.cloudera.bundleprocessor.console.ConsoleWriter;
import com.cloudera.bundleprocessor.preprocessor.PreProcessor;
import com.cloudera.bundleprocessor.subshell.CommandExecutor;
import com.cloudera.bundleprocessor.subshell.CommandLine;
import com.cloudera.bundleprocessor.subshell.CommandMapFactory;
import com.cloudera.bundleprocessor.subshell.Subshell;
import com.cloudera.bundleprocessor.subshell.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class App {

  private static final Logger LOG =
      LoggerFactory.getLogger(App.class);
  private static final String NAME_OF_THE_SHELL = "shell";

  private App() {
  }

  /**
   * This function is the entrypoint for the tool.
   *
   * @param args the args from CLI
   */
  public static void main(String[] args) {

    LOG.info("Bundle Log Processor was launched");
    Context context = readConfigFile();
    preprocess(args, context);
    launchSubshell(context);
  }

  private static Context readConfigFile() {
    try {
      return new Context();
    } catch (RuntimeException e) {
      ConsoleWriter.CONSOLE.error(
          "An exception occurred while trying to read the configuration file",
          e);
      System.exit(1);
      return null;
    }
  }

  private static void preprocess(String[] args, Context context) {
    try {
      PreProcessor processor = new PreProcessor(context);
      processor.process(args);
    } catch (RuntimeException e) {
      ConsoleWriter.CONSOLE.error(
          "An exception occurred while trying to process parameters from CLI",
          e);
      System.exit(1);
    }
  }

  private static void launchSubshell(Context context) {
    CommandExecutor commandExecutor =
        new CommandExecutor(CommandMapFactory.createCommandMap(context));
    CommandLine commandLine =
        new CommandLine(NAME_OF_THE_SHELL, commandExecutor.getCommandMap());
    Subshell subshell = new Subshell(commandLine, commandExecutor);
    try {
      subshell.init(context.getSearchIntent());
    } catch (IOException e) {
      ConsoleWriter.CONSOLE.error("Terminal could not be launched", e);
      System.exit(1);
    }
    subshell.run();
  }
}
