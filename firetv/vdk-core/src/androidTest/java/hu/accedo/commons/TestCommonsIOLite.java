/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import hu.accedo.commons.tools.FileUtilsLite;
import hu.accedo.commons.tools.IOUtilsLite;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import static hu.accedo.commons.Shared.getContext;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class TestCommonsIOLite {
    private static final String TEST_STRING = "Hello, this is a test file.";
    private static final String TEST_FILE_NORMAL = "TEST_FILE1";
    private static final String TEST_FILE_LITE = "TEST_FILE2";

    @Test
    public void testReadFile() {
        try {
            // Create some files with the testString in them with both utils
            File testFileLite = prepareLiteFile();
            File testFileNormal = prepareNormalFile();

            // Read in all the combinations
            String liteReaderLiteString = FileUtilsLite.readFileToString(testFileLite);
            String liteReaderNormalString = FileUtilsLite.readFileToString(testFileNormal);
            String normalReaderLiteString = FileUtils.readFileToString(testFileLite);
            String normalReaderNormalString = FileUtils.readFileToString(testFileNormal);

            // Everyone should equal everyone
            assertEquals(liteReaderLiteString, liteReaderNormalString);
            assertEquals(liteReaderLiteString, normalReaderLiteString);
            assertEquals(liteReaderLiteString, normalReaderNormalString);
            assertEquals(liteReaderLiteString, normalReaderNormalString);

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testToByteArray() {
        try {
            // Create some files with the testString in them with both utils
            File testFileNormal = prepareNormalFile();

            InputStream is1 = new FileInputStream(testFileNormal);
            InputStream is2 = new FileInputStream(testFileNormal);

            byte[] bytesLite = IOUtilsLite.toByteArray(is1);
            byte[] bytesNormal = IOUtils.toByteArray(is2);

            is1.close();
            is2.close();

            assertArrayEquals(bytesLite, bytesNormal);

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testToString() {
        try {
            // Create some files with the testString in them with both utils
            File testFileNormal = prepareNormalFile();

            InputStream is1 = new FileInputStream(testFileNormal);
            InputStream is2 = new FileInputStream(testFileNormal);

            String stringLite = IOUtilsLite.toString(is1);
            String stringNormal = IOUtils.toString(is2);

            is1.close();
            is2.close();

            assertEquals(stringLite, stringNormal);

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testClosing() {
        try {
            // Create some files with the testString in them with both utils
            File testFileNormal = prepareNormalFile();

            // One way
            InputStream is = new FileInputStream(testFileNormal);

            byte[] bytesLite = IOUtilsLite.toByteArray(is);
            byte[] bytesNormal = IOUtils.toByteArray(is);

            assertEquals(0, bytesNormal.length);

            is.close();

            // Other way
            is = new FileInputStream(testFileNormal);

            bytesNormal = IOUtils.toByteArray(is);
            bytesLite = IOUtilsLite.toByteArray(is);

            assertEquals(0, bytesLite.length);

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    private File prepareNormalFile() throws IOException {
        // Create new file
        File testFileNormal = new File(getContext().getFilesDir(), TEST_FILE_NORMAL);
        testFileNormal.delete();

        // Write with CommonsIO
        FileUtils.writeStringToFile(testFileNormal, TEST_STRING);

        return testFileNormal;
    }

    private File prepareLiteFile() throws IOException {
        // Create new file
        File testFileLite = new File(getContext().getFilesDir(), TEST_FILE_LITE);
        testFileLite.delete();

        // Write with Lite
        FileOutputStream fos = new FileOutputStream(testFileLite);
        IOUtilsLite.write(TEST_STRING, fos);
        fos.close();

        return testFileLite;
    }
}
