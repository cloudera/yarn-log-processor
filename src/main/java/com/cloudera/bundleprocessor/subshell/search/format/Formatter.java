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

import com.cloudera.bundleprocessor.subshell.format.ComposedPrintable;
import com.cloudera.bundleprocessor.subshell.format.Printable;

import java.util.List;
import java.util.regex.Matcher;

/**
 * Formatter is responsible to transform list of {@class Matcher}s
 * into a {@class Printable}.
 *
 * @param <T> is FormatterFunction, defines the transformation
 */
public abstract class Formatter<T extends FormatterFunction<?>> {

  private final T formatterFunction;
  private FormatterModifiers formatterModifiers;

  protected Formatter(T formatterFunction) {
    this.formatterFunction = formatterFunction;
  }

  public void init(FormatterModifiers formatterModifiers) {
    this.formatterModifiers = formatterModifiers;
  }

  protected abstract ComposedPrintable formatSeparately(List<Matcher> matchers);

  protected abstract Printable formatTogether(List<Matcher> matchers);

  /**
   * {@code format} function is called after SearchEngine created
   * the Matchers for a SingleExecutable.
   * It filters out the relevant information and
   * creates a readable output for the user.
   *
   * @param matchers matches with the important lines in the logs
   * @return Printable object (user readable output)
   */
  public Printable format(List<Matcher> matchers) {
    if (formatterModifiers.isSeparatingBySourceFile()) {
      return formatSeparately(matchers);
    } else {
      return formatTogether(matchers);
    }
  }

  protected boolean keepMatch(String searchedRegexGroup,
                              Matcher matcher, List<String> foundOnes) {
    if (searchedRegexGroup == null) {
      return true;
    } else {
      String justFound = matcher.group(searchedRegexGroup);
      if (foundOnes.contains(justFound)) {
        return false;
      } else {
        foundOnes.add(justFound);
        return true;
      }
    }
  }

  public T getFormatterFunction() {
    return formatterFunction;
  }

  public FormatterModifiers getFormatterModifiers() {
    return formatterModifiers;
  }
}
