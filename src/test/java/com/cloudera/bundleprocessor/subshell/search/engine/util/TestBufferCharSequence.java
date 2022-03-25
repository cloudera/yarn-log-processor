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

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class TestBufferCharSequence {
  @Test
  public void testSimpleStrings() {
    testBufferCharSequence("");
    testBufferCharSequence("x");
    testBufferCharSequence(" ");
    testBufferCharSequence("test_string");
    testBufferCharSequence("&8()=/*-+.!@$;./,");
  }

  private void testBufferCharSequence(String str) {
    ByteBuffer b = ByteBuffer.wrap(str.getBytes());
    BufferCharSequence seq = new BufferCharSequence(b);
    assertEquals(str.length(), seq.length());
    for (int i = 0; i < str.length(); i++) {
      assertEquals(str.charAt(i), seq.charAt(i));
    }
    assertEquals(str, seq.toString());
    if (str.length() > 7) {
      assertEquals(str.substring(4, 7), seq.subSequence(4, 7).toString());
    }
  }
}
