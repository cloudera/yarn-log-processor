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


/**
 * RowListFactory is creating Output objects.
 */
public final class RowListFactory {

  private RowListFactory() {
  }

  /**
   * {@code createOutput} creates an Output object depending
   * on the number of headernames.
   *
   * @param headerNames String array containing the name of the headers
   * @return Output object
   */
  public static RowList createRowList(String... headerNames) {
    int size = headerNames.length;
    if (size == 1) {
      return new Column(headerNames);
    } else if (size > 1) {
      return new Table(headerNames);
    } else {
      throw new RuntimeException(
          "Empty headerNames was provided to define Output object");
    }
  }
}
