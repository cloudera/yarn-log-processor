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

import static com.cloudera.bundleprocessor.subshell.command.util.RegexElements.wrapInCapturingGroup;

/**
 * {@code RegularExpressions} contains the user configured regular expressions.
 */
public class RegularExpressions {

  private String timeStamp;
  private String logFile =
      ".*(?<role>(RESOURCEMANAGER|NODNAGER))-(?<host>.+)\\.log\\.out";
  private String configFile = ".*-site.xml";

  public String getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(String timeStamp) {
    this.timeStamp = wrapInCapturingGroup("time", timeStamp);
  }

  public String getLogFile() {
    return logFile;
  }

  public void setLogFile(String logFile) {
    this.logFile = logFile;
  }

  public String getConfigFile() {
    return configFile;
  }

  public void setConfigFile(String configFile) {
    this.configFile = configFile;
  }
}
