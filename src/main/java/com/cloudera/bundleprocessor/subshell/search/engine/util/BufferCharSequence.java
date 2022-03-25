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

package com.cloudera.bundleprocessor.subshell.search.engine.util;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * BufferCharSequence is providing a buffered CharSequence from the file.
 */
public class BufferCharSequence implements CharSequence {

  private final ByteBuffer buffer;
  private final CharsetDecoder decoder = Charset.forName("8859_1").newDecoder();

  /**
   * The constructor saves the ByteBuffer.
   *
   * @param buffer ByteBuffer input
   */
  public BufferCharSequence(ByteBuffer buffer) {
    this.buffer = buffer;
  }

  /**
   * Returns the length of this character sequence.
   *
   * @see java.lang.CharSequence#length()
   */
  @Override
  public int length() {
    return buffer.limit();
  }

  /**
   * Returns the char value at the specified index.
   *
   * @see java.lang.CharSequence#charAt(int)
   */
  @Override
  public char charAt(int index) {
    return (char) buffer.get(index);
  }

  /**
   * Returns a CharSequence that is a subsequence of this sequence.
   *
   * @see java.lang.CharSequence#subSequence(int, int)
   */
  @Override
  public CharSequence subSequence(int start, int end) {
    int limit = buffer.limit();
    int position = buffer.position();
    buffer.limit(end);
    buffer.position(start);
    CharBuffer result = null;
    try {
      result = decoder.decode(buffer);
    } catch (CharacterCodingException e) {
      e.printStackTrace();
    }
    buffer.limit(limit);
    buffer.position(position);
    return result;
  }

  @Override
  @NotNull
  public String toString() {
    try {
      return decoder.decode(buffer).toString();
    } catch (CharacterCodingException cce) {
      throw new RuntimeException("Could not decode ByteBuffer", cce);
    }
  }
}