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

import java.util.function.Function;
import java.util.regex.Matcher;

public final class GrepperFactory {

  private GrepperFactory() {
  }

  public static final Column TIME_COLUMN =
      new Column("TIME", "time");
  public static final Column ATTEMPT_ID_COLUMN =
      new Column("ATTEMPT ID", "attemptid");
  public static final Column APP_ID_COLUMN =
      new Column("APPLICATION ID", "appid");
  public static final Column USER_COLUMN =
      new Column("USER", "user");
  public static final Column RAW_COLUMN =
      new Column("MATCHING LINES IN LOGS", "");
  public static final Column CONTAINER_COLUMN =
      new Column("CONTAINER ID", "containerid");
  public static final Column EXCEPTION_COLUMN =
      new Column("EXCEPTION", "exception");
  public static final Column EVENT_COLUMN =
      new Column("EVENT", "event");
  public static final Column TO_STATE_COLUMN =
      new Column("TO STATE", "tostate");
  public static final Column FROM_STATE_COLUMN =
      new Column("FROM STATE", "fromstate");
  public static final Column NODE_COLUMN =
      new Column("NODE", "node");
  public static final Column ROLE_COLUMN =
      new Column("ROLE", "role");
  public static final Column HOST_COLUMN =
      new Column("HOST", "host");
  public static final Column MEMORY_COLUMN =
      new Column("MEMORY", "memory");
  public static final Column V_CORES_COLUMN =
      new Column("VCORES", "vCores");
  public static final Column ALL_RESOURCES_COLUMN =
      new Column("RESOURCES", "all");
  public static final Column EXPRESSION_COLUMN =
      new Column("MATCHING EXPRESSIONS", "expression");
  public static final Column WHOLE_LINE_COLUMN =
      new Column("LINE IN LOGS", "");
  public static final Column SCHEDULER_COLUMN =
      new Column("SCHEDULER", "scheduler");

  /**
   * createGrepper() creates a Grepper with multiple columns.
   *
   * @param columns defines its headerNames and groupNames.
   */
  public static Grepper createGrepper(Column... columns) {
    String[] headerNames = new String[columns.length];
    String[] groupNames = new String[columns.length];
    for (int i = 0; i < columns.length; i++) {
      headerNames[i] = columns[i].getHeaderName();
      groupNames[i] = columns[i].getGroupName();
    }
    Function<Matcher, String[]> filterers = createGroupFilter(groupNames);
    GrepperFunction grepperFunction =
        new GrepperFunction(headerNames, filterers);
    return new Grepper(grepperFunction);
  }

  /**
   * createGrepper() creates a Grepper with a single column.
   *
   * @param column defines its headerName and groupName.
   */
  public static Grepper createGrepper(Column column) {
    String[] headerNames = new String[]{column.getHeaderName()};
    Function<Matcher, String[]> filterers = createGroupFilter(
        new String[]{column.getGroupName()});
    GrepperFunction grepperFunction =
        new GrepperFunction(headerNames, filterers);
    return new Grepper(grepperFunction);
  }

  /**
   * This helper function creates a Function for a Grepper query.
   *
   * @param groupNames identifier of groups in the regular  expression
   * @return Function
   */
  private static Function<Matcher, String[]> createGroupFilter(
      String[] groupNames) {
    if (groupNames.length == 1 && groupNames[0].equals("")) {
      return createWholeLineFilter();
    }
    return (Matcher matcher) -> {
      String[] rowElements = new String[groupNames.length];
      for (int i = 0; i < groupNames.length; i++) {
        rowElements[i] = matcher.group(groupNames[i]);
      }
      return rowElements;
    };
  }

  /**
   * This helper function creates a function
   * for a Grepper query matching the whole line.
   *
   * @return Function
   */
  private static Function<Matcher, String[]> createWholeLineFilter() {
    return (Matcher matcher) -> new String[]{
        matcher.group()
    };
  }

  public static class Column {
    private final String headerName;
    private final String groupName;

    public Column(String headerName, String groupName) {
      this.headerName = headerName;
      this.groupName = groupName;
    }

    public String getHeaderName() {
      return headerName;
    }

    public String getGroupName() {
      return groupName;
    }
  }
}
