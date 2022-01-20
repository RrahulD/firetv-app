/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import hu.accedo.commons.logging.L;

public class Hash {
    public static String hmacSHA1(String input, String secret) throws InvalidKeyException {
        String hash = null;
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = secret.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(input.getBytes());

            // Convert raw bytes to Hex string
            hash = bytesToHexString(rawHmac);

        } catch (NoSuchAlgorithmException e) {
            L.w(e); // Should not happen on Android.
        }
        return hash;
    }

    public static String sha1Hash(String toHash) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(toHash.getBytes(), 0, toHash.length());
            hash = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            L.w(e); // Should not happen on Android.
        }
        return hash;
    }

    public static String md5Hash(String toHash) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(toHash.getBytes(), 0, toHash.length());
            hash = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            L.w(e); // Should not happen on Android.
        }
        return hash;
    }

    final private static char[] hexArray = "0123456789abcdef".toCharArray();

    public static String bytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
