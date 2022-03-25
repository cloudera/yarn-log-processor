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

package com.cloudera.bundleprocessor.preprocessor.cliparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.cloudera.bundleprocessor.preprocessor.inputprocessor.DirectUrlInputProcessor;
import com.cloudera.bundleprocessor.preprocessor.inputprocessor.LocalFileInputProcessor;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;

public class TestCliParser {
  private CliParser parser;

  /**
   * Creates a CliParser that is used by tests.
   */
  @Before
  public void setUp() {
    parser = new CliParser();
    // printHelpMessage() should not throw exception
    parser.printHelpMessage();
  }

  @Test
  public void testParseWithoutReqOption() {
    String[] input = {""};
    try {
      parser.read(input);
      fail("ParseException should have been thrown");
    } catch (ParseException expected) {
    }
  }

  @Test
  public void testParseMultipleInputOptions() throws ParseException {
    String[] input = {"-o", "folder1", "-l", "folder2", "--keep",
        "-u", "https://test.com", "-s"};
    try {
      parser.read(input);
      fail("ParseException should have been thrown because "
          + "multiple input options are provided simultaneously.");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void testParseLocalFile() throws ParseException {
    String[] input = {"-o", "folder1", "-l", "folder2", "--keep", "-s"};
    InputParams params = parser.read(input);
    assertEquals("folder1", params.getMainDirectory().getName());
    assertTrue("The processor should be of type " +
            LocalFileInputProcessor.class.toString(),
        params.getProcessor() instanceof LocalFileInputProcessor);
    assertTrue(
        "getKeepOriginalFile() should be true, as -k/--keep has been set",
        params.getKeepOriginalFile());
    assertTrue("isLaunchingShell() should be true, as -s/--shell has been set",
        params.getSearchIntent().isLaunchingShell());
  }

  @Test
  public void testParseWrongUrl() throws ParseException {
    String[] input = {"-o", "folder1", "-u", "not_valid_url", "-k"};
    try {
      parser.read(input);
      fail("IllegalArgumentException should have been thrown");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void testParseUrl() throws ParseException {
    String[] input = {"-o", "folder1", "-u", "https://test.com", "-k"};
    InputParams params = parser.read(input);
    assertEquals("folder1", params.getMainDirectory().getName());
    assertTrue("The processor should be of type "
            + DirectUrlInputProcessor.class.toString(),
        params.getProcessor() instanceof DirectUrlInputProcessor);
    assertTrue(
        "getKeepOriginalFile() should be true, as -k/--keep has been set",
        params.getKeepOriginalFile());
    assertFalse(
        "isLaunchingShell() should be false, as -s/--shell hasn't been set",
        params.getSearchIntent().isLaunchingShell());
  }
}
