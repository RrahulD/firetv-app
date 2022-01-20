/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import java.io.File;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import hu.accedo.commons.R;
import hu.accedo.commons.logging.VdkAnalytics;

public class AppDeviceInfoTools {
    /**
     * Reads a bool from values/settings.xml returning false, and values-sw600dp/settings.xml returning true.
     *
     * @param context can be any type of context, will not be stored
     * @return true if the device is a tablet, aka it has a smallest width of 600dp or more.
     */
    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    /**
     * @param context can be any type of context, will not be stored
     * @return true, if the device is a debug build. Useful for libraries, where BuildConfig.DEBUG is unreliable.
     */
    public static boolean isDebuggable(Context context) {
        return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }

    /**
     * @param context can be any type of context, will not be stored
     * @return the application title as a string, as set inside the AndroidManifest. Takes localisation into consideration.
     */
    public static String getApplicationTitle(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;

        if (stringId != 0) {
            return context.getString(stringId);
        } else if (applicationInfo.nonLocalizedLabel != null) {
            return applicationInfo.nonLocalizedLabel.toString(); //This is sometimes null in test-cases in Android Studio 3.0, did not happen before.
        } else {
            return applicationInfo.name;
        }
    }

    /**
     * Convenience getter, to avoid a try-catch that in theory should never happen.
     * Calls: context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName
     *
     * @param context can be any type of context, will not be stored
     * @return the versionName of the application.
     */
    public static String getApplicationVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return null; //Should never happen.
        }
    }

    /**
     * Convenience getter, to avoid a try-catch that in theory should never happen.
     * Calls: context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
     *
     * @param context can be any type of context, will not be stored
     * @return the versionCode of the application
     */
    public static int getApplicationVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            return -1;
        }
    }

    @Deprecated
    public static String getMACAddress() {
        return getMACAddress(null);
    }

    @Deprecated
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null && !intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null)
                    if (interfaceName == null)
                        continue;
                    else
                        return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0)
                    buf.deleteCharAt(buf.length() - 1);
                return buf.toString().replaceAll(":", "-");
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    /**
     * @param context can be any type of context, will not be stored
     * @return detailed debug information about the device the app is run on.
     */
    public static String getDeviceInformation(Context context) {
        // Root checks
        String buildTags = android.os.Build.TAGS;
        boolean testKeys = buildTags != null && buildTags.contains("test-keys");
        boolean superUser = false;
        try {
            File file = new File("/system/app/Superuser.apk");
            superUser = file.exists();
        } catch (Exception e) {
        }

        // Build info sheet
        StringBuilder sb = new StringBuilder();
        sb.append("Current time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
        sb.append("\nandroid.os.Build");
        sb.append("\n\tBOARD: " + android.os.Build.BOARD);
        sb.append("\n\tBOOTLOADER: " + android.os.Build.BOOTLOADER);
        sb.append("\n\tBRAND: " + android.os.Build.BRAND);
        sb.append("\n\tCPU_ABI: " + android.os.Build.CPU_ABI);
        sb.append("\n\tCPU_ABI2: " + android.os.Build.CPU_ABI2);
        sb.append("\n\tDEVICE: " + android.os.Build.DEVICE);
        sb.append("\n\tDISPLAY: " + android.os.Build.DISPLAY);
        sb.append("\n\tFINGERPRINT: " + android.os.Build.FINGERPRINT);
        sb.append("\n\tHARDWARE: " + android.os.Build.HARDWARE);
        sb.append("\n\tHOST: " + android.os.Build.HOST);
        sb.append("\n\tID: " + android.os.Build.ID);
        sb.append("\n\tMANUFACTURER: " + android.os.Build.MANUFACTURER);
        sb.append("\n\tMODEL: " + android.os.Build.MODEL);
        sb.append("\n\tPRODUCT: " + android.os.Build.PRODUCT);
        sb.append("\n\tSERIAL: " + android.os.Build.SERIAL);
        sb.append("\n\tTAGS: " + android.os.Build.TAGS);
        sb.append("\n\tTIME: " + android.os.Build.TIME);
        sb.append("\n\tTYPE: " + android.os.Build.TYPE);
        sb.append("\n\tUNKNOWN: " + android.os.Build.UNKNOWN);
        sb.append("\n\tUSER: " + android.os.Build.USER);

        sb.append("\n\nandroid.os.Build.VERSION");
        sb.append("\n\tCODENAME: " + android.os.Build.VERSION.CODENAME);
        sb.append("\n\tINCREMENTAL: " + android.os.Build.VERSION.INCREMENTAL);
        sb.append("\n\tRELEASE: " + android.os.Build.VERSION.RELEASE);
        sb.append("\n\tSDK_INT: " + android.os.Build.VERSION.SDK_INT);

        sb.append("\n\nhu.accedo.commons.tools.DeviceIdentifier");
        sb.append("\n\tgetDeviceId(): " + DeviceIdentifier.getDeviceId(context));

        sb.append("\n\nhu.accedo.commons.tools.RootCheck");
        sb.append("\n\tisDeviceRooted(): " + RootCheck.isDeviceRooted());
        sb.append("\n\tTest-keys: " + testKeys);
        sb.append("\n\tSuperuser.apk: " + superUser);

        sb.append("\n\nVdkAnalytics.getVdkComponentVersions()");

        Map<String, String> vdkComponents = VdkAnalytics.getVdkComponentVersions(context);
        for (Entry<String, String> entry : vdkComponents.entrySet()) {
            sb.append("\n\t");
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue());
        }

        return sb.toString();
    }

    /**
     * @param className a fully qualified class name.
     * @return true, if the class is available in the ClassLoader.
     */
    public static boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
