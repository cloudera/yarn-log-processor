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

import com.cloudera.bundleprocessor.Constants;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestColumn {

  private static final String HEADER = "Header";
  private static final String ROW1 = "First row";
  private static final String ROW2 = "Second row";

  @Test
  public void testEmpty() {
    Column column = new Column(new String[]{""});
    assertEquals(Constants.EMPTY_OUTPUT_MESSAGE, column.print());
  }

  @Test
  public void testOnlyHeader() {
    Column column = new Column(new String[]{HEADER});
    assertEquals(Constants.EMPTY_OUTPUT_MESSAGE, column.print());
  }

  @Test
  public void testOneRow() {
    Column column = new Column(new String[]{HEADER});
    column.addRow(ROW1);
    String output = HEADER + "\n" + ROW1 + "\n";
    assertEquals(output, column.print());
  }

  @Test
  public void testTwoRows() {
    Column column = new Column(new String[]{HEADER});
    column.addRow(ROW1);
    column.addRow(ROW2);
    String output = HEADER + "\n" + ROW1 + "\n" + ROW2 + "\n";
    assertEquals(output, column.print());
  }

  @Test
  public void testNullHeader() {
    Column column = new Column(new String[]{null});
    column.addRow(ROW1);
    column.addRow(ROW2);
    String output = "null" + "\n" + ROW1 + "\n" + ROW2 + "\n";
    assertEquals(output, column.print());
  }

  @Test
  public void testNullRow() {
    Column column = new Column(new String[]{null});
    column.addRow(ROW1);
    column.addRow(new String[]{null});
    column.addRow(ROW2);
    String output = "null" + "\n" + ROW1 + "\nnull\n" + ROW2 + "\n";
    assertEquals(output, column.print());
  }
}
