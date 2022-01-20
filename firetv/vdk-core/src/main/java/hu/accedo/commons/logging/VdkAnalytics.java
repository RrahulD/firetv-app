/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.logging;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import dalvik.system.DexFile;
import hu.accedo.commons.net.PostBody;
import hu.accedo.commons.net.restclient.Response;
import hu.accedo.commons.net.restclient.RestClient;
import hu.accedo.commons.net.restclient.RestClient.LogLevel;
import hu.accedo.commons.threading.SafeAsyncTask;
import hu.accedo.commons.tools.AppDeviceInfoTools;
import hu.accedo.commons.tools.DeviceIdentifier;

public class VdkAnalytics extends ContentProvider {
    private static final String TAG = VdkAnalytics.class.getSimpleName();
    private static final String UNRECOGNISED_VERSION = "N/A";

    private static final HashMap<String, String> oldVersionLibraryMap = new HashMap<>();

    static {
        oldVersionLibraryMap.put("hu.accedo.commons.serice.ovp", "vdk-accedo-ovp");
        oldVersionLibraryMap.put("hu.accedo.commons", "vdk-core");
        oldVersionLibraryMap.put("hu.accedo.commons.widgets.epg", "vdk-epg");
        oldVersionLibraryMap.put("hu.accedo.commons.widgets.exowrapper", "vdk-exowrapper");
        oldVersionLibraryMap.put("hu.accedo.commons.widgets.modular", "vdk-modular");
        oldVersionLibraryMap.put("hu.accedo.commons.service.vikimap", "vdk-vikimap");
        oldVersionLibraryMap.put("hu.accedo.commons.widgets", "vdk-widgets");
    }

    @Override
    public void attachInfo(Context context, ProviderInfo providerInfo) {
        super.attachInfo(context, providerInfo);

        if (context == null || providerInfo == null || "hu.accedo.commons.VdkAnalytics".equals(providerInfo.authority)) {
            L.w(TAG, "Variable applicationId in application's build.gradle missing, skipping analytics.");
            return;
        }

        if (AppDeviceInfoTools.isDebuggable(context)) {
            final String payload = getPayload(context);
            if (!TextUtils.isEmpty(payload)) {
                new SafeAsyncTask<Void, Void, Response>() {
                    @Override
                    public Response call(Void... voids) throws Exception {
                        return new RestClient("https://www.google-analytics.com/batch")
                                .setMethod(RestClient.Method.POST)
                                .setLogLevel(LogLevel.OFF)
                                .setPayload(payload)
                                .connect();
                    }
                }.executeAndReturn();
            }
        }
    }

    private String getPayload(Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        Map<String, String> vdkComponents = getVdkComponentVersions(context);
        for (Entry<String, String> entry : vdkComponents.entrySet()) {
            String payloadLine = new PostBody()
                    .addParam("v", "1")
                    .addParam("tid", "UA-71352223-6")
                    .addParam("cid", DeviceIdentifier.getDeviceId(context))
                    .addParam("t", "event")
                    .addParam("an", AppDeviceInfoTools.getApplicationTitle(context))
                    .addParam("aid", context.getPackageName())
                    .addParam("av", AppDeviceInfoTools.getApplicationVersionName(context))
                    .addParam("ec", entry.getKey())
                    .addParam("ea", entry.getValue())
                    .toString();

            stringBuilder.append(payloadLine).append("\n");
        }

        return stringBuilder.toString().replaceAll("\\+", "%20");
    }

    // ContentProvider things we don't need
    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    // Generic component gatherer method, that others may use aswell.
    public static Map<String, String> getVdkComponentVersions(Context context) {
        TreeMap<String, String> result = new TreeMap<>();

        try {
            DexFile df = new DexFile(context.getPackageCodePath());
            for (Enumeration<String> iter = df.entries(); iter.hasMoreElements();) {
                String entry = iter.nextElement();
                if (entry != null && (entry.startsWith("hu.accedo.commons.") || entry.startsWith("tv.accedo.vdk.")) && entry.endsWith("BuildConfig")) {
                    try {
                        String name = null;

                        Class<?> buildConfig = Class.forName(entry);

                        String moduleName = getBuildConfigFieldText(buildConfig, "MODULE_NAME");
                        String packageName = getBuildConfigFieldText(buildConfig, "APPLICATION_ID");
                        String versionName = getBuildConfigFieldText(buildConfig, "VERSION_NAME");

                        if (moduleName != null) {
                            name = moduleName;
                        } else if (oldVersionLibraryMap.containsKey(packageName)) {
                            name = oldVersionLibraryMap.get(packageName);
                        }

                        if (name != null) {
                            result.put(name, versionName != null ? versionName : UNRECOGNISED_VERSION);
                        }
                    } catch (Exception e) { // ClassNotFoundException from Class.forName(), but we're playing it safe.
                        L.w(TAG, e);
                    }
                }
            }
        } catch (Exception e) { // IOException from new DexFile(), but we're playing it safe.
            L.w(TAG, e);
        }

        return result;
    }

    // Utility methods
    private static String getBuildConfigFieldText(Class<?> buildConfig, String fieldName) {
        try {
            Field field = buildConfig.getField(fieldName);
            field.setAccessible(true);

            String fieldText = field.get(null).toString();
            return TextUtils.isEmpty(fieldText) ? null : fieldText;
        } catch (Exception e) {
            return null;
        }
    }
}
