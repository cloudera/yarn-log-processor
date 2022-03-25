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

package com.cloudera.bundleprocessor.subshell.command.simple;

import com.cloudera.bundleprocessor.subshell.command.Command;
import com.cloudera.bundleprocessor.subshell.command.SearchCommand;
import com.cloudera.bundleprocessor.subshell.command.util.AutoCompleterWrapper;
import com.cloudera.bundleprocessor.subshell.context.Context;
import com.cloudera.bundleprocessor.subshell.format.Printable;
import com.cloudera.bundleprocessor.subshell.search.request.Executable;

/**
 * {@code SimpleSearch} abstract class is a {@link Command} class
 * for the CLI command without parameter.
 * Since no parameter is parsed from the CLI, {@code SimpleSearch} objects
 * are always providing the same output
 */
public abstract class SimpleSearch extends SearchCommand {

  protected SimpleSearch(Context context) {
    super(context);
  }

  @Override
  public Printable generatePrintable(String[] parameters) {
    return execute(createExecutable());
  }

  @Override
  public AutoCompleterWrapper createAutoCompleterWrapper() {
    return new AutoCompleterWrapper(this.getName());
  }

  /**
   * {@code createExecutable()} always creates the same Executable object.
   *
   * @return Executable contains the details how we will find
   * the information in the log files
   */
  protected abstract Executable createExecutable();
}
