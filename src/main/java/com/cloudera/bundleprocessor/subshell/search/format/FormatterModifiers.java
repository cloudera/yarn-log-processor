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

package com.cloudera.bundleprocessor.subshell.search.format;

/**
 * FormatterModifiers is containing extra information
 * about formatting an output.
 * We initialize a {@link Formatter}
 * by {@code init(FormatterModifiers fm)} function.
 */
public class FormatterModifiers {

  private final String keyParameter;
  private final boolean separatingBySourceFile;

  FormatterModifiers(Builder builder) {
    this.keyParameter = builder.keyParameter;
    this.separatingBySourceFile = builder.separatingBySourceFile;
  }

  public String getKeyParameter() {
    return keyParameter;
  }

  public boolean isSeparatingBySourceFile() {
    return separatingBySourceFile;
  }

  public static class Builder {

    private String keyParameter;
    private boolean separatingBySourceFile;

    public Builder() {
    }

    public Builder setKeyParameter(String keyParameter) {
      this.keyParameter = keyParameter;
      return this;
    }

    public Builder setSeparatingBySourceFile(boolean separatingBySourceFile) {
      this.separatingBySourceFile = separatingBySourceFile;
      return this;
    }

    public FormatterModifiers build() {
      return new FormatterModifiers(this);
    }
  }
}
