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

package com.cloudera.bundleprocessor.subshell.search.engine.cache;

import java.util.ArrayList;
import java.util.Arrays;

public final class CacheTestItems {

  private CacheTestItems() {
  }

  static final DummyKey KEY_1 = new DummyKey(1);
  static final DummyKey KEY_2 = new DummyKey(2);
  static final DummyKey KEY_3 = new DummyKey(3);
  static final DummyKey KEY_4 = new DummyKey(4);
  static final String VALUE_ITEM_1 = "first\nsecond";
  static final String VALUE_ITEM_2 = "third\nfourth";
  static final LinesOfLogs VALUE_1 = new LinesOfLogs(
      new ArrayList<>(Arrays.asList(VALUE_ITEM_1, VALUE_ITEM_1)));
  static final LinesOfLogs VALUE_2 = new LinesOfLogs(
      new ArrayList<>(Arrays.asList(VALUE_ITEM_1, VALUE_ITEM_2)));
  static final LinesOfLogs VALUE_3 = new LinesOfLogs(
      new ArrayList<>(Arrays.asList(VALUE_ITEM_2, VALUE_ITEM_1)));
  static final LinesOfLogs VALUE_4 = new LinesOfLogs(
      new ArrayList<>(Arrays.asList(VALUE_ITEM_2, VALUE_ITEM_2)));
}
