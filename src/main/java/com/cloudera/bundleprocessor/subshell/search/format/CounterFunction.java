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

import java.util.function.Function;
import java.util.regex.Matcher;

public class CounterFunction extends FormatterFunction<Integer> {

  private final Function<Integer[], String[]> finalizer;

  /**
   * CounterFunction contains the Functions of the Counter.
   *
   * @param header    header String
   * @param filterer  this Function can read a Matcher object and
   *                  count something in it
   * @param finalizer this Function finalizes the output
   *                  (adds extra message for the user)
   */
  public CounterFunction(String[] header, Function<Matcher, Integer[]> filterer,
                         Function<Integer[], String[]> finalizer) {
    super(header, filterer);
    this.finalizer = finalizer;
  }

  public Function<Integer[], String[]> getFinalizer() {
    return finalizer;
  }
}
