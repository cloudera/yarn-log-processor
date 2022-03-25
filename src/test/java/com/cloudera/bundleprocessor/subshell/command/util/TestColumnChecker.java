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
import com.cloudera.bundleprocessor.subshell.format.RowListFactory;
import org.junit.Test;

public class TestColumnChecker {

  private static final String FIRST_ROW = "first row";
  private static final String SECOND_ROW = "second row";
  private static final String WRONG_ROW = "wrong row";
  private static final String ORIGINAL_HEADER = "ORIGINAL HEADER";
  private static final String WRONG_HEADER = "WRONG HEADER";

  @Test
  public void testOriginal() {
    Column column = (Column) RowListFactory.createRowList(ORIGINAL_HEADER);
    column.addRow(FIRST_ROW);
    column.addRow(SECOND_ROW);
    ColumnChecker.check(column, ORIGINAL_HEADER,
        new String[]{FIRST_ROW, SECOND_ROW});
  }

  @Test
  public void testSwitchedOrder() {
    Column column = (Column) RowListFactory.createRowList(ORIGINAL_HEADER);
    column.addRow(SECOND_ROW);
    column.addRow(FIRST_ROW);
    ColumnChecker.check(column, ORIGINAL_HEADER,
        new String[]{FIRST_ROW, SECOND_ROW});
  }

  @Test(expected = AssertionError.class)
  public void testWrongHeader() {
    Column column = (Column) RowListFactory.createRowList(WRONG_HEADER);
    column.addRow(FIRST_ROW);
    column.addRow(SECOND_ROW);
    ColumnChecker.check(column, ORIGINAL_HEADER,
        new String[]{FIRST_ROW, SECOND_ROW});
  }

  @Test(expected = AssertionError.class)
  public void testLessRows() {
    Column column = (Column) RowListFactory.createRowList(ORIGINAL_HEADER);
    column.addRow(FIRST_ROW);
    ColumnChecker.check(column, ORIGINAL_HEADER,
        new String[]{FIRST_ROW, SECOND_ROW});
  }

  @Test(expected = AssertionError.class)
  public void testWrongRow() {
    Column column = (Column) RowListFactory.createRowList(ORIGINAL_HEADER);
    column.addRow(FIRST_ROW);
    column.addRow(WRONG_ROW);
    ColumnChecker.check(column, ORIGINAL_HEADER,
        new String[]{FIRST_ROW, SECOND_ROW});
  }
}
