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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestSearchIntent {
  @Test
  public void testMultipleInput() {
    String cmd = "--help";

    try {
      SearchIntent intent = new SearchIntent.Builder()
          .withCommand(cmd)
          .withLaunchingShell(true)
          .build();
      fail("Expected an IllegalArgumentException, but build() did not fail");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void testCommand() {
    String cmd = "--help";

    SearchIntent intent = new SearchIntent.Builder()
        .withCommand(cmd)
        .build();
    assertFalse(
        "Should not be launching with shell", intent.isLaunchingShell());
    assertEquals("The SearchIntent command does not match with the input",
        cmd, intent.getCommand());
  }

  @Test
  public void testLaunchingWithShell() {
    String cmd = "--help";

    SearchIntent intent = new SearchIntent.Builder()
        .withLaunchingShell(true)
        .build();
    assertTrue("Should be launching with shell", intent.isLaunchingShell());
    assertNull(
        "The SearchIntent command should be null, when launching with shell",
        intent.getCommand());
  }
}
