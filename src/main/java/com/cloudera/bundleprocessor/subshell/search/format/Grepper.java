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
 * Grepper is a {@link Formatter}, it greping some information
 * from Matcher objects and creates output accordingly.
 */
public class Grepper extends Formatter<GrepperFunction> {

  Grepper(GrepperFunction grepperFunction) {
    super(grepperFunction);
  }

  @Override
  protected ComposedPrintable formatSeparately(List<Matcher> matchers) {
    ComposedPrintable composedRowList = new ComposedPrintable();
    RowList output =
        RowListFactory.createRowList(getFormatterFunction().getHeader());
    List<String> foundOnes = new ArrayList<>();
    for (Matcher matcher : matchers) {
      while (matcher.find()) {
        formatMatch(matcher, output, foundOnes);
      }
      composedRowList.addPrintable(output);
      output = RowListFactory.createRowList(getFormatterFunction().getHeader());
    }
    return composedRowList;
  }

  @Override
  protected RowList formatTogether(List<Matcher> matchers) {
    RowList output = RowListFactory.
        createRowList(getFormatterFunction().getHeader());
    List<String> foundOnes = new ArrayList<>();
    for (Matcher matcher : matchers) {
      while (matcher.find()) {
        formatMatch(matcher, output, foundOnes);
      }
    }
    return output;
  }

  private void formatMatch(Matcher matcher,
                           RowList output, List<String> foundOnes) {
    String[] rowElements = getFormatterFunction().getFilterer().apply(matcher);
    if (keepMatch(getFormatterModifiers().getKeyParameter(),
        matcher, foundOnes)) {
      output.addRow(rowElements);
    }
  }
}
