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

import com.cloudera.bundleprocessor.Constants;
import com.cloudera.bundleprocessor.preprocessor.fileprocessor.FileUtils;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.LinesOfLogs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * LogFolder is able to create log files in the defined folder and
 * write lines of logs into them.
 */
public class LogFolder {

  private final File mainFolder;
  private int numberOfFiles = 0;

  /**
   * Creates a folder on the disk to save log files into.
   *
   * @param mainFolder folder to save files into
   */
  public LogFolder(File mainFolder) {
    this.mainFolder = mainFolder;
    createFolder(mainFolder);
  }

  /**
   * Creates a folder in a temporary folder on the disk to save log files into.
   *
   * @param folderName the name of the folder under the temporary folder
   */
  public LogFolder(String folderName) {
    this.mainFolder = new File(Constants.TEMPORARY_FOLDER, folderName);
    createFolder(mainFolder);
  }

  public void addNodeManager(LinesOfLogs linesOfLogs) {
    addNodeManager(null, linesOfLogs, null);
  }

  public void addNodeManager(String hostName, LinesOfLogs linesOfLogs,
                             String[] subFolders) {
    String fileName = generateNodeManagerName(hostName);
    addFile(fileName, linesOfLogs, subFolders);
  }

  public void addResourceManager(LinesOfLogs linesOfLogs) {
    addResourceManager(null, linesOfLogs, null);
  }

  public void addResourceManager(String hostName, LinesOfLogs linesOfLogs,
                                 String[] subFolders) {
    String fileName = generateResourceManagerName(hostName);
    addFile(fileName, linesOfLogs, subFolders);
  }

  /**
   * Writes a new file into the log folder on the disk.
   *
   * @param fileName    name of hte file
   * @param linesOfLogs content of the file
   * @param subFolders  name of the subfolders to write the files under
   */
  public void addFile(String fileName, LinesOfLogs linesOfLogs,
                      String[] subFolders) {
    numberOfFiles++;
    File targetDir = subFolders == null ?
        mainFolder : createSubFolder(subFolders);
    String content = linesOfLogs == null ? "" : linesOfLogs.toString();
    addFileToFolder(fileName, content, targetDir);
  }

  /**
   * Compresses the whole directory containing
   * the added log files into a single .zip file.
   *
   * @param targetFile .zip file
   */
  public void compressToZip(File targetFile) {
    try {
      FileUtilsForTests.compressFilesToZip(targetFile, mainFolder.listFiles());
    } catch (IOException ioe) {
      throw new RuntimeException("Couldn't compress the log folder", ioe);
    }
  }

  /**
   * Generates name for a NodeManager.
   *
   * @param hostName name of the host
   * @return name of the NM
   */
  public String generateNodeManagerName(String hostName) {
    if (hostName == null) {
      hostName = String.valueOf(numberOfFiles);
    }
    return Constants.NODEMANAGER + "-" + hostName + Constants.LOG_EXTENSION;
  }

  /**
   * Generates name for a ResourceManager.
   *
   * @param hostName name of the host
   * @return name of the RM
   */
  public String generateResourceManagerName(String hostName) {
    if (hostName == null) {
      hostName = String.valueOf(numberOfFiles);
    }
    return Constants.RESOURCEMANAGER + "-" + hostName + Constants.LOG_EXTENSION;
  }

  public void emptyDir() throws IOException {
    FileUtils.emptyDirectory(mainFolder.getAbsolutePath());
  }

  public void deleteDir() throws IOException {
    FileUtils.deleteDirectory(mainFolder.getAbsolutePath());
  }

  private void createFolder(File folder) {
    try {
      FileUtils.findOrCreateDirectory(folder);
    } catch (IOException ioe) {
      throw new RuntimeException("Couldn't create directory: " +
          folder.getAbsolutePath(), ioe);
    }
  }


  private File createSubFolder(String[] subFolders) {
    File actFolder = mainFolder;
    for (String subFolder : subFolders) {
      actFolder = new File(actFolder, subFolder);
    }
    createFolder(actFolder);
    return actFolder;
  }

  private void addFileToFolder(
      String fileName, String content, File targetDir) {
    File file = createFile(targetDir, fileName);
    writeToFile(file, content);
  }

  private File createFile(File targetDir, String fileName) {
    File file = new File(targetDir, fileName);
    try {
      file.createNewFile();
    } catch (Exception e) {
      throw new RuntimeException("Could not create files under directory" +
          targetDir.getAbsolutePath(), e);
    }
    return file;
  }

  private void writeToFile(File file, String content) {
    if (content != null) {
      try {
        BufferedWriter writer =
            new BufferedWriter(new FileWriter(file.getAbsolutePath()));
        writer.write(content);
        writer.close();
      } catch (IOException ioe) {
        throw new RuntimeException("Could not write in file:" +
            file.getAbsolutePath(), ioe);
      }
    }
  }
}
