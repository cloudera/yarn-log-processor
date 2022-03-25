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

import com.cloudera.bundleprocessor.subshell.search.engine.Query;
import com.cloudera.bundleprocessor.subshell.search.engine.SearchEngine;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.Cache;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.CacheIOExecutor;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.FileBasedCacheIOExecutor;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.GeneralCache;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.InMemoryLRUCache;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.LinesOfLogs;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code Context} is storing pieces of information
 * used multiple times across Subshell.
 * These are generated depending on user input.
 */
public class Context {

  private static final Logger LOG =
      LoggerFactory.getLogger(Context.class);

  private final Config config;
  private SearchEngine searchEngine;
  private SearchIntent searchIntent;

  public Context() {
    config = Config.createConfig();
  }

  public Config getConfig() {
    return config;
  }

  @VisibleForTesting
  SearchEngine createSearchEngine(File mainDirectory) {
    return new SearchEngine(mainDirectory);
  }

  /**
   * This method sets the searchEngine depending on the targetDirectory.
   *
   * @param targetDirectory is the directory containing the log files
   */
  public void setupSearchEngine(File targetDirectory) {
    this.searchEngine = createSearchEngine(targetDirectory);
    Cache<Query, LinesOfLogs> cache = null;
    if (config.getCache().getCacheType().equals("InMemoryLRUCache")) {
      int cacheCapacity =
          Integer.parseInt(config.getCache().getCacheItemCapacity());
      cache = new InMemoryLRUCache<>(cacheCapacity);
    } else if (config.getCache().getCacheType().equals("GeneralCache")) {
      File cacheDir = new File(config.getCache().getCacheDirectory());
      CacheIOExecutor cacheIOExecutor =
          new FileBasedCacheIOExecutor(cacheDir.getAbsolutePath());
      cache = new GeneralCache<>(cacheIOExecutor);
    } else if (config.getCache().getCacheType() != null ||
        !config.getCache().getCacheType().equals("")) {
      LOG.error("CacheType couldn't be recognised.");
    }
    this.searchEngine.init(config, cache);
  }

  public SearchEngine getSearchEngine() {
    return searchEngine;
  }

  public SearchIntent getSearchIntent() {
    return searchIntent;
  }

  public void setSearchIntent(SearchIntent searchIntent) {
    this.searchIntent = searchIntent;
  }
}
