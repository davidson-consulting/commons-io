package org.apache.commons.io.input;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.test.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
public class TailerTest {
    @TempDir
    public static File temporaryFolder;

    private Tailer tailer;

    @AfterEach
    public void tearDown() {
        if (tailer != null) {
            tailer.stop();
        }
    }

    protected void createFile(final File file, final long size) throws IOException {
        if (!file.getParentFile().exists()) {
            throw new IOException(("Cannot create file " + file) + " as the parent directory does not exist");
        }
        try (final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file))) {
            TestUtils.generateTestData(output, size);
        }
        RandomAccessFile reader = null;
        try {
            while (reader == null) {
                try {
                    reader = new RandomAccessFile(file.getPath(), "r");
                } catch (final FileNotFoundException ignore) {
                }
                try {
                    TestUtils.sleep(200L);
                } catch (final InterruptedException ignore) {

                }
            } 
        } finally {
            try {
                IOUtils.close(reader);
            } catch (final IOException ignored) {

            }
        }
    }

    private void write(final File file, final String... lines) throws Exception {
        try (final FileWriter writer = new FileWriter(file, true)) {
            for (final String line : lines) {
                writer.write(line + "\n");
            }
        }
    }

    private void writeString(final File file, final String... strings) throws Exception {
        try (final FileWriter writer = new FileWriter(file, true)) {
            for (final String string : strings) {
                writer.write(string);
            }
        }
    }

    private static class TestTailerListener extends TailerListenerAdapter {
        private final List<String> lines = Collections.synchronizedList(new ArrayList<String>());

        volatile Exception exception;

        volatile int notFound;

        volatile int rotated;

        volatile int initialized;

        volatile int reachedEndOfFile;

        @Override
        public void handle(final String line) {
            lines.add(line);
        }

        public List<String> getLines() {
            return lines;
        }

        public void clear() {
            lines.clear();
        }

        @Override
        public void handle(final Exception e) {
            exception = e;
        }

        @Override
        public void init(final Tailer tailer) {
            initialized++;
        }

        @Override
        public void fileNotFound() {
            notFound++;
        }

        @Override
        public void fileRotated() {
            rotated++;
        }

        @Override
        public void endOfFileReached() {
            reachedEndOfFile++;
        }
    }
}