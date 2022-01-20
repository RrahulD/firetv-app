/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpTools {
    public static String toString(Map<String, List<String>> headers) {
        StringBuilder sb = new StringBuilder();
        if (headers == null || headers.isEmpty()) {
            sb.append("<empty>");
        } else {
            for (Entry<String, List<String>> header : headers.entrySet()) {
                sb.append(String.format("%s=%s\n", header.getKey(), header.getValue()));
            }
        }
        return sb.toString();
    }

    public static String getPrintableRequestProperties(HttpURLConnection httpUrlConnection) {
        try {
            if (httpUrlConnection.getRequestProperties() != null && !httpUrlConnection.getRequestProperties().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Entry<String, List<String>> header : httpUrlConnection.getRequestProperties().entrySet()) {
                    sb.append(String.format("%s=%s\n", header.getKey(), header.getValue()));
                }
                return sb.toString();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static String getPrintableResponseHeaders(HttpURLConnection httpUrlConnection) {
        try {
            if (httpUrlConnection.getHeaderFields() != null && !httpUrlConnection.getHeaderFields().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Entry<String, List<String>> header : httpUrlConnection.getHeaderFields().entrySet()) {
                    sb.append(String.format("%s=%s\n", header.getKey(), header.getValue()));
                }
                return sb.toString();
            }
        } catch (Exception e) {
        }
        return null;
    }
}
