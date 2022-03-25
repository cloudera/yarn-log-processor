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

package com.cloudera.bundleprocessor.subshell.search.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;
import org.junit.Test;

public class TestQuery {
  @Test
  public void testEmptyBuilder() {
    Query query = new Query.Builder().build();
    assertNull(query.getPattern());
    assertFalse(query.searchInFileNames());
    assertFalse(query.searchInNmLogs());
    assertFalse(query.searchInRmLogs());
  }

  @Test
  public void testCheckingFileNames() {
    Pattern pattern = Pattern.compile(".*");
    Query query = new Query.Builder()
        .isCheckingFileNames()
        .withPattern(pattern)
        .build();
    assertEquals(pattern, query.getPattern());
    assertTrue(query.searchInFileNames());
    assertFalse(query.searchInNmLogs());
    assertFalse(query.searchInRmLogs());
  }

  @Test
  public void testCheckingNmLogs() {
    Pattern pattern = Pattern.compile(".*");
    Query query = new Query.Builder()
        .isCheckingNmLogs()
        .withPattern(pattern)
        .build();
    assertEquals(pattern, query.getPattern());
    assertFalse(query.searchInFileNames());
    assertTrue(query.searchInNmLogs());
    assertFalse(query.searchInRmLogs());
  }

  @Test
  public void testCheckingRmLogs() {
    Pattern pattern = Pattern.compile(".*");
    Query query = new Query.Builder()
        .isCheckingRmLogs()
        .withPattern(pattern)
        .build();
    assertEquals(pattern, query.getPattern());
    assertFalse(query.searchInFileNames());
    assertFalse(query.searchInNmLogs());
    assertTrue(query.searchInRmLogs());
  }
}
