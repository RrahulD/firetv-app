/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.cipher;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Provides generic utility methods for AES256 encoding content.
 */
public class AES256 {
    public static byte[] generateIV() {
        SecureRandom secureRandom = new SecureRandom();
        byte bytes[] = new byte[16];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public static SecretKey generateSecretKey(String password, String salt) {
        SecretKey secretKey = null;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes("UTF-8"), 1024, 256);
            SecretKey tmp = factory.generateSecret(spec);
            secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        } catch (InvalidKeySpecException e) {

            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return secretKey;
    }

    public static SecretKey generateRandomSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CipherOutputStream encrypt(SecretKey secretKey, byte[] iv, OutputStream os) {
        try {
            if (iv.length != 16)
                throw new InvalidKeyException("Invalid IV length. IV should be exactly 16 bytes long.");

            javax.crypto.Cipher localCipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
            localCipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            return new CipherOutputStream(os, localCipher);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static CipherOutputStream encrypt(String password, String salt, byte[] iv, OutputStream os) {
        return encrypt(generateSecretKey(password, salt), iv, os);
    }

    public static CipherInputStream decrypt(SecretKey secretKey, byte[] iv, InputStream is) {
        try {
            if (iv.length != 16)
                throw new InvalidKeyException("Invalid IV length. IV should be exactly 16 bytes long.");

            javax.crypto.Cipher localCipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
            localCipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            return new CipherInputStream(is, localCipher);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static CipherInputStream decrypt(String password, String salt, byte[] iv, InputStream is) {
        return decrypt(generateSecretKey(password, salt), iv, is);
    }

    // String versions
    public static byte[] encrypt(SecretKey secretKey, byte[] iv, byte[] input) {
        try {
            if (iv.length != 16)
                throw new InvalidKeyException("Invalid IV length. IV should be exactly 16 bytes long.");

            javax.crypto.Cipher localCipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
            localCipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

            return localCipher.doFinal(input);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt(SecretKey secretKey, byte[] iv, byte[] input) {
        try {
            if (iv.length != 16)
                throw new InvalidKeyException("Invalid IV length. IV should be exactly 16 bytes long.");

            javax.crypto.Cipher localCipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
            localCipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            return localCipher.doFinal(input);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] encrypt(String password, String salt, byte[] iv, byte[] input) {
        return encrypt(generateSecretKey(password, salt), iv, input);
    }

    public static byte[] decrypt(String password, String salt, byte[] iv, byte[] input) {
        return decrypt(generateSecretKey(password, salt), iv, input);
    }
}
