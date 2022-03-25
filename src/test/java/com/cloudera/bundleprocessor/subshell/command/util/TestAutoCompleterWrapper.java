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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.cloudera.bundleprocessor.OptionParser;
import java.util.List;
import org.junit.Test;

/**
 * Test for {@class AutoCompleterWrapper}.
 *
 * <p>Since jline's node object does not have public functions,
 * we can only check whether we receive non-null items.
 */
public class TestAutoCompleterWrapper {
  @Test
  public void testAutoCompleterWrapperFromCommandName() {
    AutoCompleterWrapper autoCompleter =
        new AutoCompleterWrapper("command_name");
    assertNotNull(autoCompleter.generateTreeCompleterNode());
    assertEquals("command_name", autoCompleter.getCommandName());
  }

  @Test
  public void testAutoCompleterWrapper() {
    OptionParser parser = new OptionParser.Builder()
        .setCommandName("test")
        .addOption("t", "test", false, "test option", false)
        .build();
    AutoCompleterWrapper autoCompleter = new AutoCompleterWrapper(parser);
    assertNotNull(autoCompleter.generateTreeCompleterNode());
    assertEquals("test", autoCompleter.getCommandName());
    List<String> options = autoCompleter.getOptions();
    // expects the -t and the --test option
    assertEquals(2, options.size());
    assertTrue("-t option is missing from AutoCompleterWrapper",
        options.contains("-t"));
    assertTrue("--test option is missing from AutoCompleterWrapper",
        options.contains("--test"));
  }
}
