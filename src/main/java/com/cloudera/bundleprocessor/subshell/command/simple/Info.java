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

import com.cloudera.bundleprocessor.Constants;
import com.cloudera.bundleprocessor.subshell.command.Command;
import com.cloudera.bundleprocessor.subshell.command.util.RegexElements;
import com.cloudera.bundleprocessor.subshell.context.Context;
import com.cloudera.bundleprocessor.subshell.search.format.Counter;
import com.cloudera.bundleprocessor.subshell.search.format.CounterFunction;
import com.cloudera.bundleprocessor.subshell.search.format.Grepper;
import com.cloudera.bundleprocessor.subshell.search.format.GrepperFactory;
import com.cloudera.bundleprocessor.subshell.search.request.ComposedExecutable;
import com.cloudera.bundleprocessor.subshell.search.request.SingleExecutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Info class is a {@link Command} class for the CLI command "applications".
 * The execution of "applications" command lists
 * every launched application in the cluster.
 */
public class Info extends SimpleSearch {

  private static final Logger LOG =
      LoggerFactory.getLogger(Info.class);
  private static final Pattern SCHEDULER_PATTERN =
      Pattern.compile(RegexElements.SCHEDULER_REGEX);
  private static final Pattern ROLE_PATTERN =
      Pattern.compile(RegexElements.ROLE_REGEX);

  private final String resourceRegex =
      this.getContext().getConfig().getRegexes().getTimeStamp() + ".*"
          + RegexElements.RESOURCE_REGEX;
  private final Pattern resourcePattern = Pattern.compile(resourceRegex);

  public Info(Context context) {
    super(context);
  }

  private static Integer[] countRoles(Matcher matcher) {
    Integer[] counterValues = new Integer[]{0, 0};
    LOG.info(String.valueOf(matcher));
    String role = matcher.group("role");
    if (role.equals(Constants.RESOURCEMANAGER)) {
      counterValues[0]++;
    } else if (role.equals(Constants.NODEMANAGER)) {
      counterValues[1]++;
    }
    return counterValues;
  }

  private static String[] generateRolesTableRow(Integer[] counterValues) {
    return new String[]{
        "  - RESOURCEMANAGER: " + counterValues[0],
        "  - NODEMANAGER: " + counterValues[1]
    };
  }

  private static Integer[] countResources(Matcher matcher) {
    Integer[] counterValues = new Integer[]{0, 0};
    counterValues[0] += Integer.parseInt(matcher.group("vCores"));
    counterValues[1] += Integer.parseInt(matcher.group("memory"));
    return counterValues;
  }

  private static String[] generateResourcesTableRow(Integer[] counterValues) {
    return new String[]{
        "  - CPU: " + counterValues[0],
        "  - MEMORY: " + counterValues[1]
    };
  }

  @Override
  protected ComposedExecutable createExecutable() {
    ComposedExecutable composedExecutable = new ComposedExecutable();
    composedExecutable.addExecutable(new SingleExecutable.Builder()
        .withPattern(ROLE_PATTERN)
        .withFormatter(new Counter(new CounterFunction(new String[]{"NODES"},
            Info::countRoles, Info::generateRolesTableRow)))
        .isCheckingFileNames()
        .build());
    composedExecutable.addExecutable(new SingleExecutable.Builder()
        .withPattern(resourcePattern)
        .withFormatter(new Counter(new CounterFunction(new String[]{"RESOURCE"},
            Info::countResources, Info::generateResourcesTableRow)))
        .removeDuplicationOfParameter("node")
        .isCheckingRmLogs()
        .build());
    Grepper grepper =
        GrepperFactory.createGrepper(GrepperFactory.SCHEDULER_COLUMN);
    composedExecutable.addExecutable(new SingleExecutable.Builder()
        .withPattern(SCHEDULER_PATTERN)
        .withFormatter(grepper)
        .removeDuplicationOfParameter("scheduler")
        .isCheckingRmLogs()
        .build());
    return composedExecutable;
  }

  @Override
  public String getName() {
    return "info";
  }

  @Override
  public String getDescription() {
    return "prints general pieces of information about the cluster";
  }
}