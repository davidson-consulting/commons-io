/* Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.apache.commons.io.input;
/**
 * Tests for {@link Tailer}.
 */
public class TailerTest {
    @org.junit.jupiter.api.io.TempDir
    public static java.io.File temporaryFolder;

    private org.apache.commons.io.input.Tailer tailer;

    @org.junit.jupiter.api.AfterEach
    public void tearDown() {
        if (tailer != null) {
            tailer.stop();
        }
    }

    // Suppress "Add at least one assertion to this test case"
    @org.powerapi.jjoules.junit5.EnergyTest
    @java.lang.SuppressWarnings("squid:S2699")
    public void testLongFile() throws java.lang.Exception {
        final long delay = 50;
        final java.io.File file = new java.io.File(org.apache.commons.io.input.TailerTest.temporaryFolder, "testLongFile.txt");
        createFile(file, 0);
        try (final java.io.Writer writer = new java.io.FileWriter(file, true)) {
            for (int i = 0; i < 100000; i++) {
                writer.write("LineLineLineLineLineLineLineLineLineLine\n");
            }
            writer.write("SBTOURIST\n");
        }
        final org.apache.commons.io.input.TailerTest.TestTailerListener listener = new org.apache.commons.io.input.TailerTest.TestTailerListener();
        tailer = new org.apache.commons.io.input.Tailer(file, listener, delay, false);
        // final long start = System.currentTimeMillis();
        final java.lang.Thread thread = new java.lang.Thread(tailer);
        thread.start();
        java.util.List<java.lang.String> lines = listener.getLines();
        while (lines.isEmpty() || (!lines.get(lines.size() - 1).equals("SBTOURIST"))) {
            lines = listener.getLines();
        } 
        // System.out.println("Elapsed: " + (System.currentTimeMillis() - start));
        listener.clear();
    }

    // Suppress "Add at least one assertion to this test case"
    @org.powerapi.jjoules.junit5.EnergyTest
    @java.lang.SuppressWarnings("squid:S2699")
    public void testBufferBreak() throws java.lang.Exception {
        final long delay = 50;
        final java.io.File file = new java.io.File(org.apache.commons.io.input.TailerTest.temporaryFolder, "testBufferBreak.txt");
        createFile(file, 0);
        writeString(file, "SBTOURIST\n");
        final org.apache.commons.io.input.TailerTest.TestTailerListener listener = new org.apache.commons.io.input.TailerTest.TestTailerListener();
        tailer = new org.apache.commons.io.input.Tailer(file, listener, delay, false, 1);
        final java.lang.Thread thread = new java.lang.Thread(tailer);
        thread.start();
        java.util.List<java.lang.String> lines = listener.getLines();
        while (lines.isEmpty() || (!lines.get(lines.size() - 1).equals("SBTOURIST"))) {
            lines = listener.getLines();
        } 
        listener.clear();
    }

