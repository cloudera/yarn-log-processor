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

import com.cloudera.bundleprocessor.subshell.format.Printable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class TestGrepperFactory {

  private static final String[] FRUIT_HEADER = new String[]{"FRUITS", "AMOUNT"};
  private static final String FRUIT = "fruit";
  private static final String NUMBER = "number";
  private static final String[] RAW_HEADER = new String[]{"RAW"};
  // Empty String will match on whole lines
  // (see GrepperFactory#createGroupFilter())
  private static final String[] RAW_GROUPNAME = new String[]{""};
  private static final String SAMPLE_TEXT =
      "There are 5 oranges, 7 apples and 1 watermelon";
  private static final String PATTERN_STRING = "(?<" + FRUIT + ">(\\d+)) (?<"
      + NUMBER + ">([a-z]+))";
  private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);

  private static String[] fruitFilterer(Matcher matcher) {
    return new String[]{matcher.group(FRUIT), matcher.group(NUMBER)};
  }

  private static String[] rawFilterer(Matcher matcher) {
    return new String[]{matcher.group()};
  }

  @Test
  public void testFruitGrepper() {
    Grepper actualGrepper = GrepperFactory.createGrepper(
        new GrepperFactory.Column(FRUIT_HEADER[0], FRUIT),
        new GrepperFactory.Column(FRUIT_HEADER[1], NUMBER));
    GrepperFunction grepperFunction =
        new GrepperFunction(FRUIT_HEADER, TestGrepperFactory::fruitFilterer);
    Grepper expectedGrepper = new Grepper(grepperFunction);
    assertSameGrepper(actualGrepper, expectedGrepper);
  }

  @Test
  public void testRawGrepper() {
    Grepper actualGrepper = GrepperFactory.createGrepper(
        new GrepperFactory.Column(RAW_HEADER[0], RAW_GROUPNAME[0]));
    GrepperFunction grepperFunction =
        new GrepperFunction(RAW_HEADER, TestGrepperFactory::rawFilterer);
    Grepper expectedGrepper = new Grepper(grepperFunction);
    assertSameGrepper(actualGrepper, expectedGrepper);
  }

  private void assertSameGrepper(Grepper actual, Grepper expected) {
    Printable actualOutput = createOutput(actual);
    Printable expectedOutput = createOutput(expected);
    assertSamePrintable(actualOutput, expectedOutput);
  }


  private Printable createOutput(Grepper grepper) {
    initGrepper(grepper);
    List<Matcher> matchers = new ArrayList<>();
    Matcher matcher = PATTERN.matcher(SAMPLE_TEXT);
    matchers.add(matcher);
    return grepper.format(matchers);
  }

  // This happens in SingleExecutable,
  // but we need to add deafult values to make greppers work properly
  private void initGrepper(Grepper grepper) {
    FormatterModifiers formatterModifiers = new FormatterModifiers.Builder()
        .setKeyParameter(null)
        .setSeparatingBySourceFile(false)
        .build();
    grepper.init(formatterModifiers);
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
