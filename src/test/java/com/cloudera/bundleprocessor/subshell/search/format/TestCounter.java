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

import com.cloudera.bundleprocessor.subshell.format.Column;
import com.cloudera.bundleprocessor.subshell.format.ComposedPrintable;
import com.cloudera.bundleprocessor.subshell.format.Printable;
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

public class TestCounter {

  private static final String[] HEADER = new String[]{"TESTING COUNTER"};
  private static final String PATTERN_STRING =
      "(?<number>(\\d+)) (?<fruit>([a-z]+))";
  private static final String[] SAMPLE_TEXTS = new String[]
      {"There are 12 apples and 3 oranges",
          "There are 5 oranges, 7 apples and 1 watermelon"};
  private static final String[] FINALIZER_STRINGS = new String[]
      {" - matches: ", " - twice of the matches: "};
  private static final Integer[] SUM_OF_NUMBERS_IN_SAMPLE_TEXTS =
      new Integer[]{15, 13};
  private static final Integer SUM_OF_FIRST_OCCURRENCES = 16;
  private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);

  private final List<Matcher> matchers = new ArrayList<>(Arrays.asList(
      PATTERN.matcher(SAMPLE_TEXTS[0]), PATTERN.matcher(SAMPLE_TEXTS[1])));
  private Counter counter;

  private static Integer[] filterer(Matcher matcher) {
    int numberOfMatches = Integer.parseInt(matcher.group("number"));
    return new Integer[]{numberOfMatches, 2 * numberOfMatches};
  }

  private static String[] finalizer(Integer[] counterValues) {
    return new String[]{
        FINALIZER_STRINGS[0] + counterValues[0],
        FINALIZER_STRINGS[1] + counterValues[1]
    };
  }

  /**
   * Initializes the Counter with a mocked CounterFunction.
   */
  @Before
  public void init() {
    CounterFunction counterFunction = mock(CounterFunction.class);
    when(counterFunction.getFinalizer()).thenReturn(TestCounter::finalizer);
    when(counterFunction.getFilterer()).thenReturn(TestCounter::filterer);
    when(counterFunction.getHeader()).thenReturn(HEADER);
    counter = new Counter(counterFunction);
  }

  @Test
  public void testFormatTogether() {
    Printable printable = initFormatTogether();
    checkFormatTogether(printable);
  }

  @Test
  public void testFormatSeparately() {
    List<Printable> printables = initFormatSeparately();
    checkFormatSeparately(printables);
  }

  @Test
  public void testKeyParameter() {
    Printable printable = initKeyParameter();
    checkKeyParameter(printable);
  }

  private Printable initFormatTogether() {
    FormatterModifiers formatterModifiers = mock(FormatterModifiers.class);
    when(formatterModifiers.isSeparatingBySourceFile()).thenReturn(false);
    counter.init(formatterModifiers);
    return counter.format(matchers);
  }

  private List<Printable> initFormatSeparately() {
    FormatterModifiers formatterModifiers = mock(FormatterModifiers.class);
    when(formatterModifiers.isSeparatingBySourceFile()).thenReturn(true);
    counter.init(formatterModifiers);
    Printable printable = counter.format(matchers);
    assertTrue("The Counter object should return a ComposedPrintable",
        printable instanceof ComposedPrintable);
    ComposedPrintable composedPrintable = (ComposedPrintable) printable;
    return composedPrintable.getPrintables();
  }

  private Printable initKeyParameter() {
    FormatterModifiers formatterModifiers = mock(FormatterModifiers.class);
    when(formatterModifiers.isSeparatingBySourceFile()).thenReturn(false);
    when(formatterModifiers.getKeyParameter()).thenReturn("fruit");
    counter.init(formatterModifiers);
    return counter.format(matchers);
  }

  private void checkFormatTogether(Printable printable) {
    assertTrue(printable instanceof Column);
    Column column = (Column) printable;
    List<String> rows = column.getRows();
    assertEquals(rows.size(), 2);
    int numberOfMatchesAcrossAllTexts =
        SUM_OF_NUMBERS_IN_SAMPLE_TEXTS[0] + SUM_OF_NUMBERS_IN_SAMPLE_TEXTS[1];
    assertTrue(rows.get(0).contentEquals(
        FINALIZER_STRINGS[0] + numberOfMatchesAcrossAllTexts));
    assertTrue(rows.get(1).contentEquals(
        FINALIZER_STRINGS[1] + 2 * numberOfMatchesAcrossAllTexts));
  }

  private void checkFormatSeparately(List<Printable> printables) {
    assertEquals(2, printables.size());
    for (int i = 0; i < 2; i++) {
      assertTrue("Printables in testFormatSeparately expected to be Columns",
          printables.get(i) instanceof Column);
      Column column = (Column) printables.get(i);
      List<String> rows = column.getRows();
      String expectedFirstRow =
          FINALIZER_STRINGS[0] + SUM_OF_NUMBERS_IN_SAMPLE_TEXTS[i];
      String expectedSecondRow =
          FINALIZER_STRINGS[1] + (2 * SUM_OF_NUMBERS_IN_SAMPLE_TEXTS[i]);
      assertEquals(expectedFirstRow, rows.get(0));
      assertEquals(expectedSecondRow, rows.get(1));
    }
  }

  private void checkKeyParameter(Printable printable) {
    assertTrue(printable instanceof Column);
    Column column = (Column) printable;
    List<String> rows = column.getRows();
    assertEquals(rows.size(), 2);
    // Here the output amount will contain the number
    // of fruits at their first occurrence
    // so when we encountered apples second time
    // in the logs it was not added to the sum
    Integer numberOfMatchesAcrossAllTexts = SUM_OF_FIRST_OCCURRENCES;
    assertTrue(rows.get(0).contentEquals(
        FINALIZER_STRINGS[0] + numberOfMatchesAcrossAllTexts));
    assertTrue(rows.get(1).contentEquals(
        FINALIZER_STRINGS[1] + 2 * numberOfMatchesAcrossAllTexts));
  }
}
