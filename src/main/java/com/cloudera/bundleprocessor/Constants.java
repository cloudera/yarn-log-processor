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

package com.cloudera.bundleprocessor;

import java.io.File;

/**
 * Constants is a utility class containing constant values.
 */
public final class Constants {

  private Constants() {
  }

  public static final String ZIP_EXTENSION = ".zip";
  public static final String GZ_EXTENSION = ".gz";
  public static final String LOG_EXTENSION = ".log.out";
  public static final String RESOURCEMANAGER = "RESOURCEMANAGER";
  public static final String NODEMANAGER = "NODEMANAGER";
  public static final int[] RANGE_OF_UNDISPLAYED_CHARS_IN_CONTAINERID = {2, 6};
  public static final int DEPTH_OF_FILTERING_SEARCH = 10;
  public static final String EMPTY_OUTPUT_MESSAGE = "Nothing to display\n";
  public static final String DEFAULT_CONFIG_PATH =
      "./src/main/resources/config.json";
  public static final File TEMPORARY_FOLDER =
      new File(System.getProperty("java.io.tmpdir"));
}
