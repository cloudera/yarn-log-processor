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

import java.util.HashMap;

public class InMemoryLRUCache<K, V> implements Cache<K, V> {

  private final HashMap<K, Node<K, V>> map;
  private final int capacity;
  private final Node<K, V> head;
  private final Node<K, V> tail;

  /**
   * Constructor for InMemoryLRUCache.
   *
   * @param capacity capacity of the cache
   */
  public InMemoryLRUCache(int capacity) {
    if (capacity < 1) {
      throw new IllegalArgumentException(
          "Cache cannot be created with zero or negative capacity");
    }
    this.capacity = capacity;
    map = new HashMap<>();
    head = new Node<>(null, null);
    tail = new Node<>(null, null);
    initLinkedList();
  }

  // This method works in O(1)
  @Override
  public V get(K key) {
    if (map.get(key) != null) {
      Node<K, V> node = map.get(key);
      V result = node.value;
      deleteNode(node);
      addToHead(node);
      return result;
    }
    return null;
  }

  // This method works in O(1)
  @Override
  public void set(K key, V value) {
    if (map.get(key) != null) {
      Node<K, V> node = map.get(key);
      node.value = value;
      deleteNode(node);
      addToHead(node);
    } else {
      Node<K, V> node = new Node<>(key, value);
      map.put(key, node);
      addToHead(node);
      if (map.size() > capacity) {
        map.remove(tail.pre.key);
        deleteNode(tail.pre);
      }
    }
  }

  @Override
  public void remove(K key) {
    Node<K, V> node = map.get(key);
    if (node != null) {
      map.remove(key);
      deleteNode(node);
    }
  }

  @Override
  public void reset() {
    map.clear();
    initLinkedList();
  }

  public int getCapacity() {
    return capacity;
  }

  private void deleteNode(Node<K, V> node) {
    node.pre.next = node.next;
    node.next.pre = node.pre;
  }

  private void addToHead(Node<K, V> node) {
    node.next = head.next;
    node.next.pre = node;
    node.pre = head;
    head.next = node;
  }

  private void initLinkedList() {
    head.next = tail;
    tail.pre = head;
    head.pre = null;
    tail.next = null;
  }

  public static class Node<K, V> {
    private final K key;
    private V value;
    private Node<K, V> pre;
    private Node<K, V> next;

    public Node(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }
}
