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

package com.cloudera.bundleprocessor.subshell.search.engine.cache;

import com.cloudera.bundleprocessor.preprocessor.fileprocessor.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class FileBasedCacheIOExecutor implements CacheIOExecutor {

  private final String targetDirectoryPath;

  public FileBasedCacheIOExecutor(String targetDirectoryPath) {
    this.targetDirectoryPath = targetDirectoryPath;
  }

  @Override
  public Serializable readItem(Object key) throws IOException {
    String path = createPath(getFileName(key));
    try {
      return FileUtils.deserialize(path);
    } catch (ClassNotFoundException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void writeItem(Object key, Serializable value) throws IOException {
    String path = createPath(getFileName(key));
    FileUtils.serialize(path, value);
  }

  @Override
  public void remove(Object key) {
    String path = createPath(getFileName(key));
    FileUtils.deleteFile(new File(path));
  }

  @Override
  public void removeAll() throws IOException {
    FileUtils.emptyDirectory(targetDirectoryPath);
  }

  private String createPath(String fileName) {
    return targetDirectoryPath + File.separator + fileName;
  }

  private String getFileName(Object key) {
    return String.valueOf(key.hashCode());
  }
}
