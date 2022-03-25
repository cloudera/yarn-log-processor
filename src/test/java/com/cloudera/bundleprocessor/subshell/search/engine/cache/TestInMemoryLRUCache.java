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

import junit.framework.TestCase;
import org.junit.Test;

public class TestInMemoryLRUCache extends TestCase {

  @Test
  public void testGetCapacity() {
    InMemoryLRUCache<Integer, Integer> cache = new InMemoryLRUCache<>(2);
    assertEquals(2, cache.getCapacity());
    cache = new InMemoryLRUCache<>(8);
    assertEquals(8, cache.getCapacity());
  }

  @Test
  public void testSettingValues() {
    InMemoryLRUCache<Integer, Integer> cache = new InMemoryLRUCache<>(10);
    assertNull(cache.get(1));
    cache.set(1, 10);
    assertNotNull(cache.get(1));
    assertEquals(10, (int) cache.get(1));
    assertNull(cache.get(12));
    cache.set(12, 78);
    assertNotNull(cache.get(12));
    assertEquals(78, (int) cache.get(12));
  }

  @Test
  public void testUpdatingValues() {
    InMemoryLRUCache<Integer, Integer> cache = new InMemoryLRUCache<>(3);
    cache.set(1, 10);
    cache.set(2, 10);
    cache.set(1, 20);
    assertEquals(20, (int) cache.get(1));
    cache.set(2, 30);
    assertEquals(30, (int) cache.get(2));
  }

  @Test
  public void testForgettingValues() {
    InMemoryLRUCache<Integer, Integer> cache = new InMemoryLRUCache<>(1);
    cache.set(1, 10);
    cache.set(2, 20);
    assertNull(cache.get(1));
    cache.set(3, 30);
    assertNull(cache.get(2));
  }

  @Test
  public void testRememberingLastRecentlyUsed() {
    InMemoryLRUCache<Integer, Integer> cache = new InMemoryLRUCache<>(3);
    cache.set(1, 10);
    cache.set(5, 55);
    cache.set(3, 30);
    cache.set(7, 11);
    cache.set(1, 10);
    cache.set(4, 40);
    cache.set(5, 50);
    assertNotNull(cache.get(1));
    assertNotNull(cache.get(4));
    assertNotNull(cache.get(5));
    assertEquals(10, (int) cache.get(1));
    assertEquals(40, (int) cache.get(4));
    assertEquals(50, (int) cache.get(5));
    assertNull(cache.get(3));
    assertNull(cache.get(7));
  }

  @Test
  public void testReadingInterfere() {
    InMemoryLRUCache<Integer, Integer> cache = new InMemoryLRUCache<>(3);
    cache.set(1, 10);
    cache.set(2, 20);
    cache.set(3, 30);
    cache.get(1);
    cache.set(4, 40);
    assertNotNull(cache.get(1));
    assertEquals(10, (int) cache.get(1));
    assertNull(cache.get(2));
  }

  @Test
  public void testRemove() {
    InMemoryLRUCache<Integer, Integer> cache = new InMemoryLRUCache<>(5);
    cache.set(1, 10);
    cache.set(2, 20);
    cache.set(3, 30);
    assertNotNull(cache.get(2));
    cache.remove(2);
    assertNull(cache.get(2));
    assertNotNull(cache.get(1));
    cache.remove(1);
    assertNull(cache.get(1));
    assertNotNull(cache.get(3));
    cache.remove(3);
    assertNull(cache.get(3));
  }

  @Test
  public void testReset() {
    InMemoryLRUCache<Integer, Integer> cache = new InMemoryLRUCache<>(3);
    cache.set(1, 10);
    cache.set(2, 20);
    cache.set(3, 30);
    assertNotNull(cache.get(1));
    assertNotNull(cache.get(2));
    assertNotNull(cache.get(3));
    cache.reset();
    assertNull(cache.get(1));
    assertNull(cache.get(2));
    assertNull(cache.get(3));
    cache.set(1, 10);
    cache.set(5, 55);
    cache.set(3, 30);
    cache.set(7, 11);
    cache.set(1, 10);
    cache.set(4, 40);
    cache.set(5, 50);
    assertNotNull(cache.get(1));
    assertNotNull(cache.get(4));
    assertNotNull(cache.get(5));
    assertEquals(10, (int) cache.get(1));
    assertEquals(40, (int) cache.get(4));
    assertEquals(50, (int) cache.get(5));
    assertNull(cache.get(3));
    assertNull(cache.get(7));
  }

  @Test
  public void testNullInputs() {
    InMemoryLRUCache<Integer, Integer> cache = new InMemoryLRUCache<>(3);
    cache.set(1, 10);
    cache.set(null, null);
    assertEquals(10, (int) cache.get(1));
    cache.set(null, 20);
    assertEquals(20, (int) cache.get(null));
    cache.set(3, null);
    assertNull(cache.get(3));
  }

  @Test
  public void testInvalidCapacity() {
    try {
      new InMemoryLRUCache<>(0);
      fail("Cache construction with zero capacity should have failed.");
    } catch (Exception expected) {
    }
    try {
      new InMemoryLRUCache<>(-5);
      fail("Cache construction with negative capacity should have failed.");
    } catch (Exception expected) {
    }
  }
}