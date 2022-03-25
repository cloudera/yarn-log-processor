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
import static org.junit.Assert.assertTrue;

public class TestRowListFactory {

  @Test(expected = RuntimeException.class)
  public void testNull() {
    RowListFactory.createRowList(null);
  }

  @Test(expected = RuntimeException.class)
  public void testEmpty() {
    RowListFactory.createRowList();
  }

  @Test
  public void testCreateColumn() {
    RowList rowList = RowListFactory.createRowList("header");
    assertTrue(rowList instanceof Column);
    String expectedOutput = Constants.EMPTY_OUTPUT_MESSAGE;
    String actualOutput = rowList.print();
    assertEquals(
        "The output differs from expected", expectedOutput, actualOutput);
  }

  @Test
  public void testCreateTable() {
    RowList rowList = RowListFactory.createRowList("header1", "header2");
    assertTrue(rowList instanceof Table);
    String expectedOutput = "+---------+---------+\n"
        + "| header1 | header2 |\n"
        + "+---------+---------+\n"
        + "+---------+---------+\n";
    String actualOutput = rowList.print();
    assertEquals(
        "The output differs from expected", expectedOutput, actualOutput);
  }

}
