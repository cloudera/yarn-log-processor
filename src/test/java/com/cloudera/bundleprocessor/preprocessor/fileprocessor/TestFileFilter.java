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

package com.cloudera.bundleprocessor.preprocessor.fileprocessor;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestFileFilter {


  public static final String MATCHING_ALL = ".*";
  public static final String MATCHING_NONE = "regexmatchingnone";
  public static final String MATCHING_ONE = ".*_.*";
  public static final String MATCHING_SOME = ".*NODEMANAGER.*";
  private static final File SOURCE_DIR =
      new File("./src/test/resources/workspace");
  private static final File WORKSPACE = new File("./src/test/resources");
  private static final String TARGET_DIR_NAME = "testfilefilter";
  private static final File TARGET_DIR = new File(WORKSPACE, TARGET_DIR_NAME);
  private static final FileFilter FILE_FILTER =
      new FileFilter(SOURCE_DIR, WORKSPACE);
  private static final String NODEMANAGER = "NODEMANAGER.log";
  private static final String NODEMANAGER2 = "NODEMANAGER2.log";
  private static final String TEST_TEXT = "TEST_TEXT.log";
  private static final String RESOURCEMANAGER = "RESOURCEMANAGER.log";

  @AfterClass
  public static void emptyTargetDirAfter() throws IOException {
    FileUtils.deleteDirectory(TARGET_DIR.getAbsolutePath());
  }

  @Before
  public void emptyTargetDir() throws IOException {
    FileUtils.deleteDirectory(TARGET_DIR.getAbsolutePath());
  }

  @Test
  public void testFilteringAll() throws IOException {
    FILE_FILTER.filter(MATCHING_ALL, TARGET_DIR_NAME);
    List<String> listOfFiles = Arrays.asList(TARGET_DIR.list());
    assertTrue(listOfFiles.contains(NODEMANAGER));
    assertTrue(listOfFiles.contains(NODEMANAGER2));
    assertTrue(listOfFiles.contains(TEST_TEXT));
    assertTrue(listOfFiles.contains(RESOURCEMANAGER));
  }

  @Test
  public void testFilteringNone() throws IOException {
    FILE_FILTER.filter(MATCHING_NONE, TARGET_DIR_NAME);
    List<String> listOfFiles = Arrays.asList(TARGET_DIR.list());
    assertFalse(listOfFiles.contains(NODEMANAGER));
    assertFalse(listOfFiles.contains(NODEMANAGER2));
    assertFalse(listOfFiles.contains(TEST_TEXT));
    assertFalse(listOfFiles.contains(RESOURCEMANAGER));
  }

  @Test
  public void testFilteringOne() throws IOException {
    FILE_FILTER.filter(MATCHING_ONE, TARGET_DIR_NAME);
    List<String> listOfFiles = Arrays.asList(TARGET_DIR.list());
    assertFalse(listOfFiles.contains(NODEMANAGER));
    assertFalse(listOfFiles.contains(NODEMANAGER2));
    assertTrue(listOfFiles.contains(TEST_TEXT));
    assertFalse(listOfFiles.contains(RESOURCEMANAGER));
  }

  @Test
  public void testFilteringSome() throws IOException {
    FILE_FILTER.filter(MATCHING_SOME, TARGET_DIR_NAME);
    List<String> listOfFiles = Arrays.asList(TARGET_DIR.list());
    assertTrue(listOfFiles.contains(NODEMANAGER));
    assertTrue(listOfFiles.contains(NODEMANAGER2));
    assertFalse(listOfFiles.contains(TEST_TEXT));
    assertFalse(listOfFiles.contains(RESOURCEMANAGER));
  }
}
