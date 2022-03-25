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

package com.cloudera.bundleprocessor.subshell.search.engine;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Query represents a search request.
 * Query is an immutable object.
 */
public final class Query {

  private final Pattern pattern;
  private final boolean checkingRmLogs;
  private final boolean checkingNmLogs;
  private final boolean checkingFileNames;

  private Query(Builder builder) {
    this.pattern = builder.pattern;
    this.checkingRmLogs = builder.checkingRmLogs;
    this.checkingNmLogs = builder.checkingNmLogs;
    this.checkingFileNames = builder.checkingFileNames;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Query query = (Query) o;
    return checkingRmLogs == query.checkingRmLogs
        && checkingNmLogs == query.checkingNmLogs
        && checkingFileNames == query.checkingFileNames
        && pattern.pattern().equals(query.pattern.pattern());
  }

  @Override
  public int hashCode() {
    return Objects.hash(pattern.pattern(), checkingRmLogs,
        checkingNmLogs, checkingFileNames);
  }

  public Pattern getPattern() {
    return pattern;
  }

  public boolean searchInRmLogs() {
    return checkingRmLogs;
  }

  public boolean searchInNmLogs() {
    return checkingNmLogs;
  }

  public boolean searchInFileNames() {
    return checkingFileNames;
  }

  public static class Builder {

    private Pattern pattern;
    private boolean checkingRmLogs;
    private boolean checkingNmLogs;
    private boolean checkingFileNames;

    public Builder() {
    }

    public Builder withPattern(Pattern pattern) {
      this.pattern = pattern;
      return this;
    }

    public Builder isCheckingRmLogs() {
      this.checkingRmLogs = true;
      return this;
    }

    public Builder isCheckingNmLogs() {
      this.checkingNmLogs = true;
      return this;
    }

    public Builder isCheckingFileNames() {
      this.checkingFileNames = true;
      return this;
    }

    public Query build() {
      return new Query(this);
    }
  }
}
