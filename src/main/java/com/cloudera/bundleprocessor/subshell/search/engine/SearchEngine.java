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

import com.cloudera.bundleprocessor.subshell.context.Config;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.Cache;
import com.cloudera.bundleprocessor.subshell.search.engine.cache.LinesOfLogs;
import com.cloudera.bundleprocessor.subshell.search.engine.util.LogManipulator;
import com.cloudera.bundleprocessor.subshell.search.request.Executable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * SearchEngine executes all {@link Executable} (~search requests) in the logs.
 */
public class SearchEngine {

  private static final Logger LOG =
      LoggerFactory.getLogger(SearchEngine.class);

  private final File logFolder;
  private QueryCacheHandler queryCacheHandler;
  private List<File> files = new ArrayList<>();
  private List<File> rmFiles = new ArrayList<>();
  private List<File> nmFiles = new ArrayList<>();

  /**
   * SearchEngine constructor needs to locate the folder
   * where the log files can be found.
   * The constructor also categorizes the files to make it easier
   * to search in the folder.
   *
   * @param logFolder is the user-defined folder where SearchEngine is searching
   */
  public SearchEngine(File logFolder) {
    this.logFolder = logFolder;
  }

  /**
   * The {@code init} function searches for relevant files in the log folder.
   * These files will be stored in lists, so SearchEngine later can use them.
   *
   * @param config user configurations
   * @param cache  cache to save already found lines
   */
  public void init(@NotNull Config config, Cache<Query, LinesOfLogs> cache) {
    final File logDir = config.getLogDir(logFolder);
    if (!logDir.exists()) {
      throw new RuntimeException("The provided log folder doesn't exist");
    }
    File[] filesArr = logDir.listFiles();
    if (filesArr != null) {
      files = Arrays.asList(filesArr);
      rmFiles = files.stream().filter(
          LogManipulator::isRMlog).collect(Collectors.toList());
      nmFiles = files.stream().filter(
          LogManipulator::isNMlog).collect(Collectors.toList());
    } else {
      throw new RuntimeException("The provided log folder was empty");
    }
    this.queryCacheHandler = new QueryCacheHandler(cache);
  }

  /**
   * The {@code createMatchers} executes {@link Query}s (search requests).
   *
   * @param query {@link Query} containing the pattern to match in logs
   * @return matchers with the matches found in logs
   */
  public List<Matcher> createMatchers(Query query) throws IOException {
    final List<Matcher> cacheMatchers = queryCacheHandler.readFromCache(query);
    if (cacheMatchers == null) {
      LOG.debug("Query is not present in the Cache");
      final List<Matcher> newMatchers = createMatchersForNewQuery(query);
      queryCacheHandler.writeToCache(query, newMatchers);
      return newMatchers;
    } else {
      LOG.debug("Query is present in the Cache");
      return cacheMatchers;
    }
  }

  private List<Matcher> createMatchersForNewQuery(Query query)
      throws IOException {
    List<Matcher> matchers = new ArrayList<>();
    if (query.searchInRmLogs()) {
      searchInRmLogs(query, matchers);
    }
    if (query.searchInNmLogs()) {
      searchInNmLogs(query, matchers);
    }
    if (query.searchInFileNames()) {
      searchInFileNames(query, matchers);
    }
    return matchers;
  }

  private void searchInRmLogs(Query query, List<Matcher> matchers)
      throws IOException {
    for (File file : rmFiles) {
      searchInFile(query, file, matchers);
    }
  }

  private void searchInNmLogs(Query query, List<Matcher> matchers)
      throws IOException {
    for (File file : nmFiles) {
      searchInFile(query, file, matchers);
    }
  }

  private void searchInFile(Query query, File file, List<Matcher> matchers)
      throws IOException {
    LOG.debug("Checking file: " + file.getName());
    matchers.add(query.getPattern().matcher(LogManipulator.readFile(file)));
  }

  private void searchInFileNames(Query query, List<Matcher> matchers) {
    for (File file : files) {
      LOG.debug("Check filename: " + file.getName());
      matchers.add(query.getPattern().matcher(file.getName()));
    }
  }
}


