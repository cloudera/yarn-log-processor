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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileFilter {

  private static final Logger LOG =
      LoggerFactory.getLogger(FileFilter.class);

  private final File sourceDir;
  private final File workspace;

  public FileFilter(File sourceDir, File workspace) {
    this.sourceDir = sourceDir;
    this.workspace = workspace;
  }

  /**
   * Filter function searches for file paths matching with a pathRegex and
   * copies matches to a specified directory.
   * The source directory (where we search for the files) and
   * the workspace (where we move them to)
   * are specified in the constructor.
   *
   * @param pathRegex     matching the filepath
   * @param targetDirName the subdirectory under workspace to move the files in
   * @throws IOException exception can occur while writing on disk
   */
  public void filter(String pathRegex, String targetDirName)
      throws IOException {
    LOG.info("Start filtering files into {}", targetDirName);
    File targetDir = new File(this.workspace, targetDirName);
    FileUtils.findOrCreateDirectory(targetDir);
    Path sourceDirPath = Paths.get(this.sourceDir.getPath());
    BiPredicate<Path, BasicFileAttributes> ifIncludeFile =
        (path, basicFileAttributes) -> {
          File file = path.toFile();
          return file.isFile() && file.getAbsolutePath().matches(pathRegex);
        };
    Stream<Path> pathStream = Files.find(
        sourceDirPath, Constants.DEPTH_OF_FILTERING_SEARCH, ifIncludeFile);
    List<Path> pathList = pathStream.collect(Collectors.toList());
    for (Path path : pathList) {
      File file = path.toFile();
      FileUtils.copyFile(file, targetDir);
    }
    LOG.info("Finished filtering files into {}", targetDirName);
  }
}

