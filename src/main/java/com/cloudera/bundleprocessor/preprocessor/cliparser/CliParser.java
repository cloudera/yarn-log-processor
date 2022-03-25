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

package com.cloudera.bundleprocessor.preprocessor.cliparser;

import com.cloudera.bundleprocessor.OptionParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * CliParser is responsible to parse arguments from the command line
 * If CliParser's {@code read()} function finishes without an error,
 * the user input is validated.
 */
public class CliParser {

  private static final Logger LOG =
      LoggerFactory.getLogger(CliParser.class);
  private final OptionParser optionParser;

  public CliParser() {
    optionParser = createOptionFields();
  }

  public InputParams read(String[] args)
      throws IllegalArgumentException, ParseException {
    return parseOptionValues(args);
  }

  private OptionParser createOptionFields() {
    LOG.debug("Start creating options... ");
    OptionParser.Builder optionParserBuilder = new OptionParser.Builder();
    optionParserBuilder.setCommandName("CliParser");
    optionParserBuilder.addOption(
        "o", "logFolder", true, "output path to extract diag bundle", true);
    optionParserBuilder.addOption(
        "l", "local", true,
        "path for the diag bundle zip in local repository", false);
    optionParserBuilder.addOption(
        "u", "url", true, "direct url path for the archive", false);
    optionParserBuilder.addOption(
        "k", "keep", false,
        "whether to keep the original archive file", false);
    optionParserBuilder.addOption(
        "s", "shell", false,
        "whether we want to launch a subshell to analyze log data", false);
    optionParserBuilder.addOption(
        "c", "command", true, "defines one command to run", false);
    return optionParserBuilder.build();
  }

  private InputParams parseOptionValues(String[] args)
      throws IllegalArgumentException, ParseException {
    LOG.debug("Start parsing option values... ");
    optionParser.parse(args);
    InputParams.Builder builder = new InputParams.Builder();
    String logFolder = optionParser.getParameter("logFolder");
    builder.withMainDirectory(new File(logFolder));
    final String localFileStr = optionParser.getParameter("local");
    final String directUrlStr = optionParser.getParameter("url");
    final String commandStr = optionParser.getParameter("command");
    if (localFileStr != null) {
      builder.withLocalFile(new File(localFileStr));
    }
    if (directUrlStr != null) {
      builder.withDirectUrl(urlGenerator(directUrlStr));

    }
    if (commandStr != null) {
      builder.withCommand(commandStr);
    }
    final boolean isKeepingOriginalFile = optionParser.checkParameter("keep");
    final boolean isWithShell = optionParser.checkParameter("shell");
    return builder.withKeepOriginalFile(isKeepingOriginalFile)
        .withShell(isWithShell)
        .build();
  }


  private URL urlGenerator(String url) throws IllegalArgumentException {
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(
          "The input URL address was malformed. " + e);
    }
  }


  public void printHelpMessage() {
    optionParser.printHelp();
  }
}
