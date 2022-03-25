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

package com.cloudera.bundleprocessor.console;

/**
 * A helper class to wrap UNIX shell color escape sequences over
 * strings. Taken from:
 * https://stackoverflow.com/questions/
 * 4842424/list-of-ansi-color-escape-sequences
 */
public class ColoredShellStringBuilder {
  private final StringBuilder internal;

  public ColoredShellStringBuilder() {
    this.internal = new StringBuilder();
  }

  /**
   * Appends a colorized string and appends a reset sequence at the end.
   *
   * @param s string
   * @param c color of the string
   * @return this builder
   */
  public ColoredShellStringBuilder append(String s, ConsoleColor c) {
    this.internal.append(String.format("%s %s", c.getEscapeSequence(), s));
    return resetColor();
  }

  /**
   * Appends a string of color red and appends a reset sequence at the end.
   *
   * @param s string
   * @return this builder
   */
  public ColoredShellStringBuilder red(String s) {
    return this.append(s, ConsoleColor.RED);
  }

  /**
   * Appends a string of color green and appends a reset sequence at the end.
   *
   * @param s string
   * @return this builder
   */
  public ColoredShellStringBuilder green(String s) {
    return this.append(s, ConsoleColor.GREEN);
  }

  /**
   * Appends a string of color blue and appends a reset sequence at the end.
   *
   * @param s string
   * @return this builder
   */
  public ColoredShellStringBuilder blue(String s) {
    return this.append(s, ConsoleColor.BLUE);
  }

  /**
   * Appends a reset sequence. It is necessary to avoid coloring all subsequent
   * string after coloring.
   *
   * @return this builder
   */
  public ColoredShellStringBuilder resetColor() {
    this.internal.append(ConsoleColor.RESET.getEscapeSequence());
    return this;
  }

  /**
   * Creates the colorized string.
   *
   * @return colorized string
   */
  public String build() {
    return this.internal.toString();
  }

  public enum ConsoleColor {
    RED("\033[31m"),
    GREEN("\033[32m"),
    BLUE("\033[34m"),
    RESET("\u001B[0m");

    private final String escapeSequence;

    ConsoleColor(String escapeSequence) {
      this.escapeSequence = escapeSequence;
    }

    public String getEscapeSequence() {
      return escapeSequence;
    }
  }
}
