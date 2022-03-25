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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileUtils is a non-instantiable utility class.
 * It contains package private functions for creating / checking files
 * Used by {@link FileDownloader} and {@link FileExtractor}
 */
public final class FileUtils {

  private static final Logger LOG =
      LoggerFactory.getLogger(FileUtils.class);

  private FileUtils() {
  }

  /**
   * Builds the directory if it does not already exist.
   *
   * @param directory the directory to build
   * @throws IOException if the directory does not exist and couldn't be created
   */
  public static void findOrCreateDirectory(File directory) throws IOException {
    if (!directory.exists()) {
      if (directory.mkdirs()) {
        LOG.debug("New directory created: {}", directory.getName());
      } else {
        throw new IOException("Could not create directory: " + directory);
      }
    }
  }

  static void copyInputStream(InputStream in, OutputStream out)
      throws IOException {
    final byte[] buffer = new byte[1024];
    int len = in.read(buffer);
    while (len >= 0) {
      out.write(buffer, 0, len);
      len = in.read(buffer);
    }
  }

  static boolean isZip(File actFile) {
    return actFile.getName().toLowerCase().endsWith(Constants.ZIP_EXTENSION);
  }

  static boolean isGz(File actFile) {
    return actFile.getName().toLowerCase().endsWith(Constants.GZ_EXTENSION);
  }

  /**
   * Deletes a file from disk.
   *
   * @param file file to be deleted
   */
  public static void deleteFile(File file) {
    final String actFileName = file.getName();
    if (file.delete()) {
      LOG.debug("File was deleted: {}", actFileName);
    } else {
      LOG.warn("File couldn't be deleted: {}", actFileName);
    }
  }

  /**
   * Copies a file to a target directory.
   *
   * @param oldFile   the file to be copied
   * @param targetDir the directory to copy in
   * @throws IOException if we couldn't copy the file into the targetDir
   */
  static void copyFile(File oldFile, File targetDir) throws IOException {
    FileUtils.findOrCreateDirectory(targetDir);
    File newFile = new File(targetDir, oldFile.getName());
    try (FileInputStream fileInputStream = new FileInputStream(oldFile);
         FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
      FileUtils.copyInputStream(fileInputStream, fileOutputStream);
    } catch (IOException e) {
      LOG.error(String.valueOf(e));
    }
  }

  /**
   * Cuts down the extension from filename.
   *
   * @param fileNameWithExtension whole filename signing the
   *                              type of the file as well (f.e. example.zip)
   * @param extension             end of the filename signing
   *                              the type of the file (f.e. .zip)
   * @return fileName without the extension
   * @throws WrongInputTypeException if the specified extension is
   *                                 not found at the end of the filename
   */
  public static String cutExtension(
      String fileNameWithExtension, String extension)
      throws WrongInputTypeException {
    int fileNameLength = fileNameWithExtension.length();
    if (extension.equals(fileNameWithExtension.substring(
        fileNameLength - extension.length(), fileNameLength))) {
      return fileNameWithExtension.substring(
          0, fileNameLength - extension.length());
    } else {
      throw new WrongInputTypeException();
    }
  }

  /**
   * Deletes all files and subdirectories located in a directory.
   *
   * @param directoryPath the path to the directory to clear.
   * @throws IOException if we couldn't delete any of the files inside
   */
  public static void emptyDirectory(String directoryPath) throws IOException {
    FileUtils.findOrCreateDirectory(new File(directoryPath));
    String[] allFileNames = new File(directoryPath).list();
    if (allFileNames != null) {
      for (String fileName : allFileNames) {
        deleteDirectory(directoryPath + File.separator + fileName);
      }
    }
  }

  /**
   * Deletes a directory with all files and subdirectories in it.
   *
   * @param directoryPath the path to the directory to clear.
   * @throws IOException if we couldn't delete any of the files inside
   */
  public static void deleteDirectory(String directoryPath) throws IOException {
    emptyDirectory(directoryPath);
    new File(directoryPath).delete();
  }

  /**
   * Writes a serialized representation of a Java object to the disk.
   *
   * @param path         the path defines the location of the file,
   *                     where the data is written
   * @param serializable the java object to write to the disk
   * @throws IOException if we can't write to the file
   */
  public static void serialize(String path, Serializable serializable)
      throws IOException {
    FileUtils.findOrCreateDirectory(new File(new File(path).getParent()));
    try (ObjectOutputStream out = new ObjectOutputStream(
        new FileOutputStream(path))) {
      out.writeObject(serializable);
    }
  }

  /**
   * Reads a Java object from the disk.
   *
   * @param path the path of the file to read from the disk
   * @return the Java object read from the disk
   * @throws IOException            if we can't read the file
   * @throws ClassNotFoundException if the class of the Java object is
   *                                not matching with the expected class
   */
  public static Serializable deserialize(String path)
      throws IOException, ClassNotFoundException {
    try (ObjectInputStream in = new ObjectInputStream(
        new FileInputStream(path))) {
      return (Serializable) in.readObject();
    }
  }
}