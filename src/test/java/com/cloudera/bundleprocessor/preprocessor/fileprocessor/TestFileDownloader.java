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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import com.cloudera.bundleprocessor.Constants;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFileDownloader {

  private final String folder = "./src/test/resources/";
  private final File inputFile =
      new File(folder, "inputfile" + Constants.ZIP_EXTENSION);

  private FileDownloader fileDownloader;
  private File outputFile;

  /**
   * Prepares an input file for the FileDownloader.
   *
   * @throws IOException if we can't write to file
   */
  @Before
  public void setUp() throws IOException {
    File targetDir = new File(folder);
    fileDownloader = new FileDownloader(targetDir);
    String inputFileContent = "The content of the input file";
    InputStream inputStream =
        new ByteArrayInputStream(inputFileContent.getBytes());
    OutputStream outputStream = new FileOutputStream(inputFile);
    FileUtils.copyInputStream(inputStream, outputStream);
  }

  @Test
  public void testDownload() throws IOException {
    URL url = Paths.get(inputFile.getAbsolutePath()).toUri().toURL();
    String outputFileName = "outputfile";
    outputFile = fileDownloader.downloadByUrl(url, outputFileName);
    assertTrue("The downloaded file does not exists", outputFile.exists());
    byte[] inputBytes =
        Files.readAllBytes(Paths.get(inputFile.getAbsolutePath()));
    byte[] outputBytes =
        Files.readAllBytes(Paths.get(outputFile.getAbsolutePath()));
    assertArrayEquals(
        "The downloaded file is not identical with the original file",
        inputBytes, outputBytes);
  }

  @After
  public void tearDown() {
    FileUtils.deleteFile(inputFile);
    FileUtils.deleteFile(outputFile);
  }
}
