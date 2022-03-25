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

package com.cloudera.bundleprocessor.subshell.search.format;

import org.junit.Test;

import java.util.function.Function;
import java.util.regex.Matcher;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestCounterFunction {
  @Test
  public void testCounterFunctionFields() {
    String[] header = {"column", "test"};
    Function<Matcher, Integer[]> filterer =
        (m) -> new Integer[]{2, 3};
    Function<Integer[], String[]> finalizer =
        (m) -> new String[]{"some", "string"};

    CounterFunction cf = new CounterFunction(header, filterer, finalizer);
    assertArrayEquals(header, cf.getHeader());
    assertEquals(finalizer, cf.getFinalizer());
    assertEquals(filterer, cf.getFilterer());
  }
}
