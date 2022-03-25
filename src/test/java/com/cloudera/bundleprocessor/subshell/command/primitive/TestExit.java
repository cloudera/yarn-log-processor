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


package com.cloudera.bundleprocessor.subshell.command.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.cloudera.bundleprocessor.subshell.command.util.AutoCompleterWrapper;
import org.junit.Test;

public class TestExit {

  private final ExitCommand exit = new ExitCommand();

  @Test
  public void testSimpleFields() {
    assertEquals("exit", exit.getName());
    assertNotNull("expected a non null message for the description",
        exit.getDescription());
    assertFalse(exit.readMore());

    AutoCompleterWrapper autoCompleter = exit.createAutoCompleterWrapper();
    assertNotNull(autoCompleter);
    assertEquals("exit", autoCompleter.getCommandName());
  }
}
