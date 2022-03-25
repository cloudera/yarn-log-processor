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

package com.cloudera.bundleprocessor.util;

import com.cloudera.bundleprocessor.subshell.command.util.RegexElements;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.LinesOfLogs;

public class ExpandableLinesOfLogs extends LinesOfLogs {

  private static final String TIMESTAMP = "2020-03-02 08:36:18";

  public ExpandableLinesOfLogs() {
    super(null);
  }

  public static String getTIMESTAMP() {
    return TIMESTAMP;
  }

  /**
   * Adds a line to the logs marking, that YARN is using a specific scheduler.
   *
   * @param scheduler name of the specific scheduler
   * @return itself
   */
  public ExpandableLinesOfLogs addLineWithScheduler(String scheduler) {
    this.lines.add(
        "2020-03-02 08:36:18,375 INFO org.apache.hadoop." +
            "yarn.server.resourcemanager.ResourceManager: " +
            "Using Scheduler: some long path." + scheduler);
    return this;
  }

  /**
   * Adds a line to the logs reporting of a node with specified resources.
   *
   * @param nodeName name of the node with the resources
   * @param cores    vCores of the node
   * @param memory   memory capacity of the node
   * @return itself
   */
  public ExpandableLinesOfLogs addLineWithResources(
      String nodeName, int memory, int cores) {
    this.lines.add(String.format(
        "2020-03-02 08:41:51,635 INFO org.apache.hadoop." +
            "yarn.server.resourcemanager.ResourceTrackerService: " +
            "NodeManager from node %s registered with capability: " +
            "<memory:%s, vCores:%s>, assigned nodeId nodeId",
        nodeName, memory, cores));
    return this;
  }

  /**
   * Adds a line registering an application attempt.
   *
   * @param appAttemptName name of the application attempt
   * @return itself
   */
  public ExpandableLinesOfLogs addLineWithAppAttempt(String appAttemptName) {
    this.lines.add(TIMESTAMP +
        RegexElements.REGISTERED_ATTEMPTID + appAttemptName);
    return this;
  }

  /**
   * Adds the line where the application with the specified name is registered.
   *
   * @param applicationName name of the application
   * @return
   */
  public ExpandableLinesOfLogs addLineWithAppliation(String applicationName) {
    this.lines.add(String.format(TIMESTAMP +
            " INFO org.apache.hadoop.yarn.server."
            + "resourcemanager.RMAuditLogger: "
            + "USER=systest\\tIP=172.27.67.72\tOPERATION=Submit "
            + "Application Request\tTARGET=ClientRMService"
            + "\\tRESULT=SUCCESS\\tAPPID=%s\tCALLERCONTEXT=CLI",
        applicationName));
    return this;
  }

  /**
   * Adds a line where the container with the specified name is registered.
   *
   * @param appIdentifier   identifier of the application
   *                        the container belongs to
   * @param attemptNumber   application attempt of the application
   * @param containerNumber number of the container
   * @return itself
   */
  public ExpandableLinesOfLogs addLineWithContainer(
      String appIdentifier, int attemptNumber, int containerNumber) {
    String applicationName = RegexElements.generateAppName(appIdentifier);
    String containerName = RegexElements.generateContainerName(
        appIdentifier, attemptNumber, containerNumber);
    this.lines.add(String.format("%s INFO org.apache.hadoop.yarn.server."
            + "nodemanager.NMAuditLogger: USER=systest\t"
            + "IP=172.27.99.4\tOPERATION=Start Container Request\t"
            + "TARGET=ContainerManageImpl\tRESULT=SUCCESS\t"
            + "APPID=%s\tCONTAINERID=%s", TIMESTAMP,
        applicationName, containerName));
    return this;
  }

  /**
   * Adds line reporting an event belonging to an application.
   *
   * @param applicationName name of the application
   * @param toState         application state before the event
   * @param fromState       application state after the event
   * @param event           name of the event
   * @return itself
   */
  public ExpandableLinesOfLogs addLineWithAppEvent(
      String applicationName, String fromState, String toState, String event) {
    this.lines.add(String.format(
        "%s INFO org.apache.hadoop.yarn.server.resourcemanager.rmapp." +
            "RMAppImpl: %s State change from %s to %s on event = %s",
        TIMESTAMP, applicationName, fromState, toState, event));
    return this;
  }

  /**
   * Adds line reporting an event belonging to an application attempt.
   *
   * @param attemptName name of the application attempt
   * @param toState     application state before the event
   * @param fromState   application state after the event
   * @param event       name of the event
   * @return itself
   */
  public ExpandableLinesOfLogs addLineWithAttemptEvent(
      String attemptName, String event, String fromState, String toState) {
    this.lines.add(String.format(
        "%s INFO org.apache.hadoop.yarn.server.resourcemanager.rmapp." +
            "RMAppAttemptImpl: %s State change from %s to %s on event = %s",
        TIMESTAMP, attemptName, fromState, toState, event));
    return this;
  }

  /**
   * Adds a line reporting a state change of a node.
   *
   * @param node      name of hte node
   * @param toState   application state before the event
   * @param fromState application state after the event
   * @return itself
   */
  public ExpandableLinesOfLogs addLineWithNodeStateChange(
      String node, String fromState, String toState) {
    this.lines.add(String.format(
        "%s INFO org.apache.hadoop.yarn.server.resourcemanager.rmnode." +
            "RMNodeImpl: %s:8041 Node Transitioned from %s to %s",
        TIMESTAMP, node, fromState, toState));

    return this;
  }

  /**
   * Adds a line containing a String.
   *
   * @param string string to be contained by the line
   * @return itself
   */
  public ExpandableLinesOfLogs addLineWithAString(String string) {
    this.lines.add(String.format("%srandomtext%srandomtext",
        TIMESTAMP, string));
    return this;
  }

  /**
   * Adds a String without any modification.
   *
   * @param string string
   * @return itself
   */
  public ExpandableLinesOfLogs addAString(String string) {
    this.lines.add(string);
    return this;
  }
}
