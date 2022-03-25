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

package com.cloudera.bundleprocessor.subshell.search.engine;

import com.cloudera.bundleprocessor.subshell.search.engine.cache.Cache;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.LinesOfLogs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryCacheHandler implements CacheHandler<Query, List<Matcher>> {

  private final Cache<Query, LinesOfLogs> cache;

  public QueryCacheHandler(Cache<Query, LinesOfLogs> cache) {
    this.cache = cache;
  }

  /**
   * {code writeToCache} writes a Query and its output into the cache.
   *
   * @param query    the key in the cache
   * @param matchers the output of the query, the value in the cache
   */
  @Override
  public void writeToCache(Query query, List<Matcher> matchers) {
    if (cache == null) {
      return;
    }
    List<String> matchedLines = new ArrayList<>();
    for (Matcher matcher : matchers) {
      StringBuilder stringBuilder = new StringBuilder();
      while (matcher.find()) {
        stringBuilder.append(matcher.group()).append("\n");
      }
      if (stringBuilder.length() != 0) {
        matchedLines.add(stringBuilder.toString());
      }
      matcher.reset();
    }
    cache.set(query, new LinesOfLogs(matchedLines));
  }


  /**
   * Reads the value for the specified query from cache.
   *
   * @param query search request
   * @return List of Matchers containing the output of the query
   */
  @Override
  public List<Matcher> readFromCache(Query query) {
    if (cache == null || cache.get(query) == null) {
      return null;
    }
    List<String> allMatchedLines = cache.get(query).getLines();
    if (allMatchedLines == null) {
      return null;
    } else {
      List<Matcher> matchers = new ArrayList<>();
      for (String matchedLinesFromOneFile : allMatchedLines) {
        Pattern pattern = query.getPattern();
        Matcher matcher = pattern.matcher(matchedLinesFromOneFile);
        matchers.add(matcher);
      }
      return matchers;
    }
  }
}
