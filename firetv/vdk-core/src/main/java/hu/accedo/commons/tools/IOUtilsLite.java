/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Extract/reimplementation of: org.apache.commons.io.IOUtils
 */
public class IOUtilsLite {
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Unconditionally close a <code>Closeable</code>.
     * <p>
     * Equivalent to {@link Closeable#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     * <p>
     * Example code:
     * <pre>
     *   Closeable closeable = null;
     *   try {
     *       closeable = new FileReader("foo.txt");
     *       // process closeable
     *       closeable.close();
     *   } catch (Exception e) {
     *       // error handling
     *   } finally {
     *       IOUtils.closeQuietly(closeable);
     *   }
     * </pre>
     *
     * @param closeable the object to close, may be null or already closed
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
        }
    }

    /**
     * Unconditionally close a <code>URLConnection</code>.
     * <p>
     * Equivalent to ((HttpURLConnection) urlConnection).disconnect(), except any exceptions will be ignored.
     * This is typically used in finally blocks.
     * <p>
     * Example code:
     * <pre>
     *   URLConnection urlConnection = null;
     *   try {
     *       urlConnection = url.openConnection();
     *   } catch (Exception e) {
     *       // error handling
     *   } finally {
     *       IOUtils.closeQuietly(urlConnection);
     *   }
     * </pre>
     *
     * @param urlConnection the object to close, may be null or already closed
     * @since 2.0
     */
    public static void closeQuietly(URLConnection urlConnection) {
        try {
            if (urlConnection != null && urlConnection instanceof HttpURLConnection) {
                ((HttpURLConnection) urlConnection).disconnect();
            }
        } catch (Exception e) {
        }
    }

    /**
     * Writes chars from a <code>String</code> to bytes on an
     * <code>OutputStream</code> using the default character encoding of the
     * platform.
     * <p>
     * This method uses {@link String#getBytes()}.
     *
     * @param output       the <code>String</code> to write, null ignored
     * @param outputStream the <code>OutputStream</code> to write to
     * @throws NullPointerException if output is null
     * @throws IOException          if an I/O error occurs
     */
    public static void write(String output, OutputStream outputStream) throws IOException {
        outputStream.write(output.getBytes(Charset.forName(DEFAULT_ENCODING)));
    }

    /**
     * Get the contents of an <code>InputStream</code> as a String
     * using the default character encoding of the platform.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param inputStream the <code>InputStream</code> to read from
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static String toString(InputStream inputStream) throws IOException {
        return new String(toByteArray(inputStream), DEFAULT_ENCODING);
    }

    /**
     * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param inputStream the <code>InputStream</code> to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O error occurs
     */
    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    /**
     * Get the contents of a <code>URLConnection</code> as a <code>byte[]</code>.
     *
     * @param urlConnection the <code>URLConnection</code> to read
     * @return the requested byte array
     * @throws NullPointerException if the urlConn is null
     * @throws IOException          if an I/O exception occurs
     */
    public static byte[] toByteArray(URLConnection urlConnection) throws IOException {
        InputStream inputStream = urlConnection.getInputStream();
        try {
            return IOUtilsLite.toByteArray(inputStream);
        } finally {
            closeQuietly(inputStream);
        }
    }

    /**
     * Get the contents of a <code>URL</code> as a <code>byte[]</code>.
     *
     * @param url the <code>URL</code> to read
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws IOException          if an I/O exception occurs
     */
    public static byte[] toByteArray(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        try {
            return IOUtilsLite.toByteArray(urlConnection);
        } finally {
            closeQuietly(urlConnection);
        }
    }
}
