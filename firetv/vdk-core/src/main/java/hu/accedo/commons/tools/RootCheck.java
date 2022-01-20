/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

import java.io.File;

/**
 * Methods that can be used to check if the device is rooted.
 */
public class RootCheck {

    /**
     * Tries to check for root by doing the following methods:
     * - checking if the OS is built with test-keys
     * - checking if /system/app/Superuser.apk is present
     *
     * @return checkRootMethod1() || checkRootMethod2()
     */
    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        try {
            File file = new File("/system/app/Superuser.apk");
            return file.exists();
        } catch (Exception e) {
        }
        return false;
    }

//    private static boolean checkRootMethod3() {
//    	ArrayList<String> response = executeCommand(new String[] { "/system/xbin/which", "su" }); 
//        return response!=null && !response.isEmpty();
//    }
//
//    private static ArrayList<String> executeCommand(String[] command) {
//        String line = null;
//        ArrayList<String> fullResponse = new ArrayList<String>();
//        Process localProcess = null;
//        try {
//            localProcess = Runtime.getRuntime().exec(command);
//        } catch (Exception e) {
//        	L.e(e);
//        	return null;
//        }
//        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(localProcess.getOutputStream()));
//        BufferedReader in = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
//        try {
//            while ((line = in.readLine()) != null) {
//            	L.d("--> Line received: " + line);
//                fullResponse.add(line);
//            }
//        } catch (Exception e) {
//        	L.e(e);
//        }
//        L.d("--> Full response was: " + fullResponse);
//        return fullResponse;
//    }
}
