/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.cache;

import android.app.Activity;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import hu.accedo.commons.cipher.AES256;
import hu.accedo.commons.logging.L;
import hu.accedo.commons.tools.Hash;
import hu.accedo.commons.tools.IOUtilsLite;

/**
 * This util can be used to persistently store serializable objects in the device's internal private storage.
 */
public class SerializableToFile {

    /**
     * Serializes an object into a file, stored in the application's private file storage.
     *
     * @param context      Needed for opening the application's private file storage.
     * @param serializable The object to serialize.
     * @param filename     The filename to write into.
     * @return true, if writing has succeeded, false otherwise.
     */
    public static boolean write(Context context, Serializable serializable, String filename) {
        if (serializable == null) {
            return false;
        }

        ObjectOutputStream oos = null;
        FileOutputStream fos = null;

        try {
            fos = context.getApplicationContext().openFileOutput(filename, Activity.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(serializable);
            fos.getFD().sync();
        } catch (Exception e) {
            L.w(e);
            delete(context, filename);
            return false;
        } finally {
            IOUtilsLite.closeQuietly(oos);
            IOUtilsLite.closeQuietly(fos);
        }

        return true;
    }

    /**
     * Serializes an object into a file, stored in the application's private file storage.
     * The file will be AES256 encrypted, with the given password.
     *
     * @param context      Needed for opening the application's private file storage.
     * @param serializable The object to serialize.
     * @param filename     The filename to write into.
     * @param password     The password to encrypt with.
     * @return true, if writing has succeeded, false otherwise.
     */
    public static boolean write(Context context, Serializable serializable, String filename, String password) {
        if (serializable == null) {
            return false;
        }

        ObjectOutputStream oos = null;
        CipherOutputStream cos = null;
        FileOutputStream fos = null;

        try {
            byte[] initVector = AES256.generateIV();

            fos = context.getApplicationContext().openFileOutput(filename, Activity.MODE_PRIVATE);
            fos.write(initVector, 0, 16);

            cos = AES256.encrypt(password, Hash.md5Hash(context.getPackageName()), initVector, fos);
            oos = new ObjectOutputStream(cos);
            oos.writeObject(serializable);

            fos.getFD().sync();
        } catch (Exception e) {
            L.w(e);
            delete(context, filename);
            return false;
        } finally {
            IOUtilsLite.closeQuietly(oos);
            IOUtilsLite.closeQuietly(cos);
            IOUtilsLite.closeQuietly(fos);
        }

        return true;
    }


    /**
     * Deserializes and returns an object from a given file, stored in the application's private file storage.
     *
     * @param context  Needed for opening the application's private file storage.
     * @param filename The filename to read from.
     * @return The deserialized object, or null if unsuccessful.
     */
    public static <T extends Serializable> T read(Context context, String filename) {
        ObjectInputStream ois = null;
        FileInputStream fis = null;
        Object result = null;

        try {
            fis = context.getApplicationContext().openFileInput(filename);
            ois = new ObjectInputStream(fis);
            result = ois.readObject();
            return (T) result;
        } catch (FileNotFoundException e) {
            L.i("ObjectToFile: File not found: " + filename);
        } catch (Exception e) {
            L.w(e);
            delete(context, filename);
        } finally {
            IOUtilsLite.closeQuietly(ois);
            IOUtilsLite.closeQuietly(fis);
        }

        return null;
    }

    /**
     * Deserializes and returns an object from a given file, stored in the application's private file storage.
     * The file will be AES256 decrypted, with the given password.
     *
     * @param context  Needed for opening the application's private file storage.
     * @param filename The filename to read from.
     * @param password The password to decrypt with.
     * @return The deserialized object, or null if unsuccessful.
     */
    public static <T extends Serializable> T read(Context context, String filename, String password) {
        ObjectInputStream ois = null;
        FileInputStream fis = null;
        CipherInputStream cis = null;
        Object result = null;

        try {
            byte[] initVector = new byte[16];

            fis = context.getApplicationContext().openFileInput(filename);
            fis.read(initVector, 0, 16);
            cis = AES256.decrypt(password, Hash.md5Hash(context.getPackageName()), initVector, fis);
            ois = new ObjectInputStream(cis);

            result = ois.readObject();
            return (T) result;
        } catch (StreamCorruptedException e) {
            L.i("ObjectToFile: Wrong password provided for: " + filename);
        } catch (FileNotFoundException e) {
            L.i("ObjectToFile: File not found: " + filename);
        } catch (Exception e) {
            L.w(e);
            delete(context, filename);
        } finally {
            IOUtilsLite.closeQuietly(ois);
            IOUtilsLite.closeQuietly(cis);
            IOUtilsLite.closeQuietly(fis);
        }

        return null;
    }

    /**
     * Deletes a file, stored in the application's private file storage, and
     * returns if the deletion was successful or no.
     *
     * @param context  Needed for opening the application's private file storage.
     * @param filename The filename to delete.
     * @return boolean telling if the deletion was successful or no.
     */
    public static boolean delete(Context context, String filename) {
        return context.getApplicationContext().deleteFile(filename);
    }

    /**
     * Checks if a file already exists or no.
     *
     * @param context  Needed for opening the application's private file storage.
     * @param filename The filename to check.
     * @return boolean telling if the file already exists or no.
     */
    public static boolean exists(Context context, String filename) {
        try {
            context.getApplicationContext().openFileInput(filename).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
