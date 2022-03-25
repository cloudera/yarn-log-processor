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

/**
 * FormatterFunction is defining the methods to generate
 * the output of a {@class Formatter}.
 * (the output is a Printable object)
 * FormatterModifiers only performs minor changes on the output.
 * We create a {@link Formatter} from a FormatterFunction object and
 * initialize it with {@link FormatterModifiers}.
 *
 * @param <T> T is the intermediate representation of the FormatterFunction
 */
public abstract class FormatterFunction<T> {

  private final String[] header;
  private final Function<Matcher, T[]> filterer;

  protected FormatterFunction(String[] header,
                              Function<Matcher, T[]> filterer) {
    this.header = header;
    this.filterer = filterer;
  }

  String[] getHeader() {
    return header;
  }

  Function<Matcher, T[]> getFilterer() {
    return filterer;
  }
}
