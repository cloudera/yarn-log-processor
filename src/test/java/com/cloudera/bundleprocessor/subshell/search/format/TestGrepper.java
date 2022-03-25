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
import com.cloudera.bundleprocessor.subshell.format.RowList;
import com.cloudera.bundleprocessor.subshell.format.RowListFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestGrepper {

  private static final String[] HEADER = new String[]{"FRUITS", "AMOUNT"};
  private static final String PATTERN_STRING =
      "(?<number>(\\d+)) (?<fruit>([a-z]+))";
  private static final String[] SAMPLE_TEXTS =
      new String[]{"There are 12 apples and 3 oranges",
          "There are 5 oranges, 7 apples and 1 watermelon"};
  private static final List<String[]> OUTPUT_OF_FIRST_TEXT =
      Arrays.asList(new String[]{"apples", "12"}, new String[]{"oranges", "3"});
  private static final List<String[]> OUTPUT_OF_SECOND_TEXT =
      Arrays.asList(new String[]{"oranges", "5"}, new String[]{"apples", "7"},
          new String[]{"watermelon", "1"});
  private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);

  private final List<Matcher> matchers = new ArrayList<>(Arrays.asList(
      PATTERN.matcher(SAMPLE_TEXTS[0]), PATTERN.matcher(SAMPLE_TEXTS[1])));
  private Grepper grepper;

  private static String[] filterer(Matcher matcher) {
    return new String[]{matcher.group("fruit"), matcher.group("number")};
  }

  @Before
  public void init() {
    GrepperFunction grepperFunction =
        new GrepperFunction(HEADER, TestGrepper::filterer);
    grepper = new Grepper(grepperFunction);
  }

  @Test
  public void testFormatTogether() {
    FormatterModifiers formatterModifiers = new FormatterModifiers.Builder()
        .setKeyParameter(null)
        .setSeparatingBySourceFile(false)
        .build();
    grepper.init(formatterModifiers);
    Printable actualPrintable = grepper.format(matchers);
    RowList expectedPrintable = RowListFactory.createRowList(HEADER);
    for (String[] row : OUTPUT_OF_FIRST_TEXT) {
      expectedPrintable.addRow(row);
    }
    for (String[] row : OUTPUT_OF_SECOND_TEXT) {
      expectedPrintable.addRow(row);
    }
    assertSamePrintable(actualPrintable, expectedPrintable);
  }

  @Test
  public void testFormatSeparately() {
    FormatterModifiers formatterModifiers = new FormatterModifiers.Builder()
        .setKeyParameter(null)
        .setSeparatingBySourceFile(true)
        .build();
    grepper.init(formatterModifiers);
    Printable printable = grepper.format(matchers);
    assertTrue(
        "The Grepper object should return a ComposedPrintable",
        printable instanceof ComposedPrintable);
    List<RowList> expectedPrintables = new ArrayList<>();
    expectedPrintables.add(RowListFactory.createRowList(HEADER));
    expectedPrintables.add(RowListFactory.createRowList(HEADER));
    for (String[] row : OUTPUT_OF_FIRST_TEXT) {
      expectedPrintables.get(0).addRow(row);
    }
    for (String[] row : OUTPUT_OF_SECOND_TEXT) {
      expectedPrintables.get(1).addRow(row);
    }
    ComposedPrintable composedPrintable = (ComposedPrintable) printable;
    List<Printable> actualPrintables = composedPrintable.getPrintables();
    for (int i = 0; i < actualPrintables.size(); i++) {
      assertSamePrintable(actualPrintables.get(i), expectedPrintables.get(i));
    }
  }

  @Test
  public void testKeyParameter() {
    FormatterModifiers formatterModifiers = mock(FormatterModifiers.class);
    when(formatterModifiers.isSeparatingBySourceFile()).thenReturn(false);
    when(formatterModifiers.getKeyParameter()).thenReturn("fruit");
    grepper.init(formatterModifiers);
    RowList expectedPrintable = RowListFactory.createRowList(HEADER);
    expectedPrintable.addRow(new String[]{"apples", "12"});
    expectedPrintable.addRow(new String[]{"oranges", "3"});
    expectedPrintable.addRow(new String[]{"watermelon", "1"});
    Printable actualPrintable = grepper.format(matchers);
    assertSamePrintable(actualPrintable, expectedPrintable);
  }

  private void assertSamePrintable(Printable actual, Printable expected) {
    String actualOutput = actual.print();
    String expectedOutput = expected.print();
    String errorMsg = "Unexpected Output"
        + "\nActual:\n"
        + actualOutput
        + "\nExpected:\n"
        + expectedOutput;
    assertEquals(errorMsg, actualOutput, expectedOutput);
  }
}
