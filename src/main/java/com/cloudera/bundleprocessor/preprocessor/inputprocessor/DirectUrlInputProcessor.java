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

import com.cloudera.bundleprocessor.preprocessor.fileprocessor.FileDownloader;
import com.cloudera.bundleprocessor.subshell.context.Config;
import com.cloudera.bundleprocessor.util.DateUtils;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class DirectUrlInputProcessor extends InputProcessor {

  private static final Logger LOG =
      LoggerFactory.getLogger(DirectUrlInputProcessor.class);

  private final URL directUrl;
  private final File mainDirectory;

  /**
   * DirectUrlInputProcessor is for processing URL address input from CLI.
   * We download the file at the specified URL.
   *
   * @param directUrl     direct URL address
   * @param mainDirectory workspace for the tool
   */
  public DirectUrlInputProcessor(URL directUrl, File mainDirectory) {
    this.directUrl = directUrl;
    this.mainDirectory = mainDirectory;
  }

  @VisibleForTesting
  FileDownloader createFileDownloader() {
    return new FileDownloader(this.mainDirectory);
  }

  @Override
  public File process(Config config) throws IOException {
    LOG.info("URL address will be used the download the original zip archive: "
        + directUrl);
    FileDownloader downloader = createFileDownloader();
    try {
      return downloader.downloadByUrl(directUrl, DateUtils.getCurrentDate());
    } catch (FileNotFoundException e) {
      printInvalidParamErrMsg("URL address");
      throw new IOException("The file couldn't be found.", e);
    }
  }

  @VisibleForTesting
  public URL getDirectUrl() {
    return directUrl;
  }

  @VisibleForTesting
  public File getMainDirectory() {
    return mainDirectory;
  }
}
