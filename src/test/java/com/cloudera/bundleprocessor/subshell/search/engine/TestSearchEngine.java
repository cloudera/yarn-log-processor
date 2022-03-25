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

package com.cloudera.bundleprocessor.subshell.search.engine;

import com.cloudera.bundleprocessor.subshell.context.Config;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.Cache;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.CacheForTest;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.LinesOfLogs;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestSearchEngine {
  private static final String PATTERN_STR = "TEST.*TEXT";
  private static final Pattern PATTERN = Pattern.compile(PATTERN_STR);
  private static final Config CONFIG = Config.createConfig(
      new File("src/test/resources", "configForTest.json"));
  private static Cache<Query, LinesOfLogs> cache;
  private static SearchEngine engine;

  /**
   * Initializes the search engine.
   */
  @BeforeClass
  public static void setUpClass() {
    engine = new SearchEngine(new File("src/test/resources"));
    cache = new CacheForTest<>();
    engine.init(CONFIG, cache);
  }

  @Before
  public void resetCache() {
    cache.reset();
  }

  @Test
  public void testSearchingNmLogs() throws IOException {
    Query query = new Query.Builder()
        .isCheckingNmLogs()
        .withPattern(PATTERN)
        .build();

    List<Matcher> matchers = engine.createMatchers(query);

    assertEquals("expected two NodeManager log files", 2, matchers.size());
    int counter = 0;
    for (Matcher matcher : matchers) {
      assertEquals(PATTERN_STR, matcher.pattern().toString());
      while (matcher.find()) {
        counter++;
      }
    }
    assertEquals(
        "expected 2 match from NODEMANAGER.log and one from NODEMANAGER2.log",
        3, counter);
    assertNotNull(cache.get(query));
    List<String> expectedOutput =
        Arrays.asList("TEST_22_TEXT\nTESTTEXT\n", "TEST_SOME_TEXT\n");
    List<String> actualOutput = cache.get(query).getLines();
    assertListEquals(expectedOutput, actualOutput);
  }

  @Test
  public void testSearchingRmLogs() throws IOException {
    Query query = new Query.Builder()
        .isCheckingRmLogs()
        .withPattern(PATTERN)
        .build();

    List<Matcher> matchers = engine.createMatchers(query);

    assertEquals("expected one ResourceManager log file", 1, matchers.size());
    int counter = 0;
    for (Matcher matcher : matchers) {
      assertEquals(PATTERN_STR, matcher.pattern().toString());
      while (matcher.find()) {
        counter++;
      }
    }
    assertEquals(
        "expected two matches in the RESOURCEMANAGER.log file", 2, counter);
    assertNotNull(cache.get(query));
    List<String> expectedOutput =
        Collections.singletonList("TEST_11_TEXT\nTESTTEXT\n");
    List<String> actualOutput = cache.get(query).getLines();
    assertListEquals(expectedOutput, actualOutput);
  }

  @Test
  public void testFileNames() throws IOException {
    Query query = new Query.Builder()
        .isCheckingFileNames()
        .withPattern(PATTERN)
        .build();

    List<Matcher> matchers = engine.createMatchers(query);

    assertEquals("expected 4 files in the directory", 4, matchers.size());
    int counter = 0;
    for (Matcher matcher : matchers) {
      assertEquals(PATTERN_STR, matcher.pattern().toString());
      while (matcher.find()) {
        counter++;
      }
      matcher.reset();
    }
    assertEquals("expected one match among the file names", 1, counter);
    assertNotNull(cache.get(query));
    List<String> expectedOutput = Collections.singletonList("TEST_TEXT\n");
    List<String> actualOutput = cache.get(query).getLines();
    assertListEquals(expectedOutput, actualOutput);
  }

  @Test
  public void testReadingCache() throws IOException {
    Query query = new Query.Builder()
        .isCheckingRmLogs()
        .withPattern(PATTERN)
        .build();
    List<String> expectedOutput =
        Arrays.asList("TEST_READING_TEXT\nTEST__TEXT\n", "TEST_CACHE_TEXT\n");
    cache.set(query, new LinesOfLogs(expectedOutput));
    assertNotNull(cache.get(query));
    List<Matcher> matchers = engine.createMatchers(query);
    List<String> actualOutput = createLines(matchers);
    assertListEquals(expectedOutput, actualOutput);
  }

  private List<String> createLines(List<Matcher> matchers) {
    List<String> matchedLines = new ArrayList<>();
    for (Matcher matcher : matchers) {
      StringBuilder stringBuilder = new StringBuilder();
      while (matcher.find()) {
        stringBuilder.append(matcher.group()).append("\n");
      }
      if (stringBuilder.length() != 0) {
        matchedLines.add(stringBuilder.toString());
      }
      matcher.reset();
    }
    return matchedLines;
  }

  private void assertListEquals(
      List<String> expectedOutput, List<String> actualOutput) {
    assertEquals(expectedOutput.size(), actualOutput.size());
    for (int i = 0; i < expectedOutput.size(); i++) {
      assertEquals(expectedOutput.get(i), actualOutput.get(i));
    }
  }
}
