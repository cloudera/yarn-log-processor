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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.cloudera.bundleprocessor.preprocessor.inputprocessor.DirectUrlInputProcessor;
import com.cloudera.bundleprocessor.preprocessor.inputprocessor.LocalFileInputProcessor;
import com.cloudera.bundleprocessor.subshell.context.SearchIntent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestInputParams {
  private static final File MAIN_DIRECTORY = new File("test");
  private static final File LOCAL_FILE = new File("local");
  private static final String COMMAND_STR = "--help";

  private static URL url;

  @BeforeClass
  public static void setUpClass() throws MalformedURLException {
    url = new URL("https://url.com");
  }

  @Test
  public void testMultipleInputParams() {
    try {
      new InputParams.Builder()
          .withMainDirectory(MAIN_DIRECTORY)
          .withLocalFile(LOCAL_FILE)
          .withDirectUrl(url)
          .withKeepOriginalFile(true)
          .withShell(true)
          .withCommand(COMMAND_STR)
          .build();
      fail("An IllegalArgumentException is expected "
          + "since multiple params are provided");
    } catch (IllegalArgumentException expected) {
    }
  }

  // test local file
  @Test
  public void testLocalFile() {
    InputParams params = new InputParams.Builder()
        .withMainDirectory(MAIN_DIRECTORY)
        .withLocalFile(LOCAL_FILE)
        .withKeepOriginalFile(true)
        .withCommand(COMMAND_STR)
        .build();
    assertEquals("The main directory does not match",
        MAIN_DIRECTORY, params.getMainDirectory());
    assertTrue("The processor should be of type " +
            LocalFileInputProcessor.class.toString(),
        params.getProcessor() instanceof LocalFileInputProcessor);
    assertTrue(params.getKeepOriginalFile());

    LocalFileInputProcessor localProcessor =
        (LocalFileInputProcessor) params.getProcessor();
    assertEquals("The LocalFileInputProcessor has different local file "
            + "than the input parameter.",
        LOCAL_FILE, localProcessor.getLocalFile());

    SearchIntent intent = params.getSearchIntent();
    assertNotNull("The SearchIntent should not be null", intent);
    assertEquals("Found different command string in SearchIntent",
        COMMAND_STR, intent.getCommand());
    assertFalse(intent.isLaunchingShell());
  }

  @Test
  public void testURL() {
    InputParams params = new InputParams.Builder()
        .withMainDirectory(MAIN_DIRECTORY)
        .withDirectUrl(url)
        .withKeepOriginalFile(true)
        .build();
    assertEquals("The main directory does not match", MAIN_DIRECTORY,
        params.getMainDirectory());
    assertTrue("The processor should be of type " +
            DirectUrlInputProcessor.class.toString(),
        params.getProcessor() instanceof DirectUrlInputProcessor);
    assertTrue(params.getKeepOriginalFile());

    DirectUrlInputProcessor urlProcessor =
        (DirectUrlInputProcessor) params.getProcessor();
    assertEquals("The DirectUrlInputProcessor has different URL " +
        "than the input.", url, urlProcessor.getDirectUrl());
    assertEquals("The DirectUrlInputProcessor has different "
            + "main directory than the input.",
        MAIN_DIRECTORY, urlProcessor.getMainDirectory());

    SearchIntent intent = params.getSearchIntent();
    assertNotNull("The SearchIntent should not be null", intent);
    assertNull("The command string isn't provided in the builder!",
        intent.getCommand());
    assertFalse(intent.isLaunchingShell());
  }

  @Test
  public void testNoInputOption() {
    try {
      new InputParams.Builder()
          .withKeepOriginalFile(true)
          .withShell(true)
          .withCommand("")
          .build();
      fail("An IllegalArgumentException is expected "
          + "since multiple params are provided");
    } catch (IllegalArgumentException expected) {
    }
  }

  // test search intent construction
}
