package org.apache.commons.io.monitor;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.CanReadFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.junit.jupiter.api.Assertions;
import org.powerapi.jjoules.junit5.EnergyTest;
public class FileAlterationObserverTestCase extends AbstractMonitorTestCase {
    public FileAlterationObserverTestCase() {
        listener = new CollectionFileListener(true);
    }

    @EnergyTest
    public void testAddRemoveListeners() {
        final FileAlterationObserver observer = new FileAlterationObserver("/foo");
        observer.addListener(null);
        Assertions.assertFalse(observer.getListeners().iterator().hasNext(), "Listeners[1]");
        observer.removeListener(null);
        Assertions.assertFalse(observer.getListeners().iterator().hasNext(), "Listeners[2]");
        final FileAlterationListenerAdaptor listener = new FileAlterationListenerAdaptor();
        observer.addListener(listener);
        final Iterator<FileAlterationListener> it = observer.getListeners().iterator();
        Assertions.assertTrue(it.hasNext(), "Listeners[3]");
        Assertions.assertEquals(listener, it.next(), "Added");
        Assertions.assertFalse(it.hasNext(), "Listeners[4]");
        observer.removeListener(listener);
        Assertions.assertFalse(observer.getListeners().iterator().hasNext(), "Listeners[5]");
    }

    @EnergyTest
    public void testToString() {
        final File file = new File("/foo");
        FileAlterationObserver observer = new FileAlterationObserver(file);
        Assertions.assertEquals(("FileAlterationObserver[file='" + file.getPath()) + "', listeners=0]", observer.toString());
        observer = new FileAlterationObserver(file, CanReadFileFilter.CAN_READ);
        Assertions.assertEquals(("FileAlterationObserver[file='" + file.getPath()) + "', CanReadFileFilter, listeners=0]", observer.toString());
        Assertions.assertEquals(file, observer.getDirectory());
    }

    @EnergyTest
    public void testDirectory() throws Exception {
        checkAndNotify();
        checkCollectionsEmpty("A");
        final File testDirA = new File(testDir, "test-dir-A");
        final File testDirB = new File(testDir, "test-dir-B");
        final File testDirC = new File(testDir, "test-dir-C");
        testDirA.mkdir();
        testDirB.mkdir();
        testDirC.mkdir();
        final File testDirAFile1 = touch(new File(testDirA, "A-file1.java"));
        final File testDirAFile2 = touch(new File(testDirA, "A-file2.txt"));
        final File testDirAFile3 = touch(new File(testDirA, "A-file3.java"));
        File testDirAFile4 = touch(new File(testDirA, "A-file4.java"));
        final File testDirBFile1 = touch(new File(testDirB, "B-file1.java"));
        checkAndNotify();
        checkCollectionSizes("B", 3, 0, 0, 4, 0, 0);
        Assertions.assertTrue(listener.getCreatedDirectories().contains(testDirA), "B testDirA");
        Assertions.assertTrue(listener.getCreatedDirectories().contains(testDirB), "B testDirB");
        Assertions.assertTrue(listener.getCreatedDirectories().contains(testDirC), "B testDirC");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile1), "B testDirAFile1");
        Assertions.assertFalse(listener.getCreatedFiles().contains(testDirAFile2), "B testDirAFile2");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile3), "B testDirAFile3");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile4), "B testDirAFile4");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirBFile1), "B testDirBFile1");
        checkAndNotify();
        checkCollectionsEmpty("C");
        testDirAFile4 = touch(testDirAFile4);
        FileUtils.deleteDirectory(testDirB);
        checkAndNotify();
        checkCollectionSizes("D", 0, 0, 1, 0, 1, 1);
        Assertions.assertTrue(listener.getDeletedDirectories().contains(testDirB), "D testDirB");
        Assertions.assertTrue(listener.getChangedFiles().contains(testDirAFile4), "D testDirAFile4");
        Assertions.assertTrue(listener.getDeletedFiles().contains(testDirBFile1), "D testDirBFile1");
        FileUtils.deleteDirectory(testDir);
        checkAndNotify();
        checkCollectionSizes("E", 0, 0, 2, 0, 0, 3);
        Assertions.assertTrue(listener.getDeletedDirectories().contains(testDirA), "E testDirA");
        Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile1), "E testDirAFile1");
        Assertions.assertFalse(listener.getDeletedFiles().contains(testDirAFile2), "E testDirAFile2");
        Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile3), "E testDirAFile3");
        Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile4), "E testDirAFile4");
        testDir.mkdir();
        checkAndNotify();
        checkCollectionsEmpty("F");
        checkAndNotify();
        checkCollectionsEmpty("G");
    }

    @EnergyTest
    public void testFileCreate() throws IOException {
        checkAndNotify();
        checkCollectionsEmpty("A");
        File testDirA = new File(testDir, "test-dir-A");
        testDirA.mkdir();
        testDir = touch(testDir);
        testDirA = touch(testDirA);
        File testDirAFile1 = new File(testDirA, "A-file1.java");
        final File testDirAFile2 = touch(new File(testDirA, "A-file2.java"));
        File testDirAFile3 = new File(testDirA, "A-file3.java");
        final File testDirAFile4 = touch(new File(testDirA, "A-file4.java"));
        File testDirAFile5 = new File(testDirA, "A-file5.java");
        checkAndNotify();
        checkCollectionSizes("B", 1, 0, 0, 2, 0, 0);
        Assertions.assertFalse(listener.getCreatedFiles().contains(testDirAFile1), "B testDirAFile1");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile2), "B testDirAFile2");
        Assertions.assertFalse(listener.getCreatedFiles().contains(testDirAFile3), "B testDirAFile3");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile4), "B testDirAFile4");
        Assertions.assertFalse(listener.getCreatedFiles().contains(testDirAFile5), "B testDirAFile5");
        Assertions.assertFalse(testDirAFile1.exists(), "B testDirAFile1 exists");
        Assertions.assertTrue(testDirAFile2.exists(), "B testDirAFile2 exists");
        Assertions.assertFalse(testDirAFile3.exists(), "B testDirAFile3 exists");
        Assertions.assertTrue(testDirAFile4.exists(), "B testDirAFile4 exists");
        Assertions.assertFalse(testDirAFile5.exists(), "B testDirAFile5 exists");
        checkAndNotify();
        checkCollectionsEmpty("C");
        testDirAFile1 = touch(testDirAFile1);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("D", 0, 1, 0, 1, 0, 0);
        Assertions.assertTrue(testDirAFile1.exists(), "D testDirAFile1 exists");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile1), "D testDirAFile1");
        testDirAFile3 = touch(testDirAFile3);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("E", 0, 1, 0, 1, 0, 0);
        Assertions.assertTrue(testDirAFile3.exists(), "E testDirAFile3 exists");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile3), "E testDirAFile3");
        testDirAFile5 = touch(testDirAFile5);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("F", 0, 1, 0, 1, 0, 0);
        Assertions.assertTrue(testDirAFile5.exists(), "F testDirAFile5 exists");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile5), "F testDirAFile5");
    }

    @EnergyTest
    public void testFileUpdate() throws IOException {
        checkAndNotify();
        checkCollectionsEmpty("A");
        File testDirA = new File(testDir, "test-dir-A");
        testDirA.mkdir();
        testDir = touch(testDir);
        testDirA = touch(testDirA);
        File testDirAFile1 = touch(new File(testDirA, "A-file1.java"));
        final File testDirAFile2 = touch(new File(testDirA, "A-file2.java"));
        File testDirAFile3 = touch(new File(testDirA, "A-file3.java"));
        final File testDirAFile4 = touch(new File(testDirA, "A-file4.java"));
        File testDirAFile5 = touch(new File(testDirA, "A-file5.java"));
        checkAndNotify();
        checkCollectionSizes("B", 1, 0, 0, 5, 0, 0);
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile1), "B testDirAFile1");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile2), "B testDirAFile2");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile3), "B testDirAFile3");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile4), "B testDirAFile4");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile5), "B testDirAFile5");
        Assertions.assertTrue(testDirAFile1.exists(), "B testDirAFile1 exists");
        Assertions.assertTrue(testDirAFile2.exists(), "B testDirAFile2 exists");
        Assertions.assertTrue(testDirAFile3.exists(), "B testDirAFile3 exists");
        Assertions.assertTrue(testDirAFile4.exists(), "B testDirAFile4 exists");
        Assertions.assertTrue(testDirAFile5.exists(), "B testDirAFile5 exists");
        checkAndNotify();
        checkCollectionsEmpty("C");
        testDirAFile1 = touch(testDirAFile1);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("D", 0, 1, 0, 0, 1, 0);
        Assertions.assertTrue(listener.getChangedFiles().contains(testDirAFile1), "D testDirAFile1");
        testDirAFile3 = touch(testDirAFile3);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("E", 0, 1, 0, 0, 1, 0);
        Assertions.assertTrue(listener.getChangedFiles().contains(testDirAFile3), "E testDirAFile3");
        testDirAFile5 = touch(testDirAFile5);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("F", 0, 1, 0, 0, 1, 0);
        Assertions.assertTrue(listener.getChangedFiles().contains(testDirAFile5), "F testDirAFile5");
    }

    @EnergyTest
    public void testFileDelete() throws IOException {
        checkAndNotify();
        checkCollectionsEmpty("A");
        File testDirA = new File(testDir, "test-dir-A");
        testDirA.mkdir();
        testDir = touch(testDir);
        testDirA = touch(testDirA);
        final File testDirAFile1 = touch(new File(testDirA, "A-file1.java"));
        final File testDirAFile2 = touch(new File(testDirA, "A-file2.java"));
        final File testDirAFile3 = touch(new File(testDirA, "A-file3.java"));
        final File testDirAFile4 = touch(new File(testDirA, "A-file4.java"));
        final File testDirAFile5 = touch(new File(testDirA, "A-file5.java"));
        Assertions.assertTrue(testDirAFile1.exists(), "B testDirAFile1 exists");
        Assertions.assertTrue(testDirAFile2.exists(), "B testDirAFile2 exists");
        Assertions.assertTrue(testDirAFile3.exists(), "B testDirAFile3 exists");
        Assertions.assertTrue(testDirAFile4.exists(), "B testDirAFile4 exists");
        Assertions.assertTrue(testDirAFile5.exists(), "B testDirAFile5 exists");
        checkAndNotify();
        checkCollectionSizes("B", 1, 0, 0, 5, 0, 0);
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile1), "B testDirAFile1");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile2), "B testDirAFile2");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile3), "B testDirAFile3");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile4), "B testDirAFile4");
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile5), "B testDirAFile5");
        checkAndNotify();
        checkCollectionsEmpty("C");
        FileUtils.deleteQuietly(testDirAFile1);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("D", 0, 1, 0, 0, 0, 1);
        Assertions.assertFalse(testDirAFile1.exists(), "D testDirAFile1 exists");
        Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile1), "D testDirAFile1");
        FileUtils.deleteQuietly(testDirAFile3);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("E", 0, 1, 0, 0, 0, 1);
        Assertions.assertFalse(testDirAFile3.exists(), "E testDirAFile3 exists");
        Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile3), "E testDirAFile3");
        FileUtils.deleteQuietly(testDirAFile5);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("F", 0, 1, 0, 0, 0, 1);
        Assertions.assertFalse(testDirAFile5.exists(), "F testDirAFile5 exists");
        Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile5), "F testDirAFile5");
    }

    @EnergyTest
    public void testObserveSingleFile() throws IOException {
        final File testDirA = new File(testDir, "test-dir-A");
        File testDirAFile1 = new File(testDirA, "A-file1.java");
        testDirA.mkdir();
        final FileFilter nameFilter = FileFilterUtils.nameFileFilter(testDirAFile1.getName());
        createObserver(testDirA, nameFilter);
        checkAndNotify();
        checkCollectionsEmpty("A");
        Assertions.assertFalse(testDirAFile1.exists(), "A testDirAFile1 exists");
        testDirAFile1 = touch(testDirAFile1);
        File testDirAFile2 = touch(new File(testDirA, "A-file2.txt"));
        File testDirAFile3 = touch(new File(testDirA, "A-file3.java"));
        Assertions.assertTrue(testDirAFile1.exists(), "B testDirAFile1 exists");
        Assertions.assertTrue(testDirAFile2.exists(), "B testDirAFile2 exists");
        Assertions.assertTrue(testDirAFile3.exists(), "B testDirAFile3 exists");
        checkAndNotify();
        checkCollectionSizes("C", 0, 0, 0, 1, 0, 0);
        Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile1), "C created");
        Assertions.assertFalse(listener.getCreatedFiles().contains(testDirAFile2), "C created");
        Assertions.assertFalse(listener.getCreatedFiles().contains(testDirAFile3), "C created");
        testDirAFile1 = touch(testDirAFile1);
        testDirAFile2 = touch(testDirAFile2);
        testDirAFile3 = touch(testDirAFile3);
        checkAndNotify();
        checkCollectionSizes("D", 0, 0, 0, 0, 1, 0);
        Assertions.assertTrue(listener.getChangedFiles().contains(testDirAFile1), "D changed");
        Assertions.assertFalse(listener.getChangedFiles().contains(testDirAFile2), "D changed");
        Assertions.assertFalse(listener.getChangedFiles().contains(testDirAFile3), "D changed");
        FileUtils.deleteQuietly(testDirAFile1);
        FileUtils.deleteQuietly(testDirAFile2);
        FileUtils.deleteQuietly(testDirAFile3);
        Assertions.assertFalse(testDirAFile1.exists(), "E testDirAFile1 exists");
        Assertions.assertFalse(testDirAFile2.exists(), "E testDirAFile2 exists");
        Assertions.assertFalse(testDirAFile3.exists(), "E testDirAFile3 exists");
        checkAndNotify();
        checkCollectionSizes("E", 0, 0, 0, 0, 0, 1);
        Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile1), "E deleted");
        Assertions.assertFalse(listener.getDeletedFiles().contains(testDirAFile2), "E deleted");
        Assertions.assertFalse(listener.getDeletedFiles().contains(testDirAFile3), "E deleted");
    }

    protected void checkAndNotify() {
        observer.checkAndNotify();
    }
}