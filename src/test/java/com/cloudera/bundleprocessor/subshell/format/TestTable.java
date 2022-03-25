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

public class TestTable {

  private static final String[] HEADER =
      new String[]{"header1", "header2"};
  private static final String[] FIRST_ROW =
      new String[]{"element1", "element2"};
  private static final String[] SECOND_ROW =
      new String[]{"element3", "element4"};
  private static final String OUTPUT_DIFFERS_MSG =
      "The output differs from expected";

  @Test(expected = RuntimeException.class)
  public void testNullHeader() {
    new Table(null);
  }

  @Test(expected = RuntimeException.class)
  public void testNullRow() {
    Table table = new Table(HEADER);
    table.addRow((String[]) null);
  }

  @Test(expected = RuntimeException.class)
  public void testTableWithNullValues() {
    Table table = new Table(new String[]{null});
    table.addRow(new String[]{null});
    table.print();
  }

  @Test(expected = RuntimeException.class)
  public void testAddingNonMatchingRow() {
    Table table = new Table(HEADER);
    table.addRow(new String[]{"element1", "element2", "element3"});
  }

  @Test
  public void testEmptyTable() {
    Table table = new Table(new String[]{});
    table.addRow(new String[]{});
    assertEquals(OUTPUT_DIFFERS_MSG,
        Constants.EMPTY_OUTPUT_MESSAGE, table.print());
  }

  @Test
  public void testOnlyHeader() {
    Table table = new Table(HEADER);
    String expectedOutput = getSeparatorLine() +
        getLineWithStrings(HEADER) + getSeparatorLine() +
        getSeparatorLine();
    String actualOutput = table.print();
    assertTrue(OUTPUT_DIFFERS_MSG, actualOutput.matches(expectedOutput));
  }

  @Test
  public void testOneRow() {
    Table table = new Table(HEADER);
    table.addRow(FIRST_ROW);
    String expectedOutput = getSeparatorLine() +
        getLineWithStrings(HEADER) + getSeparatorLine()
        + getLineWithStrings(FIRST_ROW) + getSeparatorLine();
    String actualOutput = table.print();
    assertTrue(OUTPUT_DIFFERS_MSG, actualOutput.matches(expectedOutput));
  }

  @Test
  public void testTwoRows() {
    Table table = new Table(HEADER);
    table.addRow(FIRST_ROW);
    table.addRow(SECOND_ROW);
    String expectedOutput = getSeparatorLine() +
        getLineWithStrings(HEADER) + getSeparatorLine() +
        getLineWithStrings(FIRST_ROW) + getLineWithStrings(SECOND_ROW) +
        getSeparatorLine();
    String actualOutput = table.print();
    assertTrue(OUTPUT_DIFFERS_MSG, actualOutput.matches(expectedOutput));
  }

  private String getSeparatorLine() {
    return "(" + Table.getHorizontalSep() + "|\\" +
        Table.getJoinSep() + ")*\\n";
  }

  private String getLineWithStrings(String[] strings) {
    StringBuilder builder = new StringBuilder();
    builder.append(".*");
    for (String string : strings) {
      builder.append(string).append(".*");
    }
    builder.append("\\n");
    return builder.toString();
  }
}
