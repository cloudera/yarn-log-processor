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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LinesOfLogs implements Serializable {

  private static final long serialVersionUID = 754749516;

  protected final List<String> lines;

  public LinesOfLogs(List<String> lines) {
    this.lines = lines == null ? new ArrayList<>() : lines;
  }

  public List<String> getLines() {
    return lines;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LinesOfLogs that = (LinesOfLogs) o;
    return Objects.equals(lines, that.lines);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lines);
  }

  /**
   * Prints out the log lines separated with newlines.
   *
   * @return the log lines separated with newlines
   */
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (String line : lines) {
      stringBuilder.append(line).append("\n");
    }
    return stringBuilder.toString();
  }
}
