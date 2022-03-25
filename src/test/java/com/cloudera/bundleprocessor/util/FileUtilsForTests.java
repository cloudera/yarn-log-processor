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

package com.cloudera.bundleprocessor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class FileUtilsForTests {

  private FileUtilsForTests() {
  }

  /**
   * Compresses data into a .gzip file.
   *
   * @param string the content to be compressed
   * @param target the .gzip file
   * @throws IOException if we can't write into the file
   */
  public static void compressStringToGzip(String string, File target)
      throws IOException {
    try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
        new FileOutputStream(target))) {
      gzipOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
    }
  }

  /**
   * Compresses multiple files into a zip archive.
   *
   * @param sourceFiles files to compress
   * @param targetFile  zip files to compress into
   * @throws IOException if we couldn't read from the source files
   *                     or write to the target
   */
  public static void compressFilesToZip(File targetFile, File... sourceFiles)
      throws IOException {
    try (FileOutputStream fileOut = new FileOutputStream(targetFile);
         ZipOutputStream zipOut = new ZipOutputStream(fileOut)) {
      for (File sourceFile : sourceFiles) {
        addFileToZipOut(sourceFile, zipOut);
      }
    }
  }

  private static void addFileToZipOut(File sourceFile, ZipOutputStream zipOut)
      throws IOException {
    FileInputStream fis = new FileInputStream(sourceFile);
    ZipEntry zipEntry = new ZipEntry(sourceFile.getName());
    zipOut.putNextEntry(zipEntry);
    byte[] bytes = new byte[1024];
    int length;
    while ((length = fis.read(bytes)) >= 0) {
      zipOut.write(bytes, 0, length);
    }
  }

}
