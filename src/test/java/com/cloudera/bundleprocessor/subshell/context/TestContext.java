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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cloudera.bundleprocessor.subshell.search.engine.SearchEngine;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.Cache;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.InMemoryLRUCache;
import java.io.File;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class TestContext {

  @Test
  public void testConfig() {
    Context context = new Context();
    assertEquals(
        ".*-site\\.xml", context.getConfig().getRegexes().getConfigFile());

    assertEquals("workspace",
        context.getConfig().getDirs().getDirectoryNameForYarnRelatedLogs());
  }

  @Test
  public void setupSearchEngine() {
    Context context = spy(Context.class);
    assertNull(context.getSearchEngine());
    SearchEngine searchEngine = mock(SearchEngine.class);
    when(context.createSearchEngine(any())).thenReturn(searchEngine);
    File mainDirectory = new File("./src/test/resources");
    context.setupSearchEngine(mainDirectory);
    verify(context).createSearchEngine(mainDirectory);
    ArgumentCaptor<Cache> parameterCaptor = ArgumentCaptor
        .forClass(Cache.class);
    verify(searchEngine).init(eq(context.getConfig()),
        parameterCaptor.capture());
    Cache actualCache = parameterCaptor.getValue();
    assertTrue(actualCache instanceof InMemoryLRUCache);
    assertEquals(10, ((InMemoryLRUCache) actualCache).getCapacity());
    assertNotNull(context.getSearchEngine());
  }

  @Test
  public void testSearchIntent() {
    Context context = new Context();
    assertNull(context.getSearchIntent());
    SearchIntent intent = new SearchIntent.Builder()
        .withLaunchingShell(true)
        .build();
    context.setSearchIntent(intent);
    assertTrue("Should be launching with shell",
        context.getSearchIntent().isLaunchingShell());
    assertNull(
        "The SearchIntent command should be null, when launching with shell",
        context.getSearchIntent().getCommand());
  }
}
