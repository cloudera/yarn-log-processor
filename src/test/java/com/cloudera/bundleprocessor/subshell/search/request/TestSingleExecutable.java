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

package com.cloudera.bundleprocessor.subshell.search.request;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cloudera.bundleprocessor.subshell.format.Printable;
import com.cloudera.bundleprocessor.subshell.format.StringPrintable;
import com.cloudera.bundleprocessor.subshell.search.engine.Query;
import com.cloudera.bundleprocessor.subshell.search.engine.SearchEngine;
import com.cloudera.bundleprocessor.subshell.search.format.Formatter;
import com.cloudera.bundleprocessor.subshell.search.format.GrepperFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;

public class TestSingleExecutable {

  private static final Pattern PATTERN = Pattern.compile(".*");
  private static final Formatter<?> FORMATTER = GrepperFactory.createGrepper(
      new GrepperFactory.Column("HEADER", "content"));
  private static final String RANDOM_STRING = "randomString";

  @Test(expected = IllegalStateException.class)
  public void testEmptyBuilder() {
    new SingleExecutable.Builder()
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void testEmptyPattern() {
    new SingleExecutable.Builder()
        .withFormatter(FORMATTER)
        .isCheckingRmLogs()
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void testEmptyTarget() {
    new SingleExecutable.Builder()
        .withFormatter(FORMATTER)
        .withPattern(PATTERN)
        .build();
  }

  @Test(expected = IllegalStateException.class)
  public void testEmptyFormatter() {
    new SingleExecutable.Builder()
        .withPattern(PATTERN)
        .isCheckingRmLogs()
        .build();
  }

  @Test
  public void testValidSingleExecutableBuilder() throws IOException {
    SearchEngine searchEngineMock = mock(SearchEngine.class);
    List<Matcher> matchers =
        Arrays.asList(PATTERN.matcher(RANDOM_STRING),
            PATTERN.matcher(RANDOM_STRING));
    when(searchEngineMock.createMatchers(any())).thenReturn(matchers);
    Formatter<?> formatterMock = mock(Formatter.class);
    Printable printable = new StringPrintable(RANDOM_STRING);
    when(formatterMock.format(any())).thenReturn(printable);
    SingleExecutable singleExecutable = new SingleExecutable.Builder()
        .withFormatter(formatterMock)
        .withPattern(PATTERN)
        .isCheckingRmLogs()
        .build();
    singleExecutable.execute(searchEngineMock);
    Query query = new Query.Builder()
        .withPattern(PATTERN)
        .isCheckingRmLogs()
        .build();
    verify(searchEngineMock).createMatchers(query);
    verify(formatterMock).format(matchers);
    assertEquals(printable, singleExecutable.getPrintable());
  }
}
