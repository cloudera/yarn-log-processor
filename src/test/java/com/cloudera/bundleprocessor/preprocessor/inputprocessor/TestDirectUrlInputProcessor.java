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

package com.cloudera.bundleprocessor.preprocessor.inputprocessor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cloudera.bundleprocessor.preprocessor.fileprocessor.FileDownloader;
import com.cloudera.bundleprocessor.subshell.context.Config;
import com.cloudera.bundleprocessor.util.DateUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.ConnectIOException;
import org.junit.Test;

public class TestDirectUrlInputProcessor {

  @Test
  public void testValidUrl() throws IOException {
    File dir = new File("dir");
    File expectedFile = new File("file");
    URL url = new URL(new URL(FakeURLHandler.getValidURLAddress()), "",
        new FakeURLHandler());
    DirectUrlInputProcessor processor =
        spy(new DirectUrlInputProcessor(url, dir));
    FileDownloader fileDownloader = mock(FileDownloader.class);
    when(processor.createFileDownloader()).thenReturn(fileDownloader);
    when(fileDownloader.downloadByUrl(eq(url), any())).thenReturn(expectedFile);
    File actualFile = processor.process(Config.createConfig());
    verify(fileDownloader).downloadByUrl(url, DateUtils.getCurrentDate());
    assertEquals("The processor did not give back the expected file",
        actualFile, expectedFile);
  }

  @Test(expected = ConnectIOException.class)
  public void testInvalidHost() throws IOException {
    File dir = new File("dir");
    Config config = Config.createConfig();
    URL url = new URL(new URL(FakeURLHandler.getInvalidHostName()), "",
        new FakeURLHandler());
    DirectUrlInputProcessor processor = new DirectUrlInputProcessor(url, dir);
    processor.process(config);
  }

  @Test(expected = ConnectIOException.class)
  public void testInvalidParameters() throws IOException {
    File dir = new File("dir");
    Config config = Config.createConfig();
    URL url = new URL(new URL(FakeURLHandler.getInvalidParameter()), "",
        new FakeURLHandler());
    DirectUrlInputProcessor processor = new DirectUrlInputProcessor(url, dir);
    processor.process(config);
  }
}
