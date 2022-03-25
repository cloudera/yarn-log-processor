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

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.UnknownHostException;

public class FakeURLHandler extends URLStreamHandler {

  private static final String VALID_URL_ADDRESS =
      "https://www.validhost.com/validparameter";
  private static final String INVALID_HOST_NAME =
      "https://www.invalidhost.com/validparameter";
  private static final String INVALID_PARAMETER =
      "https://www.validhost.com/invalidparameter";

  public static String getValidURLAddress() {
    return VALID_URL_ADDRESS;
  }

  public static String getInvalidHostName() {
    return INVALID_HOST_NAME;
  }

  public static String getInvalidParameter() {
    return INVALID_PARAMETER;
  }

  @Override
  protected URLConnection openConnection(URL url) throws IOException {
    if (url.toString().equals(VALID_URL_ADDRESS)) {
      return new URLConnection(new URL(VALID_URL_ADDRESS)) {
        @Override
        public void connect() {
        }
      };
    } else {
      throw new UnknownHostException();
    }
  }
}
