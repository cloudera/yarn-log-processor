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

import com.cloudera.bundleprocessor.Constants;
import com.cloudera.bundleprocessor.preprocessor.exception.WrongInputTypeException;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileExtractor is responsible to extract the given archive file
 * to the local output repository.
 * It can extract zip files iteratively as well as gz files
 * Deletes every zip and gz archive except the original zip archive
 */
public class FileExtractor {

  private static final Logger LOG =
      LoggerFactory.getLogger(FileExtractor.class);
  private final File targetDir;

  /**
   * The {@code FileExtractor()} function initializes
   * the FileExtractor with the following variables.
   *
   * @param targetDir the target directory
   */
  public FileExtractor(File targetDir) {
    this.targetDir = targetDir;
  }

  private static void processFile(File actFile, File targetDir)
      throws IOException, WrongInputTypeException {
    LOG.debug("Start processing: {}", actFile.getName());
    if (FileUtils.isZip(actFile)) {
      processZip(actFile, targetDir);
      FileUtils.deleteFile(actFile);
    } else if (FileUtils.isGz(actFile)) {
      processGz(actFile);
      FileUtils.deleteFile(actFile);
    }
  }

  private static void processZip(File actFile, File targetDir)
      throws IOException, WrongInputTypeException {
    String filePath =
        targetDir.getPath() + File.separatorChar + actFile.getName();
    final File zipWithoutExtension =
        new File(FileUtils.cutExtension(filePath, Constants.ZIP_EXTENSION));
    unpackZip(actFile, zipWithoutExtension);
  }

  private static void processGz(File actFile)
      throws IOException, WrongInputTypeException {
    String filePath = actFile.getAbsolutePath();
    final String gzWithoutExtension =
        new File(FileUtils.cutExtension(
            filePath, Constants.GZ_EXTENSION)).getAbsolutePath();
    unpackGz(filePath, gzWithoutExtension);
  }

  private static void unpackZip(File zipFile, File targetDir)
      throws IOException, WrongInputTypeException {
    FileUtils.findOrCreateDirectory(targetDir);
    try (ZipInputStream zipInput = new ZipInputStream(
        new FileInputStream(zipFile))) {
      for (ZipEntry entry = zipInput.getNextEntry(); entry != null;
           entry = zipInput.getNextEntry()) {
        LOG.debug("Extracting {} archive, current entry: {}",
            zipFile.getName(), entry.getName());
        File currFile = new File(targetDir, entry.getName());
        FileUtils.findOrCreateDirectory(currFile.getParentFile());
        if (!entry.isDirectory()) {
          try (OutputStream fileStream = new BufferedOutputStream(
              new FileOutputStream(currFile))) {
            FileUtils.copyInputStream(zipInput, fileStream);
          }
        } else {
          FileUtils.findOrCreateDirectory(currFile);
        }
        // iterative unzipping to open zip-in-zip structures
        processFile(currFile, targetDir);
      }
    }
  }

  private static void unpackGz(String gzFilePath, String targetFile)
      throws IOException {
    byte[] buffer = new byte[1024];
    try (GZIPInputStream gzis =
             new GZIPInputStream(new FileInputStream(gzFilePath));
         FileOutputStream out = new FileOutputStream(targetFile)) {
      int len;
      while ((len = gzis.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }
      LOG.debug("{} file was extracted.", gzFilePath);
    } catch (EOFException e) {
      LOG.debug("{} file couldn't be extracted. " +
          "The file was either empty or corrupted. \n {}", gzFilePath, e);
    }
  }

  /**
   * The {@code extract()} function extracts the original zip file
   * into the target directory.
   *
   * @param originalZip  the original zip archive
   * @param keepOriginal whether to keep the original archive file
   * @return the filename of the created directory with the extracted files
   * @throws IOException if the original zip file does not exist
   */
  public File extract(File originalZip, boolean keepOriginal)
      throws IOException, WrongInputTypeException {
    if (!originalZip.exists()) {
      final String filePath = originalZip.getAbsolutePath();
      String exceptionMsg = String.format(
          "The original archive file ({}) does not exist", filePath);
      throw new IOException(exceptionMsg);
    }
    LOG.info("Start extracting: {}", originalZip.getName());
    processOriginal(originalZip, keepOriginal);
    LOG.info("The archive was successfully extracted to target directory " +
        originalZip.getName());
    String extractedDirName = FileUtils.cutExtension(originalZip.getName(),
        Constants.ZIP_EXTENSION);
    return new File(targetDir, extractedDirName);
  }

  private void processOriginal(File originalZip, boolean keepOrigin)
      throws IOException, WrongInputTypeException {
    LOG.debug("Start processing: " + originalZip.getName());
    if (!FileUtils.isZip(originalZip)) {
      throw new IOException("The input file (" + originalZip.getAbsolutePath()
          + ") is not a zip archive");
    } else {
      if (keepOrigin) {
        LOG.info("Start extracting the diagnostic bundle ({}),"
                + " the original archive will be kept after the process",
            originalZip.getName());
        processZip(originalZip, this.targetDir);
      } else {
        LOG.info("Start extracting the diagnostic bundle ({}),"
                + " the original archive will be deleted after the process",
            originalZip.getName());
        processFile(originalZip, this.targetDir);
      }
    }
  }
}
