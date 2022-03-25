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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Table is responsible for displaying a table in the subshell.
 * Add header through the constructor.
 * Add rows by {@code setRow()}.
 * After that draw the table by {@code drawTable()}.
 */
public class Table extends RowList {

  private static final String HORIZONTAL_SEP = "-";
  private static final String VERTICAL_SEP = "|";
  private static final String JOIN_SEP = "+";
  private final int size;
  private final List<String[]> rows = new ArrayList<>();

  protected Table(String[] header) {
    super(header);
    this.size = header.length;
  }

  public static String getHorizontalSep() {
    return HORIZONTAL_SEP;
  }

  public static String getJoinSep() {
    return JOIN_SEP;
  }

  @Override
  protected int getSize() {
    return size;
  }

  @Override
  protected void addRowInternal(String[] rowElements) {
    rows.add(rowElements);
  }

  /**
   * The {@code print()} function displays the table in the terminal.
   */
  @Override
  public String print() {
    if (header.length == 0) {
      return Constants.EMPTY_OUTPUT_MESSAGE;
    }
    StringBuilder builder = new StringBuilder();
    int[] maxWidths = computeMaxWidths();
    printLine(maxWidths, builder);
    printRow(this.header, maxWidths, builder);
    printLine(maxWidths, builder);
    for (String[] cells : rows) {
      printRow(cells, maxWidths, builder);
    }
    printLine(maxWidths, builder);
    return builder.toString();
  }

  private int[] computeMaxWidths() {
    int[] maxWidths = Arrays.stream(header).mapToInt(String::length).toArray();
    for (String[] cells : rows) {
      computeMaxWidthsOnRow(maxWidths, cells);
    }
    return maxWidths;
  }

  private void computeMaxWidthsOnRow(int[] maxWidths, String[] cells) {
    for (int i = 0; i < cells.length; i++) {
      maxWidths[i] = Math.max(maxWidths[i], cells[i].length());
    }
  }

  private void printLine(int[] columnWidths, StringBuilder builder) {
    for (int i = 0; i < columnWidths.length; i++) {
      String line = String.join("", Collections.nCopies(columnWidths[i]
          + VERTICAL_SEP.length() + 1, HORIZONTAL_SEP));
      String joinSepOrNothing = (i == columnWidths.length - 1 ? JOIN_SEP : "");
      builder.append(JOIN_SEP).append(line).append(joinSepOrNothing);
    }
    builder.append("\n");
  }

  private void printRow(String[] cells, int[] maxWidths,
                        StringBuilder builder) {
    for (int i = 0; i < cells.length; i++) {
      String s = cells[i];
      String verStrTemp = i == cells.length - 1 ? VERTICAL_SEP : "";
      builder.append(String.format(
          "%s %-" + maxWidths[i] + "s %s", VERTICAL_SEP, s, verStrTemp));
    }
    builder.append("\n");
  }
}