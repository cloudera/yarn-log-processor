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
import com.cloudera.bundleprocessor.preprocessor.fileprocessor.FileUtils;
import com.cloudera.bundleprocessor.subshell.context.Context;
import com.cloudera.bundleprocessor.subshell.context.SearchIntent;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.LinesOfLogs;
import com.cloudera.bundleprocessor.subshell.search.engine.util.LogManipulator;
import com.cloudera.bundleprocessor.util.LogFolder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test cases in the first (and biggest) group check
 * the existence of the filtered files with the specified filenames.
 * Test cases testing the content of the filtered out files
 * are named "testContent.*".
 * Test cases testing the returned SearchIntent
 * are named "test.*SearchIntent.*".
 * The last group of tests expect RuntimeException due to various reasons.
 */
public class TestPreProcessor {


  private static final LogFolder LOG_FOLDER =
      new LogFolder(new File(Constants.TEMPORARY_FOLDER,
          "preProcessorInput"));
  private static final File OUTPUT_FOLDER =
      new File(Constants.TEMPORARY_FOLDER, "preProcessorOutput");
  private static final String ORIGINAL_ZIP_FILENAME = "zipFile";

  private static Context context;
  private static PreProcessor preProcessor;

  @BeforeClass
  public static void setUp() {
    context = new Context();
    preProcessor = new PreProcessor(context);
  }

  @AfterClass
  public static void deleteDirectories() throws IOException {
    LOG_FOLDER.deleteDir();
    FileUtils.deleteDirectory(OUTPUT_FOLDER.getAbsolutePath());
  }

  @Before
  public void cleanUp() throws IOException {
    LOG_FOLDER.emptyDir();
    FileUtils.emptyDirectory(OUTPUT_FOLDER.getAbsolutePath());
  }

  @Test
  public void testEmpty() {

    processAndCheck(null, null);
  }

  @Test
  public void testNonLogFiles() {
    String[] nonLogFileNames = new String[]{"first", "second"};

    processAndCheck(null, nonLogFileNames);
  }

  @Test
  public void testSingleRmLog() {
    String[] logFileNames =
        new String[]{LOG_FOLDER.generateResourceManagerName("first")};

    processAndCheck(logFileNames, null);
  }

  @Test
  public void testSingleNmLog() {
    String[] logFileNames =
        new String[]{LOG_FOLDER.generateNodeManagerName("first")};

    processAndCheck(logFileNames, null);
  }

  @Test
  public void testNmAndRmLogs() {
    String[] logFileNames =
        new String[]{LOG_FOLDER.generateResourceManagerName("first"),
            LOG_FOLDER.generateNodeManagerName("first")};

    processAndCheck(logFileNames, null);
  }

  @Test
  public void testNmRmAndNonLogs() {
    String[] logFileNames =
        new String[]{LOG_FOLDER.generateResourceManagerName("first"),
            LOG_FOLDER.generateNodeManagerName("first")};
    String[] nonLogFileNames = new String[]{"first", "second"};
    processAndCheck(logFileNames, nonLogFileNames);
  }

  @Test
  public void testUrlInput() throws IOException {
    String[] logFileNames =
        new String[]{LOG_FOLDER.generateResourceManagerName("first"),
            LOG_FOLDER.generateNodeManagerName("first")};
    String[] nonLogFileNames = new String[]{"first", "second"};
    writeFilesToDisk(logFileNames);
    writeFilesToDisk(nonLogFileNames);
    File zipFile =
        new File(Constants.TEMPORARY_FOLDER, ORIGINAL_ZIP_FILENAME + ".zip");
    LOG_FOLDER.compressToZip(zipFile);
    URL url = Paths.get(zipFile.getAbsolutePath()).toUri().toURL();
    String urlString = url.toString();
    preProcessor.process(
        new String[]{"--logFolder", OUTPUT_FOLDER.getAbsolutePath(),
            "--url", urlString});
    // Only one subdirectory exist and it is named by the current date
    File[] subdirectories = OUTPUT_FOLDER.listFiles();
    File subDirectoryForBundle = subdirectories[0];
    checkFilteredFiles(subDirectoryForBundle.getName(), logFileNames);
  }

  @Test
  public void testContentOfResourceManagerLogFile() throws IOException {
    String resourceManagerName =
        LOG_FOLDER.generateResourceManagerName("first");

    writeAndCompareData(resourceManagerName);
  }

  @Test
  public void testContentOfNodeManagerLogFile() throws IOException {
    String nodeManagerName =
        LOG_FOLDER.generateNodeManagerName("first");

    writeAndCompareData(nodeManagerName);
  }

