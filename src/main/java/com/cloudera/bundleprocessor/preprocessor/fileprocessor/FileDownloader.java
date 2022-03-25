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
import com.cloudera.bundleprocessor.preprocessor.exception.AuthenticationException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.rmi.ConnectIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileDownloader is responsible to download the input file
 * via direct URL address.
 * By adding local archive as input parameter,
 * you prevent this class to instantiate.
 */
public class FileDownloader {

  private static final Logger LOG =
      LoggerFactory.getLogger(FileDownloader.class);

  private final File targetDirectory;

  public FileDownloader(File targetDirectory) {
    this.targetDirectory = targetDirectory;
  }

  private static boolean isNetAvailable() {
    try {
      URL url = new URL("http://www.google.com");
      URLConnection conn = url.openConnection();
      conn.connect();
      conn.getInputStream().close();
      return true;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * The {@code downloadByUrl()} function downloads a zip archive
   * via URL address.
   *
   * @param urlAddress URL address
   * @return downloaded archive file
   * @throws IOException           if the URL address was empty
   * @throws FileNotFoundException if the URL address was invalid
   */
  public File downloadByUrl(URL urlAddress, String fileName)
      throws IllegalArgumentException, IOException {
    if (urlAddress == null) {
      throw new IllegalArgumentException("The URL address was empty");
    }
    try {
      FileUtils.findOrCreateDirectory(targetDirectory);
      File originalZip =
          new File(targetDirectory, fileName + Constants.ZIP_EXTENSION);
      LOG.info("Start downloading original archive file: " +
          originalZip.getName());
      try (InputStream in =
               new BufferedInputStream(urlAddress.openStream(), 1024);
           OutputStream out =
               new BufferedOutputStream(new FileOutputStream(originalZip))) {
        FileUtils.copyInputStream(in, out);
      }
      LOG.info("Original archive file was successfully downloaded: " +
          originalZip.getName());
      return originalZip;
    } catch (UnknownHostException e) {
      if (isNetAvailable()) {
        throw new ConnectIOException(
            "No server could be found for the specified URL address. \n"
                + "Either the server does not exist or you don't have "
                + "the authentication to reach it", e);
      } else {
        throw new AuthenticationException(
            "You have no internet connection. \n"
                + "Try to check your network cables or wifi connection.", e);
      }
    }
  }
}
