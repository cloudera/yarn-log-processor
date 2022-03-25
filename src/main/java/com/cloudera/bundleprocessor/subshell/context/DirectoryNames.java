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

package com.cloudera.bundleprocessor.subshell.context;

/**
 * {@code DirectoryNames} contains the user configured directory names,
 * where the tool will save its files.
 */
public class DirectoryNames {

  private String directoryNameForYarnRelatedLogs;
  private String subdirectoryNameForNodeLogs;
  private String subdirectoryNameForConfigFiles;

  public String getDirectoryNameForYarnRelatedLogs() {
    return directoryNameForYarnRelatedLogs;
  }

  public void setDirectoryNameForYarnRelatedLogs(
      String directoryNameForYarnRelatedLogs) {
    this.directoryNameForYarnRelatedLogs = directoryNameForYarnRelatedLogs;
  }

  public String getSubdirectoryNameForNodeLogs() {
    return subdirectoryNameForNodeLogs;
  }

  public void setSubdirectoryNameForNodeLogs(
      String subdirectoryNameForNodeLogs) {
    this.subdirectoryNameForNodeLogs = subdirectoryNameForNodeLogs;
  }

  public String getSubdirectoryNameForConfigFiles() {
    return subdirectoryNameForConfigFiles;
  }

  public void setSubdirectoryNameForConfigFiles(
      String subdirectoryNameForConfigFiles) {
    this.subdirectoryNameForConfigFiles = subdirectoryNameForConfigFiles;
  }
}
