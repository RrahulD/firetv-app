/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

import hu.accedo.commons.logging.L;

public class DeviceIdentifier {
    private static final String SHAREDPREF_KEY = "DEVICE_ID";
    private static final String FILENAME = "D_ID";
    private static String EXTERNAL_FILE_PATH = File.separator + "Android" + File.separator + "data" + File.separator;

    private static String deviceId;

    /**
     * Tries to retrieve any stored device identifier, or generates or stores a new one if necessary.
     * <p>
     * Stores deviceId in internal storage - capable of surviving external storage formatting
     * And also stores on the external storage - capable of surviving app uninstall and reinstall
     *
     * @param context can be any type of context, will not be stored
     * @return the generated or retrieved DeviceId.
     */
    public static String getDeviceId(Context context) {
        // Context check
        if (context == null) {
            throw new NullPointerException("The context provided for DeviceIdentifier.getDeviceId() must not be null.");
        }

        // Try return from memory
        if (deviceId != null) {
            return deviceId;
        }

        // Try return from SharedPrefs
        deviceId = readFromPref(context);
        if (deviceId != null) {
            writeEverywhere(context, deviceId); // Maybe it got deleted from disk
            return deviceId;
        }

        // Try return from disk
        deviceId = readFromFile();
        if (deviceId != null) {
            writeEverywhere(context, deviceId); // Maybe it got deleted from SharedPrefs
            return deviceId;
        }

        // Generate
        deviceId = generateDeviceId(context);

        // Write
        writeEverywhere(context, deviceId);

        // Return
        return deviceId;
    }

    private static void writeEverywhere(Context context, String deviceId) {
        writeToPref(context, deviceId);
        writeToFile(deviceId);
    }

    private static boolean writeToFile(String content) {
        if (Environment.getExternalStorageDirectory() == null) {
            return false;
        }

        FileOutputStream fos = null;
        try {
            // Make file and folder
            File file = new File(Environment.getExternalStorageDirectory(), EXTERNAL_FILE_PATH + FILENAME);
            if (!file.exists() && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Write
            fos = new FileOutputStream(file);
            IOUtilsLite.write(content, fos);
            return true;
        } catch (Exception e) {
            L.w("Failed to store deviceID on disk. " + e.getMessage());
            return false;
        } finally {
            IOUtilsLite.closeQuietly(fos);
        }
    }

    private static String readFromFile() {
        // Check if exists
        if (Environment.getExternalStorageDirectory() == null) {
            return null;
        }
        File file = new File(Environment.getExternalStorageDirectory(), EXTERNAL_FILE_PATH + FILENAME);
        if (!file.exists()) {
            return null;
        }

        // Read
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return IOUtilsLite.toString(fis);
        } catch (Exception e) {
            L.w("Failed to read deviceID from disk. " + e.getMessage());
        } finally {
            IOUtilsLite.closeQuietly(fis);
        }
        return null;
    }

    private static void writeToPref(Context context, String content) {
        context.getSharedPreferences(SHAREDPREF_KEY, Context.MODE_PRIVATE).edit().putString(SHAREDPREF_KEY, deviceId).apply();
    }

    private static String readFromPref(Context context) {
        return context.getSharedPreferences(SHAREDPREF_KEY, Context.MODE_PRIVATE).getString(SHAREDPREF_KEY, null);
    }

    private static String generateDeviceId(Context context) {
        try {
            // Get ANDROID_ID
            String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

            // Check ANDROID_ID
            if (androidId == null || androidId.length() != 16) {
                throw new NumberFormatException("ANDROID_ID null or not 64bit.");
            }

            // Parse ANDROID_ID
            return UUID.nameUUIDFromBytes(androidId.getBytes()).toString();

        } catch (Exception e) {
            //Something went wrong, use random UUID
            L.w("DeviceIdentifier", "Failed to generate UUID from ANDROID_ID. Falling back to random UUID. Reason: " + Log.getStackTraceString(e));
            return UUID.randomUUID().toString();
        }
    }
}
