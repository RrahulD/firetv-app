/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.logging;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import java.util.ArrayList;

/**
 * Wrapper created around android.util.Log, to have some generic control over when and how things get logged.
 */
public abstract class L {
    private static final String LOG_FORMAT = "%1$s\n%2$s";
    private static String LOG_TAG = "Accedo";
    private static boolean LOG_ENABLED = true;
    private static ArrayList<LogListener> logListeners = new ArrayList<>();

    /**
     * Calls clearLogListeners() then addLogListener()
     * !! This method should only be used in Application.onCreate() !!
     * Deprecation: Use addLogListener() instead.
     *
     * @param logListener the logListener to sign up.
     */
    @Deprecated
    public static void setLogListener(LogListener logListener) {
        clearLogListeners();
        addLogListener(logListener);
    }

    /**
     * !! This method should only be used in Application.onCreate() !!
     *
     * @param logListener the logListener to sign up.
     */
    public static void addLogListener(LogListener logListener) {
        if (logListener != null && !logListeners.contains(logListener)) {
            logListeners.add(logListener);
        }
    }

    /**
     * !! This method should only be used in Application.onCreate() !!
     *
     * @param logListener the logListener to remove.
     */
    public static void removeLogListener(LogListener logListener) {
        if (logListener != null && logListeners.contains(logListener)) {
            logListeners.remove(logListener);
        }
    }

    /**
     * Removes all previously set logListeners.
     */
    public static void clearLogListeners() {
        logListeners.clear();
    }

    /**
     * !! This method should only be used in Application.onCreate() !!
     *
     * @param tag the default tag this class uses, when no tag is specified.
     */
    public static void setDefaultTag(String tag) {
        L.LOG_TAG = tag;
    }

    /**
     * !! This method should only be called in Application.onCreate() !!
     *
     * @param enabled when false, LogCat logging will be disabled.
     */
    public static void setEnabled(boolean enabled) {
        L.LOG_ENABLED = enabled;
    }

    /**
     * Enables or disables LogCat logging of any incoming logs, based on the signing key used to sign the app.
     * !! This method should only be called in Application.onCreate() !!
     *
     * @param context can be any type of context, will not be stored
     */
    public static void setEnabled(Context context) {
        // See if we're a debug or a release build
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (packageInfo.signatures.length > 0) {
                String signature = new String(packageInfo.signatures[0].toByteArray());
                LOG_ENABLED = signature.contains("Android Debug");
            }
        } catch (NameNotFoundException e) {
            LOG_ENABLED = false;
            L.e(e);
        }
    }

    /**
     * Check whether logging to LogCat is enabled.
     *
     * @return True if logging is enabled, false otherwise.
     */
    public static boolean isEnabled() {
        return L.LOG_ENABLED;
    }

    private L() {
    }

    // Default tags
    // -----------------------------------------------------------------
    public static void d(String message, Object... args) {
        log(null, Log.DEBUG, null, message, args);
    }

    public static void i(String message, Object... args) {
        log(null, Log.INFO, null, message, args);
    }

    public static void w(String message, Object... args) {
        log(null, Log.WARN, null, message, args);
    }

    public static void w(Throwable ex) {
        log(null, Log.WARN, ex, null);
    }

    public static void w(Throwable ex, String message, Object... args) {
        log(null, Log.WARN, ex, message, args);
    }

    public static void e(String message, Object... args) {
        log(null, Log.ERROR, null, message, args);
    }

    public static void e(Throwable ex) {
        log(null, Log.ERROR, ex, null);
    }

    public static void e(Throwable ex, String message, Object... args) {
        log(null, Log.ERROR, ex, message, args);
    }

    public static void wtf(String message, Object... args) {
        e(message, args);
    }

    public static void wtf(Throwable ex) {
        e(ex);
    }

    public static void wtf(Throwable ex, String message, Object... args) {
        e(ex, message, args);
    }

    // Custom tags
    // -----------------------------------------------------------------
    public static void d(String tag, String message, Object... args) {
        log(tag, Log.DEBUG, null, message, args);
    }

    public static void i(String tag, String message, Object... args) {
        log(tag, Log.INFO, null, message, args);
    }

    public static void w(String tag, String message, Object... args) {
        log(tag, Log.WARN, null, message, args);
    }

    public static void w(String tag, Throwable ex) {
        log(tag, Log.WARN, ex, null);
    }

    public static void w(String tag, Throwable ex, String message, Object... args) {
        log(tag, Log.WARN, ex, message, args);
    }

    public static void e(String tag, String message, Object... args) {
        log(tag, Log.ERROR, null, message, args);
    }

    public static void e(String tag, Throwable ex) {
        log(tag, Log.ERROR, ex, null);
    }

    public static void e(String tag, Throwable ex, String message, Object... args) {
        log(tag, Log.ERROR, ex, message, args);
    }

    public static void wtf(String tag, String message, Object... args) {
        e(tag, message, args);
    }

    public static void wtf(String tag, Throwable ex) {
        e(tag, ex);
    }

    public static void wtf(String tag, Throwable ex, String message, Object... args) {
        e(tag, ex, message, args);
    }

    // Custom priority calls
    // -----------------------------------------------------------------
    public static void log(int priority, String message, Object... args) {
        log(null, priority, null, message, args);
    }

    public static void log(int priority, Throwable ex) {
        log(null, priority, ex, null);
    }

    public static void log(String tag, int priority, String message, Object... args) {
        log(tag, priority, null, message, args);
    }

    public static void log(String tag, int priority, Throwable ex) {
        log(tag, priority, ex, null);
    }

    // General call
    // -----------------------------------------------------------------
    public static void log(String tag, int priority, Throwable ex, String message, Object... args) {
        if (LOG_ENABLED) {
            if (tag == null) {
                tag = LOG_TAG;
            }

            if (args != null && args.length > 0) {
                message = String.format(message, args);
            }

            String log;
            if (ex == null) {
                log = message;
            } else {
                String logMessage = message == null ? ex.getMessage() : message;
                String logBody = Log.getStackTraceString(ex);
                log = String.format(LOG_FORMAT, logMessage, logBody);
            }
            Log.println(priority, tag, log);

            for (LogListener logListener : logListeners) {
                logListener.onLog(log);
                logListener.onLog(tag, priority, ex, message, args);
            }
        }
    }

    public static abstract class LogListener {
        @Deprecated
        public void onLog(String message) {

        }

        public abstract void onLog(String tag, int priority, Throwable ex, String message, Object... args);
    }
}
