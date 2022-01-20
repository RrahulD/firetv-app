/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.cache;

import android.content.Context;

import java.io.Serializable;

import hu.accedo.commons.logging.L;

/**
 * This util can be used to persistently store serializable objects in the device's internal private storage.
 *
 * @deprecated use {@link SerializableToFile instead}
 */
@Deprecated
public class ObjectToFile {

    /**
     * Serializes an object into a file, stored in the application's private file storage.
     *
     * @param context  Needed for opening the application's private file storage.
     * @param object   The object to serialize.
     * @param filename The filename to write into.
     * @return true, if writing has succeeded, false otherwise.
     */
    public static boolean write(Context context, Object object, String filename) {
        try {
            return SerializableToFile.write(context, (Serializable) object, filename);
        } catch (ClassCastException e) {
            L.w(e);
            return false;
        }
    }

    /**
     * Serializes an object into a file, stored in the application's private file storage.
     * The file will be AES256 encrypted, with the given password.
     *
     * @param context  Needed for opening the application's private file storage.
     * @param object   The object to serialize.
     * @param filename The filename to write into.
     * @param password The password to encrypt with.
     * @return true, if writing has succeeded, false otherwise.
     */
    public static boolean write(Context context, Object object, String filename, String password) {
        try {
            return SerializableToFile.write(context, (Serializable) object, filename, password);
        }catch (ClassCastException e){
            L.w(e);
            return false;
        }
    }

    /**
     * Deserializes and returns an object from a given file, stored in the application's private file storage.
     *
     * @param context  Needed for opening the application's private file storage.
     * @param filename The filename to read from.
     * @return The deserialized object, or null if unsuccessful.
     */
    public static Object read(Context context, String filename) {
        return SerializableToFile.read(context, filename);
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
    public static Object read(Context context, String filename, String password) {
        return SerializableToFile.read(context, filename, password);
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
        return SerializableToFile.delete(context, filename);
    }

    /**
     * Checks if a file already exists or no.
     *
     * @param context  Needed for opening the application's private file storage.
     * @param filename The filename to check.
     * @return boolean telling if the file already exists or no.
     */
    public static boolean exists(Context context, String filename) {
        return SerializableToFile.exists(context, filename);
    }
}
