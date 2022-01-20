/*
 * Copyright (c) 2018 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import hu.accedo.commons.tools.Hash;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class HashTest {
    /**
     * new BigInteger(1, digest.digest()).toString(16); actually trims off trailing zeroes,
     * sometimes producing shorter than 32 bit hashes.
     *
     * "27" is a good test-candidate, as it's hash starts with "0".
     */
    @Test
    public void testMd5Length() {
        for (int i = 0; i < 100; i++) {
            assertEquals("" + i + " " + 32, "" + i + " " + Hash.md5Hash("" + i).length());
        }
    }

    /**
     * Assures that despite trailing zeroes, the fix doesn't change anything (like upper, lowercase and such)
     */
    @Test
    public void testMd5Change() throws NoSuchAlgorithmException {
        for (int i = 0; i < 100; i++) {
            String md5Valid = Hash.md5Hash("" + i);
            String md5Broken = md5Broken("" + i);

            if (md5Broken.length() < 32) {
                //There could be more than one trailing zeros, but below 100 its only for 27, and that only has one. So this is good enough for us now.
                md5Broken = "0" + md5Broken;
            }

            assertEquals("" + i + " " + md5Broken, "" + i + " " + md5Valid);
        }
    }

    private static String md5Broken(String toHash) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(toHash.getBytes(), 0, toHash.length());
        return new BigInteger(1, digest.digest()).toString(16);
    }
}
