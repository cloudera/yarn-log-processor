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

package com.cloudera.bundleprocessor.subshell.format;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestStringPrintable {

  @Test
  public void testStringInput() {
    String string1 = "any string can be here";
    String string2 = "write here something";
    Printable stringPrintable1 = new StringPrintable(string1);
    Printable stringPrintable2 = new StringPrintable(string2);
    assertEquals(string1, stringPrintable1.print());
    assertEquals(string2, stringPrintable2.print());
  }

  @Test
  public void testNullInput() {
    Printable stringPrintable = new StringPrintable(null);
    assertNull(stringPrintable.print());
  }
}
