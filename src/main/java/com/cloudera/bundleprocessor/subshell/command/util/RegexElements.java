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

package com.cloudera.bundleprocessor.subshell.command.util;

import com.cloudera.bundleprocessor.Constants;

import java.util.Arrays;
import java.util.List;

public final class RegexElements {

  private RegexElements() {
  }

  public static final String LOGFILE =
      ".*(?<role>(" + Constants.RESOURCEMANAGER + "|"
          + Constants.NODEMANAGER + "))-(?<host>.+)\\.log\\.out";
  public static final String CONFIGFILE = ".*-site.xml";
  public static final String REPLACEIT = "REPLACEIT";
  public static final String ROLE_REGEX = LOGFILE;
  public static final String RESOURCE_REGEX_PREFIX =
      "ResourceTrackerService: NodeManager from node (?<node>[^\\s(]*).*";
  public static final String RESOURCE_REGEX = RESOURCE_REGEX_PREFIX
      + "memory:(?<memory>\\d*), vCores:(?<vCores>\\d*).*";
  public static final String CUSTOM_RESOURCE_TYPES_REGEX = RESOURCE_REGEX_PREFIX
      + " registered with capability: <(?<all>.*)>";
  public static final String TIME_STAMP =
      "(?<time>\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})";
  public static final String ADDITIONAL_TIME_STAMP =
      "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})";
  public static final String USER = "USER=(?<user>[a-zA-Z]+)";
  public static final String SUBMIT_APPLICATION_REQUEST =
      "OPERATION=Submit Application Request";
  public static final String SUCCESS = "RESULT=SUCCESS";
  public static final String APPID = "APPID=(?<appid>application_\\d*_\\d{4})";
  public static final String REGISTERED_ATTEMPTID =
      "Registering app attempt : ";
  public static final String ATTEMPTID =
      "(?<attemptid>appattempt_(?<appnumber>\\d+_\\d+)_(?<attemptnumber>\\d+))";
  public static final String ATTEMPTID_REGISTERED =
      REGISTERED_ATTEMPTID + ATTEMPTID;
  public static final String SUBMIT_CONTAINER_REQUEST =
      "OPERATION=Start Container Request";
  public static final String APPLICATION_NUMBER_INSIDE_CONTAINERID =
      "(?<applicationnumber>\\d*_\\d*)";
  public static final String APPATTEMPT_NUMBER_INSIDE_CONTAINERID =
      "(?<attemptnumber>" + APPLICATION_NUMBER_INSIDE_CONTAINERID + "_\\d*)";
  public static final String CONTAINERID = "(?<containerid>container_(e\\d+_)?"
      + APPATTEMPT_NUMBER_INSIDE_CONTAINERID + "_\\d*)";
  public static final String STATE_TRANSITION =
      "from (?<fromstate>[A-Z]*) to (?<tostate>[A-Z_]*)";
  public static final String EVENT = "event = (?<event>[A-Z_]*)";
  public static final String STATE =
      "in state: (?<fromstate>)(?<tostate>[A-Z_]*)";
  public static final String APP_PREFIX = "application_";
  public static final String APPATTEMPT_PREFIX = "appattempt_";
  public static final String CONTAINER_PREFIX = "container_";
  public static final String ANY_LINE = TIME_STAMP + ".*\\n";
  public static final String EXCEPTION_WITH_STACKTRACE =
      "(?<exception>.+Exception)[^\\n]+(\\s+at .+)+";
  public static final String TRANSITIONED_TO_KILLING =
      " transitioned from [A-Z_]+ to KILLING";
  public static final String EXIT_CODE = "(?<exitcode>\\d+)";
  public static final String START_OF_CONTAINER_ERROR_LOG =
      TIME_STAMP + ".*Shell execution returned exit code: "
          + EXIT_CODE + ". Privileged Execution Operation Stderr: \\n";
  public static final String LINE_WITHOUT_TIMESTAMP = "[^0-9].*\\n";
  public static final String LINES_WITHOUT_TIMESTAMP =
      "(" + LINE_WITHOUT_TIMESTAMP + ")*";
  public static final String COMMAND_ARRAY = "\\[.*" + CONTAINERID + ".*\\]\\n";
  public static final String END_OF_CONTAINER_ERROR_LOG =
      ADDITIONAL_TIME_STAMP + ".*";
  public static final String INFO_LOG_LEVEL = ".*INFO.*";
  public static final String ROLE = "(?<role>(nodemanager|resourcemanager))";
  public static final String SCHEDULER_REGEX = TIME_STAMP + INFO_LOG_LEVEL
      + "org.apache.hadoop.yarn.server.resourcemanager."
      + "ResourceManager: Using Scheduler:.*\\."
      + "(?<scheduler>[A-Za-z]*Scheduler)";
  public static final List<String> RM_EVENTS =
      Arrays.asList("MSG", "Transition", "Recover", "recover");
  public static final String RM_EVENT_REGEX = TIME_STAMP
      + ".*org.apache.hadoop.yarn.server.resourcemanager."
      + "(ResourceManager: |RMAppManager: )" + "(?<event>.*(" +
      String.join("|", RM_EVENTS) + ").*)";

  public static final List<String> PREEMPTED_CONTAINER_MESSAGES = Arrays.asList(
      "Non-AM container preempted, current.*",
      "AM container preempted, current.*",
      "Preempting container.*",
      "was preempted.*");
  public static final String PREEMPTED_CONTAINER = TIME_STAMP + ".*(?<message>"
      + String.join("|", PREEMPTED_CONTAINER_MESSAGES) + ")"
      + CONTAINERID + ".*";
  public static final String RMNODEIMPL_CLASS =
      "org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeImpl";
  public static final String NODE_TRANSITION = " Node Transitioned ";

  /**
   * {@code wrapInCapturingGroup} creates a capturing group
   * from the name of the group and its content.
   * The output will match on the same texts as
   * the original regular expression do,
   * but the matched text will be searchable with the capturing group.
   *
   * @param capturingGroupName the nam eof the capturing group
   * @param content            any regular expression
   * @return String with content in capturing group
   */
  public static String wrapInCapturingGroup(
      String capturingGroupName, String content) {
    return "(?<" + capturingGroupName + ">" + content + ")";
  }

  public static String generateAppName(String appIdentifier) {
    return String.format("%s%s", APP_PREFIX, appIdentifier);
  }

  public static String generateContainerName(
      String appIdentifier, int attemptNumber, int containerNumber) {
    return String.format("%s%s_0%s_00000%s", CONTAINER_PREFIX, appIdentifier,
        attemptNumber, containerNumber);
  }

  public static String generateAppAttemptName(
      String appIdentifier, int attemptNumber) {
    return String.format("%s%s_00000%s", APPATTEMPT_PREFIX,
        appIdentifier, attemptNumber);
  }
}
