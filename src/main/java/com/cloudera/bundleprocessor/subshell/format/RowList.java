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

import com.google.common.base.Preconditions;

/**
 * RowList is an abstract class for Printable object containing rows.
 * New row can be appended to RowList with {@code addRow()} function.
 */
public abstract class RowList implements Printable {

  protected final String[] header;

  protected RowList(String[] header) {
    Preconditions.checkNotNull(header);
    this.header = header;
  }

  /**
   * Adds a row to the Output object if it was valid.
   *
   * @param rowElements String array representing a row
   */
  public void addRow(String[] rowElements) {
    if (rowElements.length == this.getSize()) {
      addRowInternal(rowElements);
    } else {
      throw new RuntimeException("The provided row is not matching " +
          "with the size of the Output object");
    }
  }

  /**
   * Adds a row to the Output object if it was valid.
   *
   * @param rowElement single String representing a row with single cell
   */
  public void addRow(String rowElement) {
    addRow(new String[]{rowElement});
  }

  protected abstract void addRowInternal(String[] rowElements);

  protected abstract int getSize();
}
