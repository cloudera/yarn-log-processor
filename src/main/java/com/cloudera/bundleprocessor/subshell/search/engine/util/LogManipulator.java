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

package com.cloudera.bundleprocessor.subshell.search.engine.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LogManipulator is a non-instantiable utility class.
 * It contains package private functions for checking / searching in logs
 * Used by List- objects
 */
public final class LogManipulator {

  private static final String RM_REGEX = ".*RESOURCEMANAGER.*";
  private static final String NM_REGEX = ".*NODEMANAGER.*";
  private static final Pattern RM_PATTERN = Pattern.compile(RM_REGEX);
  private static final Pattern NM_PATTERN = Pattern.compile(NM_REGEX);

  private LogManipulator() {
  }

  public static boolean isRMlog(File file) {
    Matcher matcher = RM_PATTERN.matcher(file.getName());
    return matcher.find();
  }

  public static boolean isNMlog(File file) {
    Matcher matcher = NM_PATTERN.matcher(file.getName());
    return matcher.find();
  }

  /**
   * {@code fromFile()} is creating a character sequence from a file
   * to make it easier to search with regexps.
   *
   * @param file the file to process
   * @return CharSequence
   * @throws IOException if file couldn't be red
   */
  public static CharSequence readFile(File file) throws IOException {
    FileInputStream input = new FileInputStream(file);
    FileChannel channel = input.getChannel();
    // Create a read-only CharBuffer on the file
    ByteBuffer bbuf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
        (int) channel.size());
    return new BufferCharSequence(bbuf);
  }
}
