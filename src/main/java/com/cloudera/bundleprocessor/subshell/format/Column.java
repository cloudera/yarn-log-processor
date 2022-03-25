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
import com.google.common.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

/**
 * Column is responsible for displaying a list in the subshell.
 * Add header through the constructor.
 * Add rows by {@code setRow()}.
 * After that, draw the table by {@code drawTable()}.
 */
public class Column extends RowList {

  private final List<String> rows = new ArrayList<>();

  protected Column(String[] header) {
    super(header);
  }

  @Override
  protected void addRowInternal(String[] rowElements) {
    rows.add(rowElements[0]);
  }

  @Override
  protected int getSize() {
    return 1;
  }

  /**
   * The {@code print()} function displays the list in the terminal.
   */
  @Override
  public String print() {
    StringBuilder stringBuilder = new StringBuilder();
    if (rows.size() > 0) {
      stringBuilder.append(this.header[0]).append("\n");
      for (String row : this.rows) {
        stringBuilder.append(row).append("\n");
      }
    } else {
      return Constants.EMPTY_OUTPUT_MESSAGE;
    }
    return stringBuilder.toString();
  }

  @VisibleForTesting
  public List<String> getRows() {
    return rows;
  }
}