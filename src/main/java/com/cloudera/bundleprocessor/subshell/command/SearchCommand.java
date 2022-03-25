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

package com.cloudera.bundleprocessor.subshell.command;

import com.cloudera.bundleprocessor.OptionParser;
import com.cloudera.bundleprocessor.console.ConsoleWriter;
import com.cloudera.bundleprocessor.subshell.context.Context;
import com.cloudera.bundleprocessor.subshell.format.EmptyPrintable;
import com.cloudera.bundleprocessor.subshell.format.Printable;
import com.cloudera.bundleprocessor.subshell.search.format.Grepper;
import com.cloudera.bundleprocessor.subshell.search.request.Executable;

import java.io.IOException;
import java.util.Map;

/**
 * {@code SearchCommand} abstract class is a {@link Command} class
 * for the CLI command requiring a search.
 * They need to define an {@link Executable}, execute it with the
 * {@link Context} and generate a {@link Printable}
 */
public abstract class SearchCommand implements Command {

  private final Context context;

  protected SearchCommand(Context context) {
    this.context = context;
  }

  protected Printable execute(Executable executable) {
    try {
      executable.execute(this.context.getSearchEngine());
      return executable.getPrintable();
    } catch (IOException e) {
      ConsoleWriter.CONSOLE.error("An exception occurred: ", e);
      return new EmptyPrintable();
    }
  }

  protected Grepper evaluateFormatOptions(
      Map<String, Grepper> formatOptionMap, OptionParser optionParser) {
    Grepper grepper = formatOptionMap.get("default");
    for (String key : formatOptionMap.keySet()) {
      if (optionParser.checkParameter(key)) {
        grepper = formatOptionMap.get(key);
      }
    }
    return grepper;
  }

  public Context getContext() {
    return context;
  }

  @Override
  public boolean readMore() {
    return true;
  }
}
