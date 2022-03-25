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

public class Cache {

  private String cacheDirectory;
  private String cacheType;
  private String cacheItemCapacity;

  public String getCacheDirectory() {
    return cacheDirectory;
  }

  public void setCacheDirectory(String cacheDirectory) {
    this.cacheDirectory = cacheDirectory;
  }

  public String getCacheType() {
    return cacheType;
  }

  public void setCacheType(String cacheType) {
    this.cacheType = cacheType;
  }

  public String getCacheItemCapacity() {
    return cacheItemCapacity;
  }

  public void setCacheItemCapacity(String cacheItemCapacity) {
    this.cacheItemCapacity = cacheItemCapacity;
  }


}
