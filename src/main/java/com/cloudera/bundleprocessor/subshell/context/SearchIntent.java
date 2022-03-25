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

package com.cloudera.bundleprocessor.subshell.context;

import java.util.Objects;

public final class SearchIntent {

  private final String command;
  private final boolean launchingShell;

  private SearchIntent(Builder builder) {
    if (builder.command != null && builder.launchingShell) {
      throw new IllegalArgumentException(
          "Command and shell cannot be executed at the same time");
    }
    this.command = builder.command;
    this.launchingShell = builder.launchingShell;
  }

  public String getCommand() {
    return command;
  }

  public boolean isLaunchingShell() {
    return launchingShell;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchIntent that = (SearchIntent) o;
    return launchingShell == that.launchingShell
        && Objects.equals(command, that.command);
  }

  @Override
  public int hashCode() {
    return Objects.hash(command, launchingShell);
  }

  public static class Builder {

    private String command;
    private boolean launchingShell;

    public Builder() {
    }

    public Builder withCommand(String command) {
      this.command = command;
      return this;
    }

    public Builder withLaunchingShell(boolean launchingShell) {
      this.launchingShell = launchingShell;
      return this;
    }

    public SearchIntent build() {
      return new SearchIntent(this);
    }
  }
}