  @Test
  public void testEmptySearchIntent() {
    String[] emptyArgs =
        new String[]{"--logFolder", OUTPUT_FOLDER.getAbsolutePath()};
    SearchIntent emptySearchIntent = new SearchIntent.Builder()
        .build();
    checkReturnedContext(emptyArgs, emptySearchIntent);
  }

  @Test
  public void testSearchIntentWithShell() {
    String[] argsWithShell =
        new String[]{"--logFolder", OUTPUT_FOLDER.getAbsolutePath(),
            "--shell"};
    SearchIntent searchIntentWithShell = new SearchIntent.Builder()
        .withLaunchingShell(true)
        .build();
    checkReturnedContext(argsWithShell, searchIntentWithShell);
  }

  @Test
  public void testSearchIntentWithCommand() {
    String command = "info";
    String[] argsWithCommand =
        new String[]{"--logFolder", OUTPUT_FOLDER.getAbsolutePath(),
            "--command", command};
    SearchIntent searchIntentWithCommand = new SearchIntent.Builder()
        .withCommand(command)
        .build();
    checkReturnedContext(argsWithCommand, searchIntentWithCommand);
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidArgument() {
    preProcessor.process(
        new String[]{"--logFolder", OUTPUT_FOLDER.getAbsolutePath(),
            "--invalidarg", "parameter"});
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidUrl() {
    preProcessor.process(new String[]{"--logFolder",
        OUTPUT_FOLDER.getAbsolutePath(),
        "--url", "invalidurl"});
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidLocalFile() {
    preProcessor.process(
        new String[]{"--logFolder", OUTPUT_FOLDER.getAbsolutePath(),
            "--local", "invalidfile"});
  }

  @Test(expected = RuntimeException.class)
  public void testMissingRequiredParameter() {
    preProcessor.process(new String[]{});
  }

  private void checkReturnedContext(
      String[] args, SearchIntent expectedSearchIntent) {
    Context contextMock = mock(Context.class);
    PreProcessor preProcessor = new PreProcessor(contextMock);
    contextMock = preProcessor.process(args);
    verify(contextMock).setupSearchEngine(OUTPUT_FOLDER);
    verify(contextMock).setSearchIntent(expectedSearchIntent);
  }

  private void processAndCheck(
      String[] logFileNames, String[] nonLogFileNames) {
    writeFilesToDisk(logFileNames);
    writeFilesToDisk(nonLogFileNames);
    processInput();
    checkFilteredFiles(ORIGINAL_ZIP_FILENAME, logFileNames);
  }

  private void writeFilesToDisk(String[] fileNames) {
    if (fileNames != null) {
      for (String fileName : fileNames) {
        LOG_FOLDER.addFile(fileName,
            new LinesOfLogs(Collections.singletonList(fileName)), null);
      }
    }
  }

  private void processInput() {
    File zipFile =
        new File(Constants.TEMPORARY_FOLDER, ORIGINAL_ZIP_FILENAME + ".zip");
    LOG_FOLDER.compressToZip(zipFile);
    preProcessor.process(
        new String[]{"--logFolder", OUTPUT_FOLDER.getAbsolutePath(),
            "--local", zipFile.getAbsolutePath()});
  }

  private void checkFilteredFiles(
      String directoryForBundleName, String[] fileNames) {
    List<String> actualFileNames = getFilteredFiles(directoryForBundleName);
    List<String> expectedFileNames = fileNames != null
        ? Arrays.asList(fileNames)
        : Collections.emptyList();
    assertTrue(Arrays.deepEquals(
        expectedFileNames.toArray(), actualFileNames.toArray()));
  }

  private List<String> getFilteredFiles(String directoryForBundleName) {
    List<File> files = Arrays.asList(
        context.getConfig().getLogDir(
            new File(OUTPUT_FOLDER, directoryForBundleName)).listFiles());
    return files.stream().map(File::getName).collect(Collectors.toList());
  }

  private void writeAndCompareData(String logfileName) throws IOException {
    LinesOfLogs content =
        new LinesOfLogs(Arrays.asList("first row", "second row"));
    LOG_FOLDER.addFile(logfileName, content, null);
    processInput();
    File[] files = context.getConfig().getLogDir(
        new File(OUTPUT_FOLDER, ORIGINAL_ZIP_FILENAME)).listFiles();
    String actualContent = LogManipulator.readFile(files[0]).toString();
    String expectedContent = "first row\nsecond row\n";
    assertEquals(expectedContent, actualContent);
  }
}