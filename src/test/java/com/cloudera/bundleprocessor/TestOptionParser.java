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

package com.cloudera.bundleprocessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;

/**
 * Test various (required, with arguments) options
 * that the OptionParser can handle.
 */
public class TestOptionParser {
  private static final String COMMAND_NAME = "test";
  private OptionParser parser;

  /**
   * Create an OptionParser with different options
   * that can be checked by the tests.
   */
  @Before
  public void setUp() {
    this.parser = new OptionParser.Builder()
        .setCommandName(COMMAND_NAME)
        .addDefaultOptions()
        .addOption("oar", "optargreq", true, "description1", true)
        .addOption("oa", "optarg", true, "description2", false)
        .addOption("or", "optreq", false, "description3", true)
        .addOption("o", "opt", false, "description4", false)
        .build();
  }

  @Test
  public void testParserWithoutParse() {
    // test basic fields that should work without calling parse()
    assertEquals(COMMAND_NAME, parser.getCommandName());
    assertNotNull("The parser object should not be null!",
        parser.getOptionsObject());
    // printHelp should not throw exception
    parser.printHelp();
  }

  @Test
  public void testParseWithoutRequiredOption() {
    // parse without required argument -oar
    String[] input = {"-or"};
    try {
      parser.parse(input);
      fail("Parsing should have failed");
    } catch (ParseException expected) {
    }
  }

  @Test
  public void testParseWithoutRequiredArg() {
    // parse without required argument -or having no argument
    String[] input = {"-or", "-oar"};
    try {
      parser.parse(input);
      fail("Parsing should have failed");
    } catch (ParseException expected) {
    }
  }

  @Test
  public void testParseWithRequiredOptions() {
    // parse without required argument -or having no argument
    String[] input = {"-oar", "arg", "-or"};
    try {
      parser.parse(input);
    } catch (ParseException e) {
      fail("Parsing should have succeeded");
    }
    assertTrue(parser.checkParameter("or"));
    assertFalse(parser.checkParameter("o"));
    assertTrue(parser.checkParameter("oar"));
    assertEquals("expected \"arg\" as argument of -oar",
        parser.getParameter("oar"), "arg");
    assertFalse(parser.checkParameter("oa"));
    assertNull(parser.getParameter("oa"));
  }

  @Test
  public void testParseWithAllOptions() {
    // parse without required argument -or having no argument
    String[] input = {"-oar", "arg1", "-or", "-oa", "arg2", "-o"};
    try {
      parser.parse(input);
    } catch (ParseException e) {
      fail("Parsing should have succeeded");
    }
    assertTrue(parser.checkParameter("or"));
    assertTrue(parser.checkParameter("o"));
    assertTrue(parser.checkParameter("oar"));
    assertEquals("expected \"arg\" as argument of -oar",
        parser.getParameter("oar"), "arg1");
    assertTrue(parser.checkParameter("oa"));
    assertEquals("expected \"arg\" as argument of -oa",
        parser.getParameter("oa"), "arg2");
  }
}
