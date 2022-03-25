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

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GeneralCache<K, V extends Serializable> implements Cache<K, V> {

  private final CacheIOExecutor cacheIOExecutor;
  private final Map<Integer, Boolean> checkedKeys;

  /**
   * Constructor for GeneralCache.
   *
   * @param cacheIOExecutor executor to handle cachefiles
   */
  public GeneralCache(CacheIOExecutor cacheIOExecutor) {
    this.cacheIOExecutor = cacheIOExecutor;
    this.checkedKeys = new HashMap<>();
  }

  @Override
  public V get(K key) {
    Boolean hasKey = checkedKeys.get(key.hashCode());
    if (hasKey == null || hasKey) {
      V value;
      try {
        value = (V) cacheIOExecutor.readItem(key);
      } catch (Exception e) {
        return null;
      }
      checkedKeys.put(key.hashCode(), value != null);
      return value;
    } else {
      // hasKey is false, which means there is no data for the specified key
      return null;
    }
  }

  @Override
  public void set(K key, V value) {
    try {
      cacheIOExecutor.writeItem(key, value);
    } catch (IOException e) {
      e.printStackTrace();
      checkedKeys.put(key.hashCode(), false);
      return;
    }
    checkedKeys.put(key.hashCode(), true);
  }

  @Override
  public void remove(K key) {
    try {
      cacheIOExecutor.remove(key);
      checkedKeys.put(key.hashCode(), false);
    } catch (IOException ioe) {
      throw new RuntimeException("Couldn't remove from cache", ioe);
    }
  }

  @Override
  public void reset() {
    try {
      cacheIOExecutor.removeAll();
      checkedKeys.clear();
    } catch (IOException ioe) {
      throw new RuntimeException("Couldn't reset cache", ioe);
    }
  }
}
