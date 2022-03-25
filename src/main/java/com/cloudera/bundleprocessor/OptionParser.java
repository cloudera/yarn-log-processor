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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * OptionParser is capable of parsing options from CLI.
 */
public final class OptionParser {

  private final String commandName;
  private final Options options;
  private CommandLine cmd;

  private OptionParser(Builder builder) {
    commandName = builder.commandName;
    options = builder.options;
  }

  public void parse(String[] cliInput) throws ParseException {
    CommandLineParser parser = new DefaultParser();
    cmd = parser.parse(options, cliInput);
  }

  /**
   * {@code getParameter()} reads String option from CLI.
   *
   * @param parameter name of the parameter
   * @return value of the parameter
   */
  public String getParameter(String parameter) {
    return cmd.getOptionValue(parameter);
  }

  /**
   * {@code getParameter()} reads Boolean option from CLI.
   *
   * @param parameter name of the parameter
   * @return value of the parameter
   */
  public boolean checkParameter(String parameter) {
    return cmd.hasOption(parameter);
  }

  public String getCommandName() {
    return commandName;
  }

  public Options getOptionsObject() {
    return options;
  }

  public void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(commandName, options);
  }

  public static class Builder {

    private final Options options = new Options();
    private String commandName;

    public Builder() {
    }

    public Builder setCommandName(String commandName) {
      this.commandName = commandName;
      return this;
    }

    /**
     * {@code addOption()} creates an Option with the defined parameters and
     * adds it to the Options object.
     * Usage: --{@code opt} {@code value}
     *
     * @param opt         short name for option
     * @param longOpt     long name for option
     * @param hasArg      if argument is required
     * @param description description of the option
     * @param setRequired if option is required
     */
    public Builder addOption(String opt, String longOpt, boolean hasArg,
                             String description, boolean setRequired) {
      Option option = Option.builder(opt)
          .longOpt(longOpt)
          .desc(description)
          .hasArg(hasArg)
          .required(setRequired)
          .build();
      options.addOption(option);
      return this;
    }

    /**
     * {@code addDefaultOptions()} adds raw, verbose and
     * list option to the Builder.
     */
    public Builder addDefaultOptions() {
      return this.addOption("h", "help", false,
          "display the valid subcommands of the command", false)
          .addOption("r", "raw", false,
              "display matched lines without filtering out information", false)
          .addOption("v", "verbose", false,
              "display extra information", false)
          .addOption("l", "list", false,
              "display results in list", false);
    }

    public OptionParser build() {
      return new OptionParser(this);
    }
  }
}