    @org.powerapi.jjoules.junit5.EnergyTest
    public void testMultiByteBreak() throws java.lang.Exception {
        // System.out.println("testMultiByteBreak() Default charset: " + Charset.defaultCharset().displayName());
        final long delay = 50;
        final java.io.File origin = org.apache.commons.io.TestResources.getFile("test-file-utf8.bin");
        final java.io.File file = new java.io.File(org.apache.commons.io.input.TailerTest.temporaryFolder, "testMultiByteBreak.txt");
        createFile(file, 0);
        final org.apache.commons.io.input.TailerTest.TestTailerListener listener = new org.apache.commons.io.input.TailerTest.TestTailerListener();
        final java.lang.String osname = java.lang.System.getProperty("os.name");
        final boolean isWindows = osname.startsWith("Windows");
        // Need to use UTF-8 to read & write the file otherwise it can be corrupted (depending on the default charset)
        final java.nio.charset.Charset charsetUTF8 = java.nio.charset.StandardCharsets.UTF_8;
        tailer = new org.apache.commons.io.input.Tailer(file, charsetUTF8, listener, delay, false, isWindows, org.apache.commons.io.IOUtils.DEFAULT_BUFFER_SIZE);
        final java.lang.Thread thread = new java.lang.Thread(tailer);
        thread.start();
        try (java.io.Writer out = new java.io.OutputStreamWriter(new java.io.FileOutputStream(file), charsetUTF8);java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(origin), charsetUTF8))) {
            final java.util.List<java.lang.String> lines = new java.util.ArrayList<>();
            java.lang.String line;
            while ((line = reader.readLine()) != null) {
                out.write(line);
                out.write("\n");
                lines.add(line);
            } 
            out.close();// ensure data is written

            final long testDelayMillis = delay * 10;
            org.apache.commons.io.test.TestUtils.sleep(testDelayMillis);
            final java.util.List<java.lang.String> tailerlines = listener.getLines();
            org.junit.jupiter.api.Assertions.assertEquals(lines.size(), tailerlines.size(), "line count");
            for (int i = 0, len = lines.size(); i < len; i++) {
                final java.lang.String expected = lines.get(i);
                final java.lang.String actual = tailerlines.get(i);
                if (!expected.equals(actual)) {
                    org.junit.jupiter.api.Assertions.fail((((((((("Line: " + i) + "\nExp: (") + expected.length()) + ") ") + expected) + "\nAct: (") + actual.length()) + ") ") + actual);
                }
            }
        }
    }

    @org.powerapi.jjoules.junit5.EnergyTest
    public void testTailerEof() throws java.lang.Exception {
        // Create & start the Tailer
        final long delay = 50;
        final java.io.File file = new java.io.File(org.apache.commons.io.input.TailerTest.temporaryFolder, "tailer2-test.txt");
        createFile(file, 0);
        final org.apache.commons.io.input.TailerTest.TestTailerListener listener = new org.apache.commons.io.input.TailerTest.TestTailerListener();
        tailer = new org.apache.commons.io.input.Tailer(file, listener, delay, false);
        final java.lang.Thread thread = new java.lang.Thread(tailer);
        thread.start();
        // Write some lines to the file
        writeString(file, "Line");
        org.apache.commons.io.test.TestUtils.sleep(delay * 2);
        java.util.List<java.lang.String> lines = listener.getLines();
        org.junit.jupiter.api.Assertions.assertEquals(0, lines.size(), "1 line count");
        writeString(file, " one\n");
        org.apache.commons.io.test.TestUtils.sleep(delay * 2);
        lines = listener.getLines();
        org.junit.jupiter.api.Assertions.assertEquals(1, lines.size(), "1 line count");
        org.junit.jupiter.api.Assertions.assertEquals("Line one", lines.get(0), "1 line 1");
        listener.clear();
    }

    @org.powerapi.jjoules.junit5.EnergyTest
    public void testTailer() throws java.lang.Exception {
        // Create & start the Tailer
        final long delayMillis = 50;
        final java.io.File file = new java.io.File(org.apache.commons.io.input.TailerTest.temporaryFolder, "tailer1-test.txt");
        createFile(file, 0);
        final org.apache.commons.io.input.TailerTest.TestTailerListener listener = new org.apache.commons.io.input.TailerTest.TestTailerListener();
        final java.lang.String osname = java.lang.System.getProperty("os.name");
        final boolean isWindows = osname.startsWith("Windows");
        tailer = new org.apache.commons.io.input.Tailer(file, listener, delayMillis, false, isWindows);
        final java.lang.Thread thread = new java.lang.Thread(tailer);
        thread.start();
        // Write some lines to the file
        write(file, "Line one", "Line two");
        final long testDelayMillis = delayMillis * 10;
        org.apache.commons.io.test.TestUtils.sleep(testDelayMillis);
        java.util.List<java.lang.String> lines = listener.getLines();
        org.junit.jupiter.api.Assertions.assertEquals(2, lines.size(), "1 line count");
        org.junit.jupiter.api.Assertions.assertEquals("Line one", lines.get(0), "1 line 1");
        org.junit.jupiter.api.Assertions.assertEquals("Line two", lines.get(1), "1 line 2");
        listener.clear();
        // Write another line to the file
        write(file, "Line three");
        org.apache.commons.io.test.TestUtils.sleep(testDelayMillis);
        lines = listener.getLines();
        org.junit.jupiter.api.Assertions.assertEquals(1, lines.size(), "2 line count");
        org.junit.jupiter.api.Assertions.assertEquals("Line three", lines.get(0), "2 line 3");
        listener.clear();
        // Check file does actually have all the lines
        lines = org.apache.commons.io.FileUtils.readLines(file, "UTF-8");
        org.junit.jupiter.api.Assertions.assertEquals(3, lines.size(), "3 line count");
        org.junit.jupiter.api.Assertions.assertEquals("Line one", lines.get(0), "3 line 1");
        org.junit.jupiter.api.Assertions.assertEquals("Line two", lines.get(1), "3 line 2");
        org.junit.jupiter.api.Assertions.assertEquals("Line three", lines.get(2), "3 line 3");
        // Delete & re-create
        file.delete();
        org.junit.jupiter.api.Assertions.assertFalse(file.exists(), "File should not exist");
        createFile(file, 0);
        org.junit.jupiter.api.Assertions.assertTrue(file.exists(), "File should now exist");
        org.apache.commons.io.test.TestUtils.sleep(testDelayMillis);
        // Write another line
        write(file, "Line four");
        org.apache.commons.io.test.TestUtils.sleep(testDelayMillis);
        lines = listener.getLines();
        org.junit.jupiter.api.Assertions.assertEquals(1, lines.size(), "4 line count");
        org.junit.jupiter.api.Assertions.assertEquals("Line four", lines.get(0), "4 line 3");
        listener.clear();
        // Stop
        thread.interrupt();
        org.apache.commons.io.test.TestUtils.sleep(testDelayMillis * 4);
        write(file, "Line five");
        org.junit.jupiter.api.Assertions.assertEquals(0, listener.getLines().size(), "4 line count");
        org.junit.jupiter.api.Assertions.assertNotNull(listener.exception, "Missing InterruptedException");
        org.junit.jupiter.api.Assertions.assertTrue(listener.exception instanceof java.lang.InterruptedException, "Unexpected Exception: " + listener.exception);
        org.junit.jupiter.api.Assertions.assertEquals(1, listener.initialized, "Expected init to be called");
        // assertEquals(0 , listener.notFound, "fileNotFound should not be called"); // there is a window when it might be called
        org.junit.jupiter.api.Assertions.assertEquals(1, listener.rotated, "fileRotated should be be called");
    }

    @org.powerapi.jjoules.junit5.EnergyTest
    public void testTailerEndOfFileReached() throws java.lang.Exception {
        // Create & start the Tailer
        final long delayMillis = 50;
        final long testDelayMillis = delayMillis * 10;
        final java.io.File file = new java.io.File(org.apache.commons.io.input.TailerTest.temporaryFolder, "tailer-eof-test.txt");
        createFile(file, 0);
        final org.apache.commons.io.input.TailerTest.TestTailerListener listener = new org.apache.commons.io.input.TailerTest.TestTailerListener();
        final java.lang.String osname = java.lang.System.getProperty("os.name");
        final boolean isWindows = osname.startsWith("Windows");
        tailer = new org.apache.commons.io.input.Tailer(file, listener, delayMillis, false, isWindows);
        final java.lang.Thread thread = new java.lang.Thread(tailer);
        thread.start();
        // write a few lines
        write(file, "line1", "line2", "line3");
        org.apache.commons.io.test.TestUtils.sleep(testDelayMillis);
        // write a few lines
        write(file, "line4", "line5", "line6");
        org.apache.commons.io.test.TestUtils.sleep(testDelayMillis);
        // write a few lines
        write(file, "line7", "line8", "line9");
        org.apache.commons.io.test.TestUtils.sleep(testDelayMillis);
        // May be > 3 times due to underlying OS behavior wrt streams
        org.junit.jupiter.api.Assertions.assertTrue(listener.reachedEndOfFile >= 3, "end of file reached at least 3 times");
    }

    protected void createFile(final java.io.File file, final long size) throws java.io.IOException {
        if (!file.getParentFile().exists()) {
            throw new java.io.IOException(("Cannot create file " + file) + " as the parent directory does not exist");
        }
        try (final java.io.BufferedOutputStream output = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file))) {
            org.apache.commons.io.test.TestUtils.generateTestData(output, size);
        }
        // try to make sure file is found
        // (to stop continuum occasionally failing)
        java.io.RandomAccessFile reader = null;
        try {
            while (reader == null) {
                try {
                    reader = new java.io.RandomAccessFile(file.getPath(), "r");
                } catch (final java.io.FileNotFoundException ignore) {
                }
                try {
                    org.apache.commons.io.test.TestUtils.sleep(200L);
                } catch (final java.lang.InterruptedException ignore) {
                    // ignore
                }
            } 
        } finally {
            try {
                org.apache.commons.io.IOUtils.close(reader);
            } catch (final java.io.IOException ignored) {
                // ignored
            }
        }
    }

    /**
     * Append some lines to a file
     */
    private void write(final java.io.File file, final java.lang.String... lines) throws java.lang.Exception {
        try (java.io.FileWriter writer = new java.io.FileWriter(file, true)) {
            for (final java.lang.String line : lines) {
                writer.write(line + "\n");
            }
        }
    }

    /**
     * Append a string to a file
     */
    private void writeString(final java.io.File file, final java.lang.String... strings) throws java.lang.Exception {
        try (java.io.FileWriter writer = new java.io.FileWriter(file, true)) {
            for (final java.lang.String string : strings) {
                writer.write(string);
            }
        }
    }

    @org.powerapi.jjoules.junit5.EnergyTest
    public void testStopWithNoFile() throws java.lang.Exception {
        final java.io.File file = new java.io.File(org.apache.commons.io.input.TailerTest.temporaryFolder, "nosuchfile");
        org.junit.jupiter.api.Assertions.assertFalse(file.exists(), "nosuchfile should not exist");
        final org.apache.commons.io.input.TailerTest.TestTailerListener listener = new org.apache.commons.io.input.TailerTest.TestTailerListener();
        final int delay = 100;
        final int idle = 50;// allow time for thread to work

        tailer = org.apache.commons.io.input.Tailer.create(file, listener, delay, false);
        org.apache.commons.io.test.TestUtils.sleep(idle);
        tailer.stop();
        org.apache.commons.io.test.TestUtils.sleep(delay + idle);
        org.junit.jupiter.api.Assertions.assertNull(listener.exception, "Should not generate Exception");
        org.junit.jupiter.api.Assertions.assertEquals(1, listener.initialized, "Expected init to be called");
        org.junit.jupiter.api.Assertions.assertTrue(listener.notFound > 0, "fileNotFound should be called");
        org.junit.jupiter.api.Assertions.assertEquals(0, listener.rotated, "fileRotated should be not be called");
        org.junit.jupiter.api.Assertions.assertEquals(0, listener.reachedEndOfFile, "end of file never reached");
    }

    /* Tests [IO-357][Tailer] InterruptedException while the thead is sleeping is silently ignored. */
    @org.powerapi.jjoules.junit5.EnergyTest
    public void testInterrupt() throws java.lang.Exception {
        final java.io.File file = new java.io.File(org.apache.commons.io.input.TailerTest.temporaryFolder, "nosuchfile");
        org.junit.jupiter.api.Assertions.assertFalse(file.exists(), "nosuchfile should not exist");
        final org.apache.commons.io.input.TailerTest.TestTailerListener listener = new org.apache.commons.io.input.TailerTest.TestTailerListener();
        // Use a long delay to try to make sure the test thread calls interrupt() while the tailer thread is sleeping.
        final int delay = 1000;
        final int idle = 50;// allow time for thread to work

        tailer = new org.apache.commons.io.input.Tailer(file, listener, delay, false, org.apache.commons.io.IOUtils.DEFAULT_BUFFER_SIZE);
        final java.lang.Thread thread = new java.lang.Thread(tailer);
        thread.setDaemon(true);
        thread.start();
        org.apache.commons.io.test.TestUtils.sleep(idle);
        thread.interrupt();
        org.apache.commons.io.test.TestUtils.sleep(delay + idle);
        org.junit.jupiter.api.Assertions.assertNotNull(listener.exception, "Missing InterruptedException");
        org.junit.jupiter.api.Assertions.assertTrue(listener.exception instanceof java.lang.InterruptedException, "Unexpected Exception: " + listener.exception);
        org.junit.jupiter.api.Assertions.assertEquals(1, listener.initialized, "Expected init to be called");
        org.junit.jupiter.api.Assertions.assertTrue(listener.notFound > 0, "fileNotFound should be called");
        org.junit.jupiter.api.Assertions.assertEquals(0, listener.rotated, "fileRotated should be not be called");
        org.junit.jupiter.api.Assertions.assertEquals(0, listener.reachedEndOfFile, "end of file never reached");
    }

    @org.powerapi.jjoules.junit5.EnergyTest
    public void testStopWithNoFileUsingExecutor() throws java.lang.Exception {
        final java.io.File file = new java.io.File(org.apache.commons.io.input.TailerTest.temporaryFolder, "nosuchfile");
        org.junit.jupiter.api.Assertions.assertFalse(file.exists(), "nosuchfile should not exist");
        final org.apache.commons.io.input.TailerTest.TestTailerListener listener = new org.apache.commons.io.input.TailerTest.TestTailerListener();
        final int delay = 100;
        final int idle = 50;// allow time for thread to work

        tailer = new org.apache.commons.io.input.Tailer(file, listener, delay, false);
        final java.util.concurrent.Executor exec = new java.util.concurrent.ScheduledThreadPoolExecutor(1);
        exec.execute(tailer);
        org.apache.commons.io.test.TestUtils.sleep(idle);
        tailer.stop();
        org.apache.commons.io.test.TestUtils.sleep(delay + idle);
        org.junit.jupiter.api.Assertions.assertNull(listener.exception, "Should not generate Exception");
        org.junit.jupiter.api.Assertions.assertEquals(1, listener.initialized, "Expected init to be called");
        org.junit.jupiter.api.Assertions.assertTrue(listener.notFound > 0, "fileNotFound should be called");
        org.junit.jupiter.api.Assertions.assertEquals(0, listener.rotated, "fileRotated should be not be called");
        org.junit.jupiter.api.Assertions.assertEquals(0, listener.reachedEndOfFile, "end of file never reached");
    }

    @org.powerapi.jjoules.junit5.EnergyTest
    public void testIO335() throws java.lang.Exception {
        // test CR behavior
        // Create & start the Tailer
        final long delayMillis = 50;
        final java.io.File file = new java.io.File(org.apache.commons.io.input.TailerTest.temporaryFolder, "tailer-testio334.txt");
        createFile(file, 0);
        final org.apache.commons.io.input.TailerTest.TestTailerListener listener = new org.apache.commons.io.input.TailerTest.TestTailerListener();
        tailer = new org.apache.commons.io.input.Tailer(file, listener, delayMillis, false);
        final java.lang.Thread thread = new java.lang.Thread(tailer);
        thread.start();
        // Write some lines to the file
        writeString(file, "CRLF\r\n", "LF\n", "CR\r", "CRCR\r\r", "trail");
        final long testDelayMillis = delayMillis * 10;
        org.apache.commons.io.test.TestUtils.sleep(testDelayMillis);
        final java.util.List<java.lang.String> lines = listener.getLines();
        org.junit.jupiter.api.Assertions.assertEquals(4, lines.size(), "line count");
        org.junit.jupiter.api.Assertions.assertEquals("CRLF", lines.get(0), "line 1");
        org.junit.jupiter.api.Assertions.assertEquals("LF", lines.get(1), "line 2");
        org.junit.jupiter.api.Assertions.assertEquals("CR", lines.get(2), "line 3");
        org.junit.jupiter.api.Assertions.assertEquals("CRCR\r", lines.get(3), "line 4");
    }

    /**
     * Test {@link TailerListener} implementation.
     */
    private static class TestTailerListener extends org.apache.commons.io.input.TailerListenerAdapter {
        // Must be synchronized because it is written by one thread and read by another
        private final java.util.List<java.lang.String> lines = java.util.Collections.synchronizedList(new java.util.ArrayList<java.lang.String>());

        volatile java.lang.Exception exception;

        volatile int notFound;

        volatile int rotated;

        volatile int initialized;

        volatile int reachedEndOfFile;

        @java.lang.Override
        public void handle(final java.lang.String line) {
            lines.add(line);
        }

        public java.util.List<java.lang.String> getLines() {
            return lines;
        }

        public void clear() {
            lines.clear();
        }

        @java.lang.Override
        public void handle(final java.lang.Exception e) {
            exception = e;
        }

        @java.lang.Override
        public void init(final org.apache.commons.io.input.Tailer tailer) {
            initialized++;// not atomic, but OK because only updated here.

        }

        @java.lang.Override
        public void fileNotFound() {
            notFound++;// not atomic, but OK because only updated here.

        }

        @java.lang.Override
        public void fileRotated() {
            rotated++;// not atomic, but OK because only updated here.

        }

        @java.lang.Override
        public void endOfFileReached() {
            reachedEndOfFile++;// not atomic, but OK because only updated here.

        }
    }
}