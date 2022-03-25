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

package com.cloudera.bundleprocessor.subshell.search.request;

import com.cloudera.bundleprocessor.subshell.format.ComposedPrintable;
import com.cloudera.bundleprocessor.subshell.search.engine.SearchEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ComposedExecutable is an aggregation of multiple {@link Executable}s.
 */
public class ComposedExecutable implements Executable {

  private final List<Executable> executables = new ArrayList<>();

  public void addExecutable(Executable executable) {
    executables.add(executable);
  }

  @Override
  public void execute(SearchEngine searchEngine) throws IOException {
    for (Executable singleExecutable : executables) {
      singleExecutable.execute(searchEngine);
    }
  }

  @Override
  public ComposedPrintable getPrintable() {
    ComposedPrintable composedPrintable = new ComposedPrintable();
    for (Executable singleExecutable : executables) {
      composedPrintable.addPrintable(singleExecutable.getPrintable());
    }
    return composedPrintable;
  }
}
