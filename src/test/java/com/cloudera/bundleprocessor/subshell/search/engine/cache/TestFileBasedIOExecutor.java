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

package com.cloudera.bundleprocessor.subshell.search.engine.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Serializable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFileBasedIOExecutor {

  private static final String TARGET_DIRECTORY_PATH = "src/test/cachebinaries";
  private static FileBasedCacheIOExecutor fileExecutor;

  @BeforeClass
  public static void setIOExecutor() {
    fileExecutor = new FileBasedCacheIOExecutor(TARGET_DIRECTORY_PATH);
  }

  @Before
  public void resetFileExecutor() throws IOException {
    fileExecutor.removeAll();
  }

  @Test
  public void testWriteAndRead() throws IOException {
    setAndCheck(CacheTestItems.KEY_1, CacheTestItems.VALUE_1);
  }

  @Test
  public void testWriteAndReadMultipleValues() throws IOException {
    setAndCheck(CacheTestItems.KEY_1, CacheTestItems.VALUE_1);
    setAndCheck(CacheTestItems.KEY_2, CacheTestItems.VALUE_2);
    setAndCheck(CacheTestItems.KEY_3, CacheTestItems.VALUE_3);
    checkKey(CacheTestItems.KEY_1, CacheTestItems.VALUE_1);
    checkKey(CacheTestItems.KEY_2, CacheTestItems.VALUE_2);
  }

  @Test
  public void testRemove() throws IOException {
    setAndCheck(CacheTestItems.KEY_1, CacheTestItems.VALUE_1);
    removeAndCheck(CacheTestItems.KEY_1);
    setAndCheck(CacheTestItems.KEY_2, CacheTestItems.VALUE_2);
    removeAndCheck(CacheTestItems.KEY_2);
    setAndCheck(CacheTestItems.KEY_3, CacheTestItems.VALUE_3);
    removeAndCheck(CacheTestItems.KEY_3);
  }

  @Test
  public void removeAll() throws IOException {
    setAndCheck(CacheTestItems.KEY_1, CacheTestItems.VALUE_1);
    setAndCheck(CacheTestItems.KEY_2, CacheTestItems.VALUE_2);
    setAndCheck(CacheTestItems.KEY_3, CacheTestItems.VALUE_3);
    fileExecutor.removeAll();
    checkNonExistentKey(CacheTestItems.KEY_1);
    checkNonExistentKey(CacheTestItems.KEY_2);
    checkNonExistentKey(CacheTestItems.KEY_3);
  }

  private void setAndCheck(Object key, Serializable value) throws IOException {
    checkNonExistentKey(key);
    fileExecutor.writeItem(key, value);
    checkKey(key, value);
  }

  private void removeAndCheck(DummyKey key) {
    fileExecutor.remove(key);
    checkNonExistentKey(key);
  }

  private void checkNonExistentKey(Object key) {
    try {
      fileExecutor.readItem(key);
      fail("Found unexpected file saving value for the specified key");
    } catch (IOException expected) {
    }
  }

  private void checkKey(Object key, Serializable value) throws IOException {
    Serializable cachedValue = fileExecutor.readItem(key);
    assertNotNull("Null value red from the file", cachedValue);
    assertEquals("Unexpected value red from the file", value, cachedValue);
  }
}
