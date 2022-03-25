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
import com.cloudera.bundleprocessor.subshell.command.util.RegexElements;
import com.cloudera.bundleprocessor.subshell.context.Context;
import com.cloudera.bundleprocessor.subshell.search.format.Grepper;
import com.cloudera.bundleprocessor.subshell.search.format.GrepperFactory;
import com.cloudera.bundleprocessor.subshell.search.request.Executable;
import com.cloudera.bundleprocessor.subshell.search.request.SingleExecutable;

import java.util.regex.Pattern;

/**
 * Roles class is a {@link Command} class for the CLI command "roles".
 * "roles" command looks through the log folder
 * and lists every YARN role in the cluster.
 */
public class Roles extends SimpleSearch {

  private static final Pattern ROLE_PATTERN =
      Pattern.compile(RegexElements.ROLE_REGEX);
  private static final Grepper ROLE_GREPPER = GrepperFactory.createGrepper(
      GrepperFactory.ROLE_COLUMN, GrepperFactory.HOST_COLUMN);

  public Roles(Context context) {
    super(context);
  }

  @Override
  protected Executable createExecutable() {
    return new SingleExecutable.Builder()
        .withPattern(ROLE_PATTERN)
        .withFormatter(ROLE_GREPPER)
        .isCheckingFileNames()
        .build();
  }

  @Override
  public String getName() {
    return "roles";
  }

  @Override
  public String getDescription() {
    return "lists all YARN roles in the cluster " +
        "and the hosts connected to them";
  }
}
