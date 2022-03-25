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

import com.cloudera.bundleprocessor.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * {@code Config} is the in-memory representation of the configuration file.
 */
public class Config {

  private RegularExpressions regularExpressions;
  private DirectoryNames directoryNames;
  private Cache cache;

  /**
   * {@code createConfig()} function creates a representation
   * of the configuration file on the disk.
   *
   * @return Config the representation of the user configuration.
   */
  public static Config createConfig(File configFileOnDisk) {
    if (!configFileOnDisk.exists()) {
      throw new RuntimeException("No configuration was provided for the tool");
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(configFileOnDisk, Config.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Couldn't read configurations from the config file", e);
    }
  }

  public static Config createConfig() {
    return createConfig(new File(Constants.DEFAULT_CONFIG_PATH));
  }

  public RegularExpressions getRegexes() {
    return regularExpressions;
  }

  public void setRegularExpressions(RegularExpressions regularExpressions) {
    this.regularExpressions = regularExpressions;
  }


  public DirectoryNames getDirs() {
    return directoryNames;
  }

  public void setDirectoryNames(DirectoryNames directoryNames) {
    this.directoryNames = directoryNames;
  }

  public Cache getCache() {
    return cache;
  }

  public void setCache(Cache cache) {
    this.cache = cache;
  }

  public File getLogDir(File mainDir) {
    final File workspace =
        new File(mainDir, getDirs().getDirectoryNameForYarnRelatedLogs());
    return new File(workspace, getDirs().getSubdirectoryNameForNodeLogs());
  }
}
