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

package com.cloudera.bundleprocessor.preprocessor.inputprocessor;

import com.cloudera.bundleprocessor.console.ConsoleWriter;
import com.cloudera.bundleprocessor.subshell.context.Config;

import java.io.File;
import java.io.IOException;

/**
 * {code InputProcessor} abstract class is the parent of classes handling
 * different input options parsed from CLI.
 */
public abstract class InputProcessor {

  static void printInvalidParamErrMsg(String param) {
    ConsoleWriter.CONSOLE.error(
        "There is no available input for the given {} \n"
            + "Please check the {} or try another input parameter.",
        param, param);
  }

  /**
   * {code process} gives back the archive file containing
   * the logs to be analyzed.
   *
   * @param context context object
   * @return the archive file containing the log files
   * @throws IOException if downloading was unsuccessful
   */
  public abstract File process(Config context) throws IOException;
}
