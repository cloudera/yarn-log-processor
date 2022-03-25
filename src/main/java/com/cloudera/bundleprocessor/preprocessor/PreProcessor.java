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

package com.cloudera.bundleprocessor.preprocessor;

import com.cloudera.bundleprocessor.Constants;
import com.cloudera.bundleprocessor.console.ConsoleWriter;
import com.cloudera.bundleprocessor.preprocessor.cliparser.CliParser;
import com.cloudera.bundleprocessor.preprocessor.cliparser.InputParams;
import com.cloudera.bundleprocessor.preprocessor.exception.WrongInputTypeException;
import com.cloudera.bundleprocessor.preprocessor.fileprocessor.FileExtractor;
import com.cloudera.bundleprocessor.preprocessor.fileprocessor.FileFilter;
import com.cloudera.bundleprocessor.preprocessor.fileprocessor.FileUtils;
import com.cloudera.bundleprocessor.preprocessor.inputprocessor.InputProcessor;
import com.cloudera.bundleprocessor.subshell.context.Config;
import com.cloudera.bundleprocessor.subshell.context.Context;

import java.io.File;
import java.io.IOException;

/**
 * PreProcessor is the main class for the preprocessing part of the application.
 * PreProcessor parses arguments from CLI and (if no error occurs) provides
 * a folder with filtered out log files,
 * which can be further analyzed.
 */
public class PreProcessor {

  private final Config config;
  private final Context context;
  private InputParams inputParams;
  private InputProcessor inputProcessor;
  private File mainDirectory;
  private File directoryForBundle;

  public PreProcessor(Context context) throws RuntimeException {
    this.context = context;
    this.config = context.getConfig();
  }

  /**
   * {@code process()} method prepares a folder with filtered out log files.
   * Firstly, it reads the arguments from CLI to determine
   * the input source and other parameters.
   * Then it processes the input which can be an URL or a local archive or
   * an already preprocessed log folder.
   * Lastly, it saves the needed variables in the Context object,
   * which is also the return object of the function.
   *
   * @param args the parameters parsed from the CLI
   * @return Context object where the SearchEngine
   * and the SearchIntent are saved
   * @throws RuntimeException if the CLI input (args) was invalid
   *                          or if we don`t have the adequate rights
   *                          to read and write on the disk
   */
  public Context process(final String[] args) throws RuntimeException {
    parseParameters(args);
    if (inputProcessor != null) {
      // one of the input types (URL address or local file path)
      // is provided
      File inputFile = getOrCreateOriginalArchive(inputProcessor, config);
      directoryForBundle = setDirectoryForBundle(inputFile);
      File subDirectoryForExtractedFiles =
          extractLogsIntoSubDirectory(directoryForBundle, inputFile);
      filterOutLogsIntoSubDirectory(
          directoryForBundle, subDirectoryForExtractedFiles);
      context.setupSearchEngine(returnSubDirectory());
    } else {
      // none of of the input types
      // (URL address or local file path)
      // are provided
      context.setupSearchEngine(returnMainDirectory());
    }
    context.setSearchIntent(inputParams.getSearchIntent());
    return context;
  }

  private void parseParameters(final String[] args) {
    CliParser cliParser = new CliParser();
    try {
      this.inputParams = cliParser.read(args);
      this.inputProcessor = inputParams.getProcessor();
      this.mainDirectory = inputParams.getMainDirectory();
    } catch (Exception e) {
      cliParser.printHelpMessage();
      throw new RuntimeException(
          "An error occurred while parsing the arguments... ", e);
    }
  }

  private File getOrCreateOriginalArchive(
      InputProcessor inputProcessor, Config config) {
    try {
      return inputProcessor.process(config);
    } catch (IllegalArgumentException | IOException e) {
      throw new RuntimeException(
          "An error occurred during processing the input parameter.", e);
    }
  }

  private File setDirectoryForBundle(File originalArchive) {
    try {
      String nameOfDirectoryForBundle =
          FileUtils.cutExtension(
              originalArchive.getName(), Constants.ZIP_EXTENSION);
      return new File(this.mainDirectory, nameOfDirectoryForBundle);
    } catch (WrongInputTypeException e) {
      throw new RuntimeException(
          "Received input file is not a zip archive.", e);
    }
  }

  private File extractLogsIntoSubDirectory(
      File directoryForBundle, File originalArchive) {
    try {
      boolean keepOriginalFile = this.inputParams.getKeepOriginalFile();
      FileExtractor fileExtractor = new FileExtractor(directoryForBundle);
      return fileExtractor.extract(originalArchive, keepOriginalFile);
    } catch (IOException | WrongInputTypeException e) {
      throw new RuntimeException("An exception occurred during " +
          "the extraction of the diagnostic bundle.", e);
    }
  }

  private void filterOutLogsIntoSubDirectory(
      File directoryForBundle, File subDirectoryForExtractedFiles) {
    try {
      String directoryNameForYarnRelatedLogs =
          config.getDirs().getDirectoryNameForYarnRelatedLogs();
      File directoryOfFilteredFiles =
          new File(directoryForBundle, directoryNameForYarnRelatedLogs);
      FileFilter fileFilter =
          new FileFilter(subDirectoryForExtractedFiles,
              directoryOfFilteredFiles);
      fileFilter.filter(config.getRegexes().getLogFile(),
          config.getDirs().getSubdirectoryNameForNodeLogs());
      fileFilter.filter(config.getRegexes().getConfigFile(),
          config.getDirs().getSubdirectoryNameForConfigFiles());
    } catch (IOException e) {
      throw new RuntimeException(
          "An exception occurred during filtering the files.", e);
    }
  }

  private File returnSubDirectory() {
    ConsoleWriter.CONSOLE.info("The preprocessing phase is finished, "
        + "and the subshell is also requested to be launched.\n"
        + "Subshell will be launched on the just preprocessed bundle.");
    return directoryForBundle;
  }

  private File returnMainDirectory() {
    ConsoleWriter.CONSOLE.info("No input parameter is provided, "
        + "but the subshell is requested to be launched.\n"
        + "An already extracted bundle is expected "
        + "in the user-defined log folder");
    return mainDirectory;
  }
}
