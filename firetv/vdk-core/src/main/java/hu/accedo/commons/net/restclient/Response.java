/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.net.restclient;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import hu.accedo.commons.logging.L;
import hu.accedo.commons.net.Parser;
import hu.accedo.commons.net.ThrowingParser;
import hu.accedo.commons.tools.IOUtilsLite;
import hu.accedo.commons.tools.StringParser;

public class Response implements Serializable {
    public static final DateFormat DATE_HEADER_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);

    private int code = -1;
    private byte[] body;
    private String charset;
    private String url;
    private Map<String, List<String>> headers = new HashMap<>();
    private transient Exception caughtException;

    /**
     * The constructor to use when the connection was successful.
     *
     * @param url     the url we were connecting to.
     * @param charset the charset used, the default being RestClient.charset
     */
    public Response(String url, String charset, int code, byte[] body, Map<String, List<String>> headers) {
        this.url = url;
        this.charset = charset;
        this.code = code;
        this.body = body;
        this.headers = headers;
    }

    /**
     * The constructor to use when connection has failed.
     *
     * @param url    the url we wanted to connect to.
     * @param reason the reason why we couldn't connect.
     */
    public Response(String url, Exception reason) {
        this.url = url;
        this.caughtException = reason;
    }

    /**
     * The deprecated constructor to use when connection has failed.
     *
     * @deprecated add failure reason
     */
    @Deprecated
    public Response(String url) {
        this.url = url;
    }

    /**
     * @return the response code, or -1 if no connection was made
     */
    public int getCode() {
        return code;
    }

    /**
     * @return the response body, string encoded with the charset of the RestClient instance, that created this Response
     */
    public String getText() {
        byte[] output = body;

        if (headers.get("Content-Encoding") != null && headers.get("Content-Encoding").contains("gzip")) {
            output = tryDecodeGzip(body);
        }

        return StringParser.tryCreateString(output, charset, null);
    }

    /**
     * @return the raw response body
     */
    public byte[] getRawResponse() {
        return body;
    }

    /**
     * @return true, if the response code is 200 or 204
     */
    public boolean isSuccess() {
        return code == 200 || code == 204;
    }

    /**
     * @return the url fetched
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return a map of response headers, or an empty map if no connection was established (never null)
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * @param name the name of the header to find
     * @return the first value for that header or an empty string
     */
    public String getFirstHeader(String name) {
        List<String> values = headers.get(name);
        if (values != null && !values.isEmpty()) {
            return values.get(0);
        }

        return "";
    }

    public long getServerTime() {
        try {
            String dateHeader = getFirstHeader("Date");
            return DATE_HEADER_FORMAT.parse(dateHeader).getTime();
        } catch (ParseException e) {
            L.w(RestClient.TAG, "Failed to parse server time from Date header, returning device time.", e);
            return System.currentTimeMillis();
        }
    }

    /**
     * @return The exception caught during the creation of the urlConnection used, or during connection, or during the parsing of the response.
     */
    public Exception getCaughtException() {
        return caughtException;
    }

    /**
     * Parses this response with the supplied parser, or throws an exception on failure
     *
     * @param parser A parser providing the logic on how to convert the Response into T
     * @param <T>    The type to parse in.
     * @param <E>    The kind of exception thrown by the parser.
     * @return The domain parsed variant of this Response's body
     * @throws E thrown by the parser provided.
     */
    public <T, E extends Exception> T getParsedText(ThrowingParser<Response, T, E> parser) throws E {
        return parser.parse(this);
    }

    /**
     * @deprecated use {@link Response#getParsedText(ThrowingParser)} instead.
     */
    @Deprecated
    public <T> T getParsedText(Parser<Response, T> parser) {
        return parser.parse(this);
    }

    private byte[] tryDecodeGzip(byte[] input) {
        try {
            return IOUtilsLite.toByteArray(new GZIPInputStream(new ByteArrayInputStream(body)));
        } catch (Exception e) {
            L.w(RestClient.TAG, "Content-Encoding=[gzip], but failed to decode response as gzip.");
            return input;
        }
    }
}
