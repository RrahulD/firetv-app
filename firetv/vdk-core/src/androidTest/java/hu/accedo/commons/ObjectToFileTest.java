/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import hu.accedo.commons.cache.ObjectToFile;
import hu.accedo.commons.tools.IOUtilsLite;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static hu.accedo.commons.Shared.getContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ObjectToFileTest {
    private static final String TEST_STRING = "Hello, this is a test file.";
    private static final String TEST_FILE = "TEST_FILE_CRYPTO";

    private static final String PASSWORD_1 = "SuperSecurePassword1234";
    private static final String PASSWORD_2 = "123456";

    @Test
    public void testReadWriteGoodPassword() {
        ObjectToFile.write(getContext(), TEST_STRING, TEST_FILE, PASSWORD_1);

        assertEquals(TEST_STRING, ObjectToFile.read(getContext(), TEST_FILE, PASSWORD_1));
    }

    @Test
    public void testReadInvalidPassword() {
        ObjectToFile.write(getContext(), TEST_STRING, TEST_FILE, PASSWORD_1);
        assertNull(ObjectToFile.read(getContext(), TEST_FILE, PASSWORD_2));
    }

    @Test
    public void testNonEncrypted() {
        ObjectToFile.write(getContext(), TEST_STRING, TEST_FILE);

        try {
            String result = IOUtilsLite.toString(getContext().openFileInput(TEST_FILE));

            assertTrue(result.contains(TEST_STRING));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testEncrypted() {
        ObjectToFile.write(getContext(), TEST_STRING, TEST_FILE, PASSWORD_1);

        try {
            String result = IOUtilsLite.toString(getContext().openFileInput(TEST_FILE));

            assertFalse(result.contains(TEST_STRING));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testNoPassword() {
        ObjectToFile.write(getContext(), TEST_STRING, TEST_FILE, PASSWORD_1);

        assertNull(ObjectToFile.read(getContext(), TEST_FILE));
    }

}
