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

package com.cloudera.bundleprocessor.preprocessor.fileprocessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cloudera.bundleprocessor.preprocessor.exception.WrongInputTypeException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestFileUtils {

  private static final File WORKSPACE = new File("./src/test/testfileutils");

  @AfterClass
  public static void destroyWorkspace() {
    WORKSPACE.delete();
  }

  @Test
  public void testNotExistingFileFailsMkdir() {
    File file1 = mock(File.class);
    when(file1.exists()).thenReturn(false);
    when(file1.mkdirs()).thenReturn(false);
    try {
      FileUtils.findOrCreateDirectory(file1);
      fail("If directory creation fails, " +
          "findOrBuildDirectory should throw exception");
    } catch (IOException expected) {
    }
  }

  @Test
  public void testExistingFileFailsMkdir() throws IOException {
    File file2 = mock(File.class);
    when(file2.exists()).thenReturn(true);
    when(file2.mkdirs()).thenReturn(false);
    FileUtils.findOrCreateDirectory(file2);
  }

  @Test
  public void testNonExistingFileMkdir() throws IOException {
    File file3 = mock(File.class);
    when(file3.exists()).thenReturn(false);
    when(file3.mkdirs()).thenReturn(true);
    FileUtils.findOrCreateDirectory(file3);
  }

  @Test
  public void testExistingFileMkdir() throws IOException {
    File file4 = mock(File.class);
    when(file4.exists()).thenReturn(true);
    when(file4.mkdirs()).thenReturn(true);
    FileUtils.findOrCreateDirectory(file4);
  }

  @Test
  public void testCopyInputStream() throws IOException {
    String s = "test_text";
    InputStream is = new ByteArrayInputStream(s.getBytes());
    OutputStream os = new ByteArrayOutputStream();
    FileUtils.copyInputStream(is, os);
    assertEquals(s, os.toString());
  }

  @Test
  public void testIsZip() {
    assertTrue(FileUtils.isZip(new File("test.zip")));
    assertFalse(FileUtils.isZip(new File("test.tar.gz")));
    assertFalse(FileUtils.isZip(new File("test.rar")));
  }

  @Test
  public void testIsGz() {
    assertFalse(FileUtils.isGz(new File("test.zip")));
    assertTrue(FileUtils.isGz(new File("test.tar.gz")));
    assertFalse(FileUtils.isGz(new File("test.rar")));
  }

  @Test
  public void testDeleteFile() {
    File file1 = mock(File.class);
    when(file1.delete()).thenReturn(true);
    when(file1.getName()).thenReturn("file1");
    FileUtils.deleteFile(file1);

    File file2 = mock(File.class);
    when(file2.delete()).thenReturn(false);
    when(file2.getName()).thenReturn("file2");
    FileUtils.deleteFile(file2);
  }

  @Test
  public void testCopyFile() throws IOException {
    String testString = "test_string";

    File file = new File("target", "test");
    File subDir = new File("target", "subdir");
    File copied = new File(subDir, "test");
    try {
      if (!subDir.exists()) {
        assertTrue(subDir.mkdirs());
      }
      assertTrue(file.createNewFile());
      try (FileOutputStream fos = new FileOutputStream(file)) {
        fos.write(testString.getBytes());
      }
      FileUtils.copyFile(file, subDir);
      try (FileInputStream fis = new FileInputStream(copied)) {
        byte[] input = new byte[testString.length()];
        fis.read(input);
        assertEquals(testString, new String(input));
      }
    } finally {
      if (file.exists()) {
        file.delete();
      }
      if (copied.exists()) {
        copied.delete();
      }
      if (subDir.exists()) {
        subDir.delete();
      }
    }
  }

  @Test
  public void testCutExtension() throws WrongInputTypeException {
    assertEquals("test", FileUtils.cutExtension("test.zip", ".zip"));
    assertEquals("other", FileUtils.cutExtension("other.rar", ".rar"));
    try {
      FileUtils.cutExtension("third.yes", ".no");
      fail("no exception has occurred, though cutExtension "
          + "received a parameter with wrong extension");
    } catch (WrongInputTypeException expected) {

    }
  }

  @Test
  public void testSerialization() throws IOException, ClassNotFoundException {
    FileUtils.findOrCreateDirectory(WORKSPACE);
    String input = "sample text";
    String filepath = WORKSPACE + File.separator + "binaryfile";
    FileUtils.serialize(filepath, input);
    String output = (String) FileUtils.deserialize(filepath);
    assertEquals(input, output);
  }

  @Test
  public void testEmptyDirectory() throws IOException {
    buildAndEraseDirectory(true);
    assertTrue(WORKSPACE.exists());
  }

  @Test
  public void testDeleteDirectory() throws IOException {
    buildAndEraseDirectory(false);
    assertFalse(WORKSPACE.exists());
  }

  private void buildAndEraseDirectory(boolean keepMainFolder)
      throws IOException {
    FileUtils.findOrCreateDirectory(WORKSPACE);
    TemporaryFolder tempFolder = new TemporaryFolder(WORKSPACE);
    tempFolder.create();
    File subfolder = tempFolder.newFolder("subfolder");
    File file1 = tempFolder.newFile("test.txt");
    File file2 = tempFolder.newFile("subfolder/test.txt");
    assertTrue(subfolder.exists());
    assertTrue(file1.exists());
    assertTrue(file2.exists());
    if (keepMainFolder) {
      FileUtils.emptyDirectory(WORKSPACE.getAbsolutePath());
    } else {
      FileUtils.deleteDirectory(WORKSPACE.getAbsolutePath());
    }
    assertFalse(subfolder.exists());
    assertFalse(file1.exists());
    assertFalse(file2.exists());
  }
}
