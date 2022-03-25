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

import com.cloudera.bundleprocessor.subshell.context.Config;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * LocalFileInputProcessor is for processing local file path input from CLI.
 */
public class LocalFileInputProcessor extends InputProcessor {

  private static final Logger LOG =
      LoggerFactory.getLogger(InputProcessor.class);

  private final File localFile;

  public LocalFileInputProcessor(File localFile) {
    this.localFile = localFile;
  }

  @Override
  public File process(Config context) {
    LOG.info("Local file path will be used to find the original zip archive: "
        + localFile);
    return localFile;
  }

  @VisibleForTesting
  public File getLocalFile() {
    return this.localFile;
  }
}
