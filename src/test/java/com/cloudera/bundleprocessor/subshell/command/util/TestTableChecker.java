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

import com.cloudera.bundleprocessor.subshell.format.RowListFactory;
import com.cloudera.bundleprocessor.subshell.format.Table;
import org.junit.Test;

import java.util.Arrays;

public class TestTableChecker {

  private static final String[] FIRST_ROW =
      new String[]{"first row, first cell", "first row, second cell"};
  private static final String[] SECOND_ROW =
      new String[]{"second row, first cell", "second row, second cell"};
  private static final String[] WRONG_ROW =
      new String[]{"this is wrong", "very wrong"};
  private static final String[] ORIGINAL_HEADER =
      new String[]{"header first cell", "header second cell"};
  private static final String[] WRONG_HEADER =
      new String[]{"this is wrong", "very wrong"};

  @Test
  public void testOriginal() {
    Table table = (Table) RowListFactory.createRowList(ORIGINAL_HEADER);
    table.addRow(FIRST_ROW);
    table.addRow(SECOND_ROW);
    TableChecker.check(
        table, ORIGINAL_HEADER, Arrays.asList(FIRST_ROW, SECOND_ROW));
  }

  @Test
  public void testSwitchedOrder() {
    Table table = (Table) RowListFactory.createRowList(ORIGINAL_HEADER);
    table.addRow(SECOND_ROW);
    table.addRow(FIRST_ROW);
    TableChecker.check(
        table, ORIGINAL_HEADER, Arrays.asList(FIRST_ROW, SECOND_ROW));
  }

  @Test(expected = AssertionError.class)
  public void testWrongHeader() {
    Table table = (Table) RowListFactory.createRowList(WRONG_HEADER);
    table.addRow(FIRST_ROW);
    table.addRow(SECOND_ROW);
    TableChecker.check(
        table, ORIGINAL_HEADER, Arrays.asList(FIRST_ROW, SECOND_ROW));
  }

  @Test(expected = AssertionError.class)
  public void testLessRows() {
    Table table = (Table) RowListFactory.createRowList(ORIGINAL_HEADER);
    table.addRow(FIRST_ROW);
    TableChecker.check(
        table, ORIGINAL_HEADER, Arrays.asList(FIRST_ROW, SECOND_ROW));
  }

  @Test(expected = AssertionError.class)
  public void testWrongRow() {
    Table table = (Table) RowListFactory.createRowList(ORIGINAL_HEADER);
    table.addRow(FIRST_ROW);
    table.addRow(WRONG_ROW);
    TableChecker.check(
        table, ORIGINAL_HEADER, Arrays.asList(FIRST_ROW, SECOND_ROW));
  }
}
