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

package com.cloudera.bundleprocessor.subshell.command.util;

import com.cloudera.bundleprocessor.subshell.format.Column;
import com.cloudera.bundleprocessor.subshell.format.Printable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class ColumnChecker {

  private ColumnChecker() {
  }

  /**
   * Checks if the printable is a column containing
   * the expected header and rows in any order.
   * In case of different content AssertionError is thrown.
   *
   * @param printable      the printable to examine
   * @param expectedHeader the expected header of the column
   * @param expectedRows   the expected rows of the column
   */
  public static void check(
      Printable printable, String expectedHeader, String[] expectedRows) {
    assertTrue("This printable is not a Column", printable instanceof Column);
    Column column = (Column) printable;
    String[] lines = column.print().split("\n");
    if (expectedRows.length == 0) {
      assertEquals(
          "The header differs from expected.", "Nothing to display", lines[0]);
    } else {
      String actualHeader = lines[0];
      assertEquals(
          "The header differs from expected.", expectedHeader, actualHeader);
      String[] actualRows = Arrays.copyOfRange(lines, 1, lines.length);
      assertRowsEqual(expectedRows, actualRows);
    }

  }

  private static void assertRowsEqual(
      String[] expectedRows, String[] actualRows) {
    assertEquals("Size of the column differs from expected",
        expectedRows.length, actualRows.length);
    List<String> sortedExpected =
        Arrays.stream(expectedRows).sorted().collect(Collectors.toList());
    List<String> sortedActual =
        Arrays.stream(actualRows).sorted().collect(Collectors.toList());
    assertTrue("The content of the column differs from expected.",
        Arrays.deepEquals(sortedExpected.toArray(), sortedActual.toArray()));
  }
}
