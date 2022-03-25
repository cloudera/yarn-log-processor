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
import com.cloudera.bundleprocessor.subshell.format.RowList;
import com.cloudera.bundleprocessor.subshell.format.RowListFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Counter is a {@link Formatter},
 * it aggregates Matcher objects based on different attributes.
 * The output is usually one or more counter value (integer),
 * decorated with a predefined context to improve readability
 */
public class Counter extends Formatter<CounterFunction> {

  public Counter(CounterFunction counterFunction) {
    super(counterFunction);
  }

  @Override
  protected ComposedPrintable formatSeparately(List<Matcher> matchers) {
    ComposedPrintable composedPrintable = new ComposedPrintable();
    for (Matcher matcher : matchers) {
      RowList output = formatOne(matcher);
      composedPrintable.addPrintable(output);
    }
    return composedPrintable;
  }

  private RowList formatOne(Matcher matcher) {
    Integer[] counterValues = new Integer[100];
    List<String> foundOnes = new ArrayList<>();
    while (matcher.find()) {
      counterValues = executeMatch(matcher, counterValues, foundOnes);
    }
    return generateRowList(counterValues);
  }

  @Override
  protected RowList formatTogether(List<Matcher> matchers) {
    Integer[] counterValues = new Integer[100];
    List<String> foundKeys = new ArrayList<>();
    for (Matcher matcher : matchers) {
      while (matcher.find()) {
        counterValues = executeMatch(matcher, counterValues, foundKeys);
      }
    }
    return generateRowList(counterValues);
  }

  private Integer[] executeMatch(
      Matcher matcher, Integer[] counterValues, List<String> foundKeys) {
    Integer[] newValues = getFormatterFunction().getFilterer().apply(matcher);
    if (keepMatch(getFormatterModifiers().getKeyParameter(),
        matcher, foundKeys)) {
      if (counterValues[0] == null) {
        counterValues = newValues;
      } else {
        for (int i = 0; i < counterValues.length; i++) {
          counterValues[i] += newValues[i];
        }
      }
    }
    return counterValues;
  }

  private RowList generateRowList(Integer[] summedValues) {
    String[] rows = getFormatterFunction().getFinalizer().apply(summedValues);
    RowList output =
        RowListFactory.createRowList(getFormatterFunction().getHeader());
    for (String row : rows) {
      output.addRow(row);
    }
    return output;
  }
}