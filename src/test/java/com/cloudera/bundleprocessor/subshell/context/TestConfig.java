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

package com.cloudera.bundleprocessor.subshell.context;

import com.cloudera.bundleprocessor.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class TestConfig {

  private static final String TEST_RESOURCES_FOLDER = "./src/test/resources";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Test(expected = RuntimeException.class)
  public void testNonExistentConfig() {
    Config.createConfig(new File(TEST_RESOURCES_FOLDER, "nonExistent.json"));
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidConfig() {
    Config.createConfig(new File(TEST_RESOURCES_FOLDER, "invalidConfig.json"));
  }

  @Test
  public void testValidConfig() throws IOException {
    testConfig("configForTest.json");
  }

  @Test(expected = RuntimeException.class)
  public void testWithExtraProperties() throws IOException {
    testConfig("configWithExtraProperties.json");
  }

  @Test
  public void testWithMissingProperties() throws IOException {
    testConfig("configWithMissingProperties.json");
  }

  @Test
  public void testDefaultConfig() throws IOException {
    Config config = Config.createConfig();
    JsonNode jsonNode = OBJECT_MAPPER.readTree(Files.newInputStream(
        Paths.get(Constants.DEFAULT_CONFIG_PATH)));
    checkValues(config, jsonNode);
  }

  private void testConfig(String fileName) throws IOException {
    Config config = Config.createConfig(
        new File(TEST_RESOURCES_FOLDER, fileName));
    JsonNode jsonNode = OBJECT_MAPPER.readTree(
        Files.newInputStream(Paths.get(TEST_RESOURCES_FOLDER, fileName)));
    checkValues(config, jsonNode);
  }

  private void checkValues(Config config, JsonNode jsonNode) {
    assertEquals(
        jsonNode.get("regularExpressions").get("configFile").asText(),
        config.getRegexes().getConfigFile());

    assertEquals(
        jsonNode.get("directoryNames").get("directoryNameForYarnRelatedLogs").
            asText(), config.getDirs().getDirectoryNameForYarnRelatedLogs());
  }
}
