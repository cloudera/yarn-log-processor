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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class TestGeneralCache {

  private final GeneralCache<DummyKey, LinesOfLogs> cache =
      new GeneralCache<>(new DummyIOExecutor());

  @Before
  public void resetCache() {
    cache.reset();
  }

  @Test
  public void testSettingValue() {
    setAndCheck(CacheTestItems.KEY_1, CacheTestItems.VALUE_1);
  }

  @Test
  public void testSettingMultipleValues() {
    setAndCheck(CacheTestItems.KEY_1, CacheTestItems.VALUE_1);
    setAndCheck(CacheTestItems.KEY_2, CacheTestItems.VALUE_2);
    setAndCheck(CacheTestItems.KEY_3, CacheTestItems.VALUE_3);
    setAndCheck(CacheTestItems.KEY_4, CacheTestItems.VALUE_4);
    assertTrue(compareValues(CacheTestItems.VALUE_3,
        cache.get(CacheTestItems.KEY_3)));
    assertTrue(compareValues(CacheTestItems.VALUE_4,
        cache.get(CacheTestItems.KEY_4)));
    assertTrue(compareValues(CacheTestItems.VALUE_1,
        cache.get(CacheTestItems.KEY_1)));
    assertTrue(compareValues(CacheTestItems.VALUE_2,
        cache.get(CacheTestItems.KEY_2)));
  }

  @Test
  public void testRemove() {
    setAndCheck(CacheTestItems.KEY_1, CacheTestItems.VALUE_1);
    setAndCheck(CacheTestItems.KEY_2, CacheTestItems.VALUE_2);
    setAndCheck(CacheTestItems.KEY_3, CacheTestItems.VALUE_3);
    setAndCheck(CacheTestItems.KEY_4, CacheTestItems.VALUE_4);
    cache.remove(CacheTestItems.KEY_1);
    assertNull(cache.get(CacheTestItems.KEY_1));
    cache.remove(CacheTestItems.KEY_3);
    assertNull(cache.get(CacheTestItems.KEY_3));
    cache.remove(CacheTestItems.KEY_4);
    assertNull(cache.get(CacheTestItems.KEY_4));
    cache.remove(CacheTestItems.KEY_2);
    assertNull(cache.get(CacheTestItems.KEY_2));
  }

  @Test
  public void testReset() {
    setAndCheck(CacheTestItems.KEY_1, CacheTestItems.VALUE_1);
    setAndCheck(CacheTestItems.KEY_2, CacheTestItems.VALUE_2);
    setAndCheck(CacheTestItems.KEY_3, CacheTestItems.VALUE_3);
    setAndCheck(CacheTestItems.KEY_4, CacheTestItems.VALUE_4);
    cache.reset();
    assertNull(cache.get(CacheTestItems.KEY_1));
    assertNull(cache.get(CacheTestItems.KEY_3));
    assertNull(cache.get(CacheTestItems.KEY_4));
    assertNull(cache.get(CacheTestItems.KEY_2));
  }

  private void setAndCheck(DummyKey key, LinesOfLogs expected) {
    assertNull("Found value, where null was expected", cache.get(key));
    cache.set(key, expected);
    assertNotNull("Found null, where value was expected", cache.get(key));
    LinesOfLogs actual = cache.get(key);
    assertTrue("The found value differs from expected",
        compareValues(expected, actual));
  }

  private boolean compareValues(LinesOfLogs expected, LinesOfLogs actual) {
    return Arrays.deepEquals(
        expected.getLines().toArray(), actual.getLines().toArray());
  }
}