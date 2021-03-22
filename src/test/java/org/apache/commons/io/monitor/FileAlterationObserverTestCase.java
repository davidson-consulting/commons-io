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
package org.apache.commons.io.monitor;
import java.io.IOException;

import org.powerapi.jjoules.junit5.EnergyTest;
/**
 * {@link FileAlterationObserver} Test Case.
 */
public class FileAlterationObserverTestCase extends org.apache.commons.io.monitor.AbstractMonitorTestCase {

    @EnergyTest
    public void aaawarmup0() {
        System.out.println("aaawarmup");
    }
    
    @EnergyTest
    public void aaawarmup1() {
        System.out.println("aaawarmup");
    }
    
    @EnergyTest
    public void aaawarmup2() {
        System.out.println("aaawarmup");
    }
    
    @EnergyTest
    public void aaawarmup3() {
        System.out.println("aaawarmup");
    }

    @EnergyTest
    public void aaawarmup4() {
        System.out.println("aaawarmup");
    }

    @EnergyTest
    public void aaawarmup5() {
        System.out.println("aaawarmup");
    }

    @EnergyTest
    public void aaawarmup6() {
        System.out.println("aaawarmup");
    }

    @EnergyTest
    public void aaawarmup7() {
    }

    @EnergyTest
    public void aaawarmup8() {
        System.out.println("aaawarmup");
    }

    @EnergyTest
    public void aaawarmup9() {
        System.out.println("aaawarmup");
    }

    /**
     * Construct a new test case.
     */
    public FileAlterationObserverTestCase() {
        listener = new org.apache.commons.io.monitor.CollectionFileListener(true);
    }

    /**
     * Test add/remove listeners.
     */
    @EnergyTest
    public void testAddRemoveListeners() {
        final org.apache.commons.io.monitor.FileAlterationObserver observer = new org.apache.commons.io.monitor.FileAlterationObserver("/foo");
        // Null Listener
        observer.addListener(null);
        org.junit.jupiter.api.Assertions.assertFalse(observer.getListeners().iterator().hasNext(), "Listeners[1]");
        observer.removeListener(null);
        org.junit.jupiter.api.Assertions.assertFalse(observer.getListeners().iterator().hasNext(), "Listeners[2]");
        // Add Listener
        final org.apache.commons.io.monitor.FileAlterationListenerAdaptor listener = new org.apache.commons.io.monitor.FileAlterationListenerAdaptor();
        observer.addListener(listener);
        final java.util.Iterator<org.apache.commons.io.monitor.FileAlterationListener> it = observer.getListeners().iterator();
        org.junit.jupiter.api.Assertions.assertTrue(it.hasNext(), "Listeners[3]");
        org.junit.jupiter.api.Assertions.assertEquals(listener, it.next(), "Added");
        org.junit.jupiter.api.Assertions.assertFalse(it.hasNext(), "Listeners[4]");
        // Remove Listener
        observer.removeListener(listener);
        org.junit.jupiter.api.Assertions.assertFalse(observer.getListeners().iterator().hasNext(), "Listeners[5]");
    }

    /**
     * Test toString().
     */
    @EnergyTest
    public void testToString() {
        final java.io.File file = new java.io.File("/foo");
        org.apache.commons.io.monitor.FileAlterationObserver observer = new org.apache.commons.io.monitor.FileAlterationObserver(file);
        org.junit.jupiter.api.Assertions.assertEquals(("FileAlterationObserver[file='" + file.getPath()) + "', listeners=0]", observer.toString());
        observer = new org.apache.commons.io.monitor.FileAlterationObserver(file, org.apache.commons.io.filefilter.CanReadFileFilter.CAN_READ);
        org.junit.jupiter.api.Assertions.assertEquals(("FileAlterationObserver[file='" + file.getPath()) + "', CanReadFileFilter, listeners=0]", observer.toString());
        org.junit.jupiter.api.Assertions.assertEquals(file, observer.getDirectory());
    }

    /**
     * Test checkAndNotify() method
     *
     * @throws Exception
     * 		
     */
    @EnergyTest
    public void testDirectory() throws java.lang.Exception {
        checkAndNotify();
        checkCollectionsEmpty("A");
        final java.io.File testDirA = new java.io.File(testDir, "test-dir-A");
        final java.io.File testDirB = new java.io.File(testDir, "test-dir-B");
        final java.io.File testDirC = new java.io.File(testDir, "test-dir-C");
        testDirA.mkdir();
        testDirB.mkdir();
        testDirC.mkdir();
        final java.io.File testDirAFile1 = touch(new java.io.File(testDirA, "A-file1.java"));
        final java.io.File testDirAFile2 = touch(new java.io.File(testDirA, "A-file2.txt"));// filter should ignore this

        final java.io.File testDirAFile3 = touch(new java.io.File(testDirA, "A-file3.java"));
        java.io.File testDirAFile4 = touch(new java.io.File(testDirA, "A-file4.java"));
        final java.io.File testDirBFile1 = touch(new java.io.File(testDirB, "B-file1.java"));
        checkAndNotify();
        checkCollectionSizes("B", 3, 0, 0, 4, 0, 0);
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedDirectories().contains(testDirA), "B testDirA");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedDirectories().contains(testDirB), "B testDirB");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedDirectories().contains(testDirC), "B testDirC");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile1), "B testDirAFile1");
        org.junit.jupiter.api.Assertions.assertFalse(listener.getCreatedFiles().contains(testDirAFile2), "B testDirAFile2");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile3), "B testDirAFile3");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile4), "B testDirAFile4");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirBFile1), "B testDirBFile1");
        checkAndNotify();
        checkCollectionsEmpty("C");
        testDirAFile4 = touch(testDirAFile4);
        org.apache.commons.io.FileUtils.deleteDirectory(testDirB);
        checkAndNotify();
        checkCollectionSizes("D", 0, 0, 1, 0, 1, 1);
        org.junit.jupiter.api.Assertions.assertTrue(listener.getDeletedDirectories().contains(testDirB), "D testDirB");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getChangedFiles().contains(testDirAFile4), "D testDirAFile4");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getDeletedFiles().contains(testDirBFile1), "D testDirBFile1");
        org.apache.commons.io.FileUtils.deleteDirectory(testDir);
        checkAndNotify();
        checkCollectionSizes("E", 0, 0, 2, 0, 0, 3);
        org.junit.jupiter.api.Assertions.assertTrue(listener.getDeletedDirectories().contains(testDirA), "E testDirA");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile1), "E testDirAFile1");
        org.junit.jupiter.api.Assertions.assertFalse(listener.getDeletedFiles().contains(testDirAFile2), "E testDirAFile2");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile3), "E testDirAFile3");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile4), "E testDirAFile4");
        testDir.mkdir();
        checkAndNotify();
        checkCollectionsEmpty("F");
        checkAndNotify();
        checkCollectionsEmpty("G");
    }

    /**
     * Test checkAndNotify() creating
     *
     * @throws IOException
     * 		if an I/O error occurs.
     */
    @EnergyTest
    public void testFileCreate() throws java.io.IOException {
        checkAndNotify();
        checkCollectionsEmpty("A");
        java.io.File testDirA = new java.io.File(testDir, "test-dir-A");
        testDirA.mkdir();
        testDir = touch(testDir);
        testDirA = touch(testDirA);
        java.io.File testDirAFile1 = new java.io.File(testDirA, "A-file1.java");
        final java.io.File testDirAFile2 = touch(new java.io.File(testDirA, "A-file2.java"));
        java.io.File testDirAFile3 = new java.io.File(testDirA, "A-file3.java");
        final java.io.File testDirAFile4 = touch(new java.io.File(testDirA, "A-file4.java"));
        java.io.File testDirAFile5 = new java.io.File(testDirA, "A-file5.java");
        checkAndNotify();
        checkCollectionSizes("B", 1, 0, 0, 2, 0, 0);
        org.junit.jupiter.api.Assertions.assertFalse(listener.getCreatedFiles().contains(testDirAFile1), "B testDirAFile1");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile2), "B testDirAFile2");
        org.junit.jupiter.api.Assertions.assertFalse(listener.getCreatedFiles().contains(testDirAFile3), "B testDirAFile3");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile4), "B testDirAFile4");
        org.junit.jupiter.api.Assertions.assertFalse(listener.getCreatedFiles().contains(testDirAFile5), "B testDirAFile5");
        org.junit.jupiter.api.Assertions.assertFalse(testDirAFile1.exists(), "B testDirAFile1 exists");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile2.exists(), "B testDirAFile2 exists");
        org.junit.jupiter.api.Assertions.assertFalse(testDirAFile3.exists(), "B testDirAFile3 exists");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile4.exists(), "B testDirAFile4 exists");
        org.junit.jupiter.api.Assertions.assertFalse(testDirAFile5.exists(), "B testDirAFile5 exists");
        checkAndNotify();
        checkCollectionsEmpty("C");
        // Create file with name < first entry
        testDirAFile1 = touch(testDirAFile1);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("D", 0, 1, 0, 1, 0, 0);
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile1.exists(), "D testDirAFile1 exists");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile1), "D testDirAFile1");
        // Create file with name between 2 entries
        testDirAFile3 = touch(testDirAFile3);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("E", 0, 1, 0, 1, 0, 0);
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile3.exists(), "E testDirAFile3 exists");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile3), "E testDirAFile3");
        // Create file with name > last entry
        testDirAFile5 = touch(testDirAFile5);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("F", 0, 1, 0, 1, 0, 0);
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile5.exists(), "F testDirAFile5 exists");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile5), "F testDirAFile5");
    }

    /**
     * Test checkAndNotify() creating
     *
     * @throws IOException
     * 		if an I/O error occurs.
     */
    @EnergyTest
    public void testFileUpdate() throws java.io.IOException {
        checkAndNotify();
        checkCollectionsEmpty("A");
        java.io.File testDirA = new java.io.File(testDir, "test-dir-A");
        testDirA.mkdir();
        testDir = touch(testDir);
        testDirA = touch(testDirA);
        java.io.File testDirAFile1 = touch(new java.io.File(testDirA, "A-file1.java"));
        final java.io.File testDirAFile2 = touch(new java.io.File(testDirA, "A-file2.java"));
        java.io.File testDirAFile3 = touch(new java.io.File(testDirA, "A-file3.java"));
        final java.io.File testDirAFile4 = touch(new java.io.File(testDirA, "A-file4.java"));
        java.io.File testDirAFile5 = touch(new java.io.File(testDirA, "A-file5.java"));
        checkAndNotify();
        checkCollectionSizes("B", 1, 0, 0, 5, 0, 0);
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile1), "B testDirAFile1");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile2), "B testDirAFile2");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile3), "B testDirAFile3");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile4), "B testDirAFile4");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile5), "B testDirAFile5");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile1.exists(), "B testDirAFile1 exists");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile2.exists(), "B testDirAFile2 exists");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile3.exists(), "B testDirAFile3 exists");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile4.exists(), "B testDirAFile4 exists");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile5.exists(), "B testDirAFile5 exists");
        checkAndNotify();
        checkCollectionsEmpty("C");
        // Update first entry
        testDirAFile1 = touch(testDirAFile1);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("D", 0, 1, 0, 0, 1, 0);
        org.junit.jupiter.api.Assertions.assertTrue(listener.getChangedFiles().contains(testDirAFile1), "D testDirAFile1");
        // Update file with name between 2 entries
        testDirAFile3 = touch(testDirAFile3);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("E", 0, 1, 0, 0, 1, 0);
        org.junit.jupiter.api.Assertions.assertTrue(listener.getChangedFiles().contains(testDirAFile3), "E testDirAFile3");
        // Update last entry
        testDirAFile5 = touch(testDirAFile5);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("F", 0, 1, 0, 0, 1, 0);
        org.junit.jupiter.api.Assertions.assertTrue(listener.getChangedFiles().contains(testDirAFile5), "F testDirAFile5");
    }

    /**
     * Test checkAndNotify() deleting
     *
     * @throws IOException
     * 		if an I/O error occurs.
     */
    @EnergyTest
    public void testFileDelete() throws java.io.IOException {
        checkAndNotify();
        checkCollectionsEmpty("A");
        java.io.File testDirA = new java.io.File(testDir, "test-dir-A");
        testDirA.mkdir();
        testDir = touch(testDir);
        testDirA = touch(testDirA);
        final java.io.File testDirAFile1 = touch(new java.io.File(testDirA, "A-file1.java"));
        final java.io.File testDirAFile2 = touch(new java.io.File(testDirA, "A-file2.java"));
        final java.io.File testDirAFile3 = touch(new java.io.File(testDirA, "A-file3.java"));
        final java.io.File testDirAFile4 = touch(new java.io.File(testDirA, "A-file4.java"));
        final java.io.File testDirAFile5 = touch(new java.io.File(testDirA, "A-file5.java"));
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile1.exists(), "B testDirAFile1 exists");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile2.exists(), "B testDirAFile2 exists");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile3.exists(), "B testDirAFile3 exists");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile4.exists(), "B testDirAFile4 exists");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile5.exists(), "B testDirAFile5 exists");
        checkAndNotify();
        checkCollectionSizes("B", 1, 0, 0, 5, 0, 0);
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile1), "B testDirAFile1");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile2), "B testDirAFile2");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile3), "B testDirAFile3");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile4), "B testDirAFile4");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile5), "B testDirAFile5");
        checkAndNotify();
        checkCollectionsEmpty("C");
        // Delete first entry
        org.apache.commons.io.FileUtils.deleteQuietly(testDirAFile1);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("D", 0, 1, 0, 0, 0, 1);
        org.junit.jupiter.api.Assertions.assertFalse(testDirAFile1.exists(), "D testDirAFile1 exists");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile1), "D testDirAFile1");
        // Delete file with name between 2 entries
        org.apache.commons.io.FileUtils.deleteQuietly(testDirAFile3);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("E", 0, 1, 0, 0, 0, 1);
        org.junit.jupiter.api.Assertions.assertFalse(testDirAFile3.exists(), "E testDirAFile3 exists");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile3), "E testDirAFile3");
        // Delete last entry
        org.apache.commons.io.FileUtils.deleteQuietly(testDirAFile5);
        testDirA = touch(testDirA);
        checkAndNotify();
        checkCollectionSizes("F", 0, 1, 0, 0, 0, 1);
        org.junit.jupiter.api.Assertions.assertFalse(testDirAFile5.exists(), "F testDirAFile5 exists");
        org.junit.jupiter.api.Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile5), "F testDirAFile5");
    }

    /**
     * Test checkAndNotify() method
     *
     * @throws IOException
     * 		if an I/O error occurs.
     */
    @EnergyTest
    public void testObserveSingleFile() throws java.io.IOException {
        final java.io.File testDirA = new java.io.File(testDir, "test-dir-A");
        java.io.File testDirAFile1 = new java.io.File(testDirA, "A-file1.java");
        testDirA.mkdir();
        final java.io.FileFilter nameFilter = org.apache.commons.io.filefilter.FileFilterUtils.nameFileFilter(testDirAFile1.getName());
        createObserver(testDirA, nameFilter);
        checkAndNotify();
        checkCollectionsEmpty("A");
        org.junit.jupiter.api.Assertions.assertFalse(testDirAFile1.exists(), "A testDirAFile1 exists");
        // Create
        testDirAFile1 = touch(testDirAFile1);
        java.io.File testDirAFile2 = touch(new java.io.File(testDirA, "A-file2.txt"));/* filter should ignore */

        java.io.File testDirAFile3 = touch(new java.io.File(testDirA, "A-file3.java"));/* filter should ignore */

        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile1.exists(), "B testDirAFile1 exists");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile2.exists(), "B testDirAFile2 exists");
        org.junit.jupiter.api.Assertions.assertTrue(testDirAFile3.exists(), "B testDirAFile3 exists");
        checkAndNotify();
        checkCollectionSizes("C", 0, 0, 0, 1, 0, 0);
        org.junit.jupiter.api.Assertions.assertTrue(listener.getCreatedFiles().contains(testDirAFile1), "C created");
        org.junit.jupiter.api.Assertions.assertFalse(listener.getCreatedFiles().contains(testDirAFile2), "C created");
        org.junit.jupiter.api.Assertions.assertFalse(listener.getCreatedFiles().contains(testDirAFile3), "C created");
        // Modify
        testDirAFile1 = touch(testDirAFile1);
        testDirAFile2 = touch(testDirAFile2);
        testDirAFile3 = touch(testDirAFile3);
        checkAndNotify();
        checkCollectionSizes("D", 0, 0, 0, 0, 1, 0);
        org.junit.jupiter.api.Assertions.assertTrue(listener.getChangedFiles().contains(testDirAFile1), "D changed");
        org.junit.jupiter.api.Assertions.assertFalse(listener.getChangedFiles().contains(testDirAFile2), "D changed");
        org.junit.jupiter.api.Assertions.assertFalse(listener.getChangedFiles().contains(testDirAFile3), "D changed");
        // Delete
        org.apache.commons.io.FileUtils.deleteQuietly(testDirAFile1);
        org.apache.commons.io.FileUtils.deleteQuietly(testDirAFile2);
        org.apache.commons.io.FileUtils.deleteQuietly(testDirAFile3);
        org.junit.jupiter.api.Assertions.assertFalse(testDirAFile1.exists(), "E testDirAFile1 exists");
        org.junit.jupiter.api.Assertions.assertFalse(testDirAFile2.exists(), "E testDirAFile2 exists");
        org.junit.jupiter.api.Assertions.assertFalse(testDirAFile3.exists(), "E testDirAFile3 exists");
        checkAndNotify();
        checkCollectionSizes("E", 0, 0, 0, 0, 0, 1);
        org.junit.jupiter.api.Assertions.assertTrue(listener.getDeletedFiles().contains(testDirAFile1), "E deleted");
        org.junit.jupiter.api.Assertions.assertFalse(listener.getDeletedFiles().contains(testDirAFile2), "E deleted");
        org.junit.jupiter.api.Assertions.assertFalse(listener.getDeletedFiles().contains(testDirAFile3), "E deleted");
    }

    /**
     * Call {@link FileAlterationObserver#checkAndNotify()}.
     */
    protected void checkAndNotify() {
        observer.checkAndNotify();
    }
}