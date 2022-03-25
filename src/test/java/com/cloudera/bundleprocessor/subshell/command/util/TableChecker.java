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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.cloudera.bundleprocessor.subshell.format.Printable;
import com.cloudera.bundleprocessor.subshell.format.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TableChecker {

  private static final Pattern PATTERN = Pattern.compile("( [^|]* )");

  /**
   * Checks if the printable is a table containing the expected header and rows.
   * Same rows in any order are accepted.
   * In case of different content Assertion exception is thrown.
   *
   * @param printable      the printable to examine
   * @param expectedHeader the expected header of the table
   * @param expectedRows   the expected rows of the table
   */
  public static void check(Printable printable, String[] expectedHeader,
                           List<String[]> expectedRows) {
    assertTrue("This printable is not a Table", printable instanceof Table);
    Table table = (Table) printable;
    String[] lines = table.print().split("\n");
    // 0th line is separator line in a table, the 1st line contains the header
    String[] actualHeader = parseLine(lines[1]);
    List<String[]> actualRows = parseRows(lines);
    assertArrayEquals(
        "The header differs from expected.", expectedHeader, actualHeader);
    assertListOfArraysEqual(expectedRows, actualRows);
  }

  private static void assertListOfArraysEqual(
      List<String[]> expectedRows, List<String[]> actualRows) {
    assertEquals("Size of the table differs from expected",
        expectedRows.size(), actualRows.size());
    List<String> transformedExpected = new ArrayList<>();
    List<String> transformedActual = new ArrayList<>();
    for (String[] expectedRow : expectedRows) {
      transformedExpected.add(String.join(" ", expectedRow));
    }
    for (String[] actualRow : actualRows) {
      transformedActual.add(String.join(" ", actualRow));
    }
    transformedExpected =
        transformedExpected.stream().sorted().collect(Collectors.toList());
    transformedActual =
        transformedActual.stream().sorted().collect(Collectors.toList());
    assertTrue("The content of the table differs from expected.",
        Arrays.deepEquals(transformedExpected.toArray(),
            transformedActual.toArray()));
  }

  private static List<String[]> parseRows(String[] lines) {
    List<String[]> rows = new ArrayList<>();
    // 0th line is separator line, the 1st is the header, the 2nd is separator,
    // the content starts in the 3rd line
    // the last line is a separator line again,
    // so we parse line between 3rd and n-1
    for (int i = 3; i < lines.length - 1; i++) {
      rows.add(parseLine(lines[i]));
    }
    return rows;
  }

  private static String[] parseLine(String line) {
    Matcher matcher = PATTERN.matcher(line);
    List<String> stringList = new ArrayList<>();
    while (matcher.find()) {
      stringList.add(matcher.group().trim());
    }
    return stringList.toArray(new String[0]);
  }
}
