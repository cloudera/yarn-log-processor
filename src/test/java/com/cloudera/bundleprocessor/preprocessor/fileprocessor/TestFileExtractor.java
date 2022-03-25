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

package com.cloudera.bundleprocessor.preprocessor.fileprocessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.cloudera.bundleprocessor.Constants;
import com.cloudera.bundleprocessor.preprocessor.exception.WrongInputTypeException;
import com.cloudera.bundleprocessor.subshell.search.engine.util.LogManipulator;
import com.cloudera.bundleprocessor.util.FileUtilsForTests;
import java.io.File;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFileExtractor {

  private static final File WORKSPACE =
      new File(Constants.TEMPORARY_FOLDER, "testfileextractor");
  private static final File EXTRACTED_DIR =
      new File(WORKSPACE, "extractedDir");
  private static final File WRONG_INPUT_FILE =
      new File(WORKSPACE, "wronginputfile.txt");
  private static final File MAIN_ZIP_FILE =
      new File(WORKSPACE, "main.zip");
  private static final File SUB_ZIP_FILE =
      new File(WORKSPACE, "sub.zip");
  private static final File GZ_FILE_IN_SUB_FOLDER =
      new File(WORKSPACE, "gzFileInSub.gz");
  private static final File GZ_FILE_IN_MAIN_FOLDER =
      new File(WORKSPACE, "gzFileInMain.gz");
  private static final String GZ_FILE_CONTENT_IN_MAIN =
      "gzFileContentInMain";
  private static final String GZ_FILE_CONTENT_IN_SUB =
      "gzFileContentInSub";

  private static final FileExtractor FILE_EXTRACTOR =
      new FileExtractor(EXTRACTED_DIR);
  private static final String FOLDER_NOT_FOUND_MSG =
      "The extracted file in the folder was not found";
  private static final String CONTENT_DIFFERS_MSG =
      "The content of the file is different than expected";

  @BeforeClass
  public static void setUp() throws IOException {
    FileUtils.findOrCreateDirectory(WORKSPACE);
  }

  /**
   * Cleaning up files.
   */
  @AfterClass
  public static void deleteDirectory() throws IOException {
    FileUtils.deleteDirectory(WORKSPACE.getAbsolutePath());
  }

  @Test(expected = IOException.class)
  public void testNonExistentInput()
      throws IOException, WrongInputTypeException {
    File wrongInputFile = new File(WORKSPACE, "nonexistent.zip");
    FILE_EXTRACTOR.extract(wrongInputFile, true);
  }

  @Test
  public void testWrongInputFile() throws WrongInputTypeException, IOException {
    WRONG_INPUT_FILE.createNewFile();
    File extractedDir = null;
    try {
      extractedDir = FILE_EXTRACTOR.extract(WRONG_INPUT_FILE, true);
      fail("IOException is supposed to be thrown");
    } catch (IOException expected) {
    }
    assertNull(extractedDir);
    assertTrue(WRONG_INPUT_FILE.exists());
  }

  @Test
  public void testKeepInputFile() throws IOException, WrongInputTypeException {
    File validInputFile = createInputFile();
    File extractedDir = FILE_EXTRACTOR.extract(validInputFile, true);
    assertTrue("No extracted folder was found", extractedDir.exists());

    File extractedFileInMain = new File(extractedDir, FileUtils.cutExtension(
        GZ_FILE_IN_MAIN_FOLDER.getName(), ".gz"));
    assertTrue(FOLDER_NOT_FOUND_MSG, extractedFileInMain.exists());

    String dataInMain = LogManipulator.readFile(extractedFileInMain).toString();
    assertEquals(CONTENT_DIFFERS_MSG, GZ_FILE_CONTENT_IN_MAIN, dataInMain);
    assertTrue(validInputFile.exists());
  }

  @Test
  public void testEraseInputFile() throws IOException, WrongInputTypeException {
    File validInputFile = createInputFile();
    File extractedDir = FILE_EXTRACTOR.extract(validInputFile, false);
    assertTrue("No extracted folder was found", extractedDir.exists());

    File extractedFileInMain = new File(extractedDir,
        FileUtils.cutExtension(GZ_FILE_IN_MAIN_FOLDER.getName(), ".gz"));
    checkExtractedFile(extractedFileInMain, GZ_FILE_CONTENT_IN_MAIN);

    File subDir = new File(extractedDir,
        FileUtils.cutExtension(SUB_ZIP_FILE.getName(), ".zip"));
    File extractedFileInSub = new File(subDir,
        FileUtils.cutExtension(GZ_FILE_IN_SUB_FOLDER.getName(), ".gz"));
    checkExtractedFile(extractedFileInSub, GZ_FILE_CONTENT_IN_SUB);
    assertFalse(validInputFile.exists());
  }

  private void checkExtractedFile(File extractedFile, String expectedContent)
      throws IOException {
    assertTrue(FOLDER_NOT_FOUND_MSG, extractedFile.exists());
    String actualContent = LogManipulator.readFile(extractedFile).toString();
    assertEquals(CONTENT_DIFFERS_MSG, expectedContent, actualContent);
  }

  private File createInputFile() {
    try {
      FileUtilsForTests.compressStringToGzip(
          GZ_FILE_CONTENT_IN_SUB, GZ_FILE_IN_SUB_FOLDER);
      FileUtilsForTests.compressStringToGzip(
          GZ_FILE_CONTENT_IN_MAIN, GZ_FILE_IN_MAIN_FOLDER);
    } catch (IOException e) {
      throw new RuntimeException(
          "Error occurred while trying to write gz file to disk", e);
    }
    try {
      FileUtilsForTests.compressFilesToZip(
          SUB_ZIP_FILE, GZ_FILE_IN_SUB_FOLDER);
      FileUtilsForTests.compressFilesToZip(
          MAIN_ZIP_FILE, GZ_FILE_IN_MAIN_FOLDER, SUB_ZIP_FILE);
    } catch (IOException e) {
      throw new RuntimeException(
          "Error occurred while trying to write zip file to disk", e);
    }
    return MAIN_ZIP_FILE;
  }
}
