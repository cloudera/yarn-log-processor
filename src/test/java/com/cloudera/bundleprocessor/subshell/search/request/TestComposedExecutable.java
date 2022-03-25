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

package com.cloudera.bundleprocessor.subshell.search.request;

import com.cloudera.bundleprocessor.subshell.format.StringPrintable;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestComposedExecutable {

  @Test
  public void testEmptyComposedExecutable() throws IOException {
    ComposedExecutable composedExecutable = new ComposedExecutable();
    composedExecutable.execute(null);
    String actualOutput = composedExecutable.getPrintable().print();
    assertEquals("The actual output is not empty String", "", actualOutput);
  }

  @Test(expected = NullPointerException.class)
  public void testNullExecutable() throws IOException {
    ComposedExecutable composedExecutable = new ComposedExecutable();
    composedExecutable.addExecutable(null);
    composedExecutable.execute(null);
    String actualOutput = composedExecutable.getPrintable().print();
    assertEquals("The actual output is not empty String", "", actualOutput);
  }

  @Test
  public void testComposedExecutable() throws IOException {
    String string1 = "string1";
    String string2 = "string2";
    SingleExecutable singleExecutable1 = mock(SingleExecutable.class);
    SingleExecutable singleExecutable2 = mock(SingleExecutable.class);
    when(singleExecutable1.getPrintable()).thenReturn(
        new StringPrintable(string1));
    when(singleExecutable2.getPrintable()).thenReturn(
        new StringPrintable(string2));
    ComposedExecutable composedExecutable = new ComposedExecutable();
    composedExecutable.addExecutable(singleExecutable1);
    composedExecutable.addExecutable(singleExecutable2);
    composedExecutable.execute(null);
    String expectedOutput = string1 + string2;
    String actualOutput = composedExecutable.getPrintable().print();
    assertEquals(
        "The actual output is not the concatenation of individual outputs",
        expectedOutput, actualOutput);
  }
}
