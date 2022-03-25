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

package com.cloudera.bundleprocessor.preprocessor.cliparser;

import com.cloudera.bundleprocessor.preprocessor.inputprocessor.DirectUrlInputProcessor;
import com.cloudera.bundleprocessor.preprocessor.inputprocessor.InputProcessor;
import com.cloudera.bundleprocessor.preprocessor.inputprocessor.LocalFileInputProcessor;
import com.cloudera.bundleprocessor.subshell.context.SearchIntent;

import java.io.File;
import java.net.URL;

/**
 * InputParams is a context class storing parameters parsed from the CLI.
 * It is responsible for processing the input parameters.
 * There are two main types of {@link InputProcessor}
 * processing 2 types of input: URL address and local file path.
 * Defining more than 1 out of these three results
 * in {@link IllegalArgumentException}.
 * If none of the 3 is provided, the {@link InputProcessor} attribute
 * will be assigned null.
 */
public final class InputParams {
  private final File mainDirectory;
  private final InputProcessor processor;
  private final boolean keepOriginalFile;
  private final SearchIntent searchIntent;

  private InputParams(Builder builder) {
    this.mainDirectory = builder.mainDirectory;
    this.processor = createProcessor(builder);
    this.keepOriginalFile = builder.keepOriginalFile;
    this.searchIntent = builder.searchIntentBuilder.build();
  }

  private InputProcessor createProcessor(Builder builder) {
    // if none of the input types are provided, processor will be null
    InputProcessor processor = null;
    int notNullInputParameters = 0;
    if (builder.localFile != null) {
      processor = new LocalFileInputProcessor(builder.localFile);
      notNullInputParameters++;
    }
    if (builder.directUrl != null) {
      processor = new DirectUrlInputProcessor(
          builder.directUrl, builder.mainDirectory);
      notNullInputParameters++;
    }
    if (notNullInputParameters > 1) {
      throw new IllegalArgumentException(
          "More than one input parameter was provided");
    }
    return processor;
  }

  public File getMainDirectory() {
    return mainDirectory;
  }

  public InputProcessor getProcessor() {
    return processor;
  }

  public boolean getKeepOriginalFile() {
    return keepOriginalFile;
  }

  public SearchIntent getSearchIntent() {
    return searchIntent;
  }

  /**
   * Builder for the InputParams context class.
   */
  public static class Builder {

    private final SearchIntent.Builder searchIntentBuilder =
        new SearchIntent.Builder();
    private File mainDirectory;
    private File localFile;
    private URL directUrl;
    private boolean keepOriginalFile;

    public Builder() {
    }

    public Builder withMainDirectory(File mainDirectory) {
      this.mainDirectory = mainDirectory;
      return this;
    }

    /**
     * Adds local file path input parameter to the Builder.
     * Only one input parameter can be added.
     *
     * @param localFile the local file path
     * @return Builder
     */
    public Builder withLocalFile(File localFile) {
      this.localFile = localFile;
      return this;
    }

    /**
     * Adds URL address input parameter to the Builder.
     * Only one input parameter can be added.
     *
     * @param directUrl the direct URL address
     * @return Builder
     */
    public Builder withDirectUrl(URL directUrl) {
      this.directUrl = directUrl;
      return this;
    }

    public Builder withKeepOriginalFile(boolean keepOriginalFile) {
      this.keepOriginalFile = keepOriginalFile;
      return this;
    }

    public Builder withShell(boolean withShell) {
      this.searchIntentBuilder.withLaunchingShell(withShell);
      return this;
    }

    public Builder withCommand(String commandStr) {
      this.searchIntentBuilder.withCommand(commandStr);
      return this;
    }

    public InputParams build() throws IllegalArgumentException {
      return new InputParams(this);
    }
  }
}
