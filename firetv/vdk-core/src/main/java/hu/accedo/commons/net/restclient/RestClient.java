/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.net.restclient;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.accedo.commons.logging.L;
import hu.accedo.commons.net.PathUrl;
import hu.accedo.commons.net.PostBody;
import hu.accedo.commons.net.restclient.implementations.Implementation;
import hu.accedo.commons.net.restclient.implementations.OkHttp3Impl;
import hu.accedo.commons.net.restclient.implementations.UrlConnectionImpl;
import hu.accedo.commons.tools.HttpTools;
import hu.accedo.commons.tools.StringParser;

import static hu.accedo.commons.tools.AppDeviceInfoTools.isClassAvailable;

/**
 * Useful for handling single-shot request, and their responses.
 * 
 * However, for building up your API layer, you might want to check out retrofit instead:
 * https://square.github.io/retrofit/
 * 
 * Might get deprecated in the future.
 */
public class RestClient {
    public static final String TAG = "RestClient";
    public static final int DEFAULT_TIMEOUT_CONNECT = 5000;
    public static final int DEFAULT_TIMEOUT_READ = 10000;
    public static final long DEFAULT_CACHE_SIZE = 10 * 1024 * 1024;  // 10 MiB
    public static final Implementation DEFAULT_IMPLEMENTATION = isClassAvailable("okhttp3.OkHttpClient") ? new OkHttp3Impl() : new UrlConnectionImpl();

    protected final ArrayList<HttpCookie> cookies = new ArrayList<>();
    protected final HashMap<String, List<String>> headers = new HashMap<>();
    protected String url;
    protected Method method = Method.GET;
    protected byte[] payload;
    protected String charset = "UTF-8";
    protected int timeoutConnect = DEFAULT_TIMEOUT_CONNECT;
    protected int timeoutRead = DEFAULT_TIMEOUT_READ;
    protected LogLevel logLevel = LogLevel.NORMAL;
    protected File httpCacheDir;
    protected Implementation implementation = DEFAULT_IMPLEMENTATION;
    protected OnResponseListener onResponseListener;

    /**
     * @param url the url to load
     */
    public RestClient(String url) {
        this.url = url;
    }

    /**
     * @param pathUrl the url to load
     */
    public RestClient(PathUrl pathUrl) {
        this.url = pathUrl != null ? pathUrl.toString() : null;
    }

    /**
     * @deprecated use regular constructor with {@link RestClient#setHttpCacheDir(File)} instead.
     */
    @Deprecated
    public RestClient(String url, File httpCacheDir) {
        this(url);
        setHttpCacheDir(httpCacheDir);
    }

    /**
     * @deprecated use regular constructor with {@link RestClient#setHttpCacheDir(File)} instead.
     */
    @Deprecated
    public RestClient(PathUrl pathUrl, File httpCacheDir) {
        this(pathUrl);
        setHttpCacheDir(httpCacheDir);
    }

    /**
     * Connects and tries to obtain a response.
     *
     * @return the obtained Response. Never null.
     */
    public Response connect() {
        logRequest();
        Response response = implementation.fetchResponse(this);
        logResponse(response);

        //Notify listeners
        if (onResponseListener != null) {
            onResponseListener.onResponse(response);
        }

        return response;
    }

    public <E extends Exception> Response connect(RetryResponseChecker<E> responseChecker) throws E {
        logRequest();
        Response response = implementation.fetchResponse(this);
        logResponse(response);

        //Check response, and throw if necessary
        if (responseChecker != null) {
            response = responseChecker.throwIfNecessary(this, response);
        }

        //Notify listeners if we're still here
        if (onResponseListener != null) {
            onResponseListener.onResponse(response);
        }

        return response;
    }

    public <E extends Exception> Response connect(ResponseChecker<E> responseChecker) throws E {
        logRequest();
        Response response = implementation.fetchResponse(this);
        logResponse(response);

        //Check response, and throw if necessary
        if (responseChecker != null) {
            responseChecker.throwIfNecessary(response);
        }

        //Notify listeners if we're still here
        if (onResponseListener != null) {
            onResponseListener.onResponse(response);
        }

        return response;
    }

    protected void logRequest() {
        if (!L.isEnabled()) {
            return;
        }
        if (!LogLevel.OFF.equals(logLevel)) {
            L.i(TAG, "Sending " + method.name() + " request: " + url);
        }
        if (LogLevel.VERBOSE.equals(logLevel)) {
            L.d(TAG, "Request headers: " + HttpTools.toString(headers));
            L.d(TAG, "Request body: " + StringParser.tryCreateString(payload, charset, null));
        }
    }

    protected void logResponse(Response response) {
        if (!L.isEnabled()) {
            return;
        }
        if (response.getCaughtException() != null) {
            L.w(TAG, response.getCaughtException());
        }
        if (LogLevel.NORMAL.equals(logLevel)) {
            L.d(RestClient.TAG, "Response " + response.getCode() + " for: " + response.getUrl() + "\n" + response.getText());
        } else if (LogLevel.VERBOSE.equals(logLevel)) {
            L.d(RestClient.TAG, "Response for: " + response.getUrl());
            L.d(RestClient.TAG, "Response headers: " + HttpTools.toString(response.getHeaders()));
            L.d(RestClient.TAG, "Response body: " + response.getText());
        }
    }

    /**
     * @return the url this RestClient instance is trying to fetch
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the HTTP method of the call.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Sets the HTTP method of the call.
     *
     * @param method GET, POST, PUT or DELETE
     * @return this RestClient instance for chaining
     */
    public RestClient setMethod(Method method) {
        if (method != null) {
            this.method = method;
        }
        return this;
    }

    /**
     * Sets the value of the specified request header field.
     *
     * @param key   the header name to be added
     * @param value the header value to be added
     * @return this RestClient instance for chaining
     */
    public RestClient setHeader(String key, String value) {
        if (key != null) {
            headers.put(key, new ArrayList<String>());
            headers.get(key).add(value);
        }
        return this;
    }

    /**
     * Adds the given property to the request header. Existing properties with the same name will not be overwritten by this method.
     *
     * @param key   the header name to be added
     * @param value the header value to be added
     * @return this RestClient instance for chaining
     */
    public RestClient addHeader(String key, String value) {
        if (key != null) {
            if (!headers.containsKey(key)) {
                setHeader(key, value);
            } else {
                headers.get(key).add(value);
            }
        }
        return this;
    }

    /**
     * @return an unmodifiable map of headers added to the RestClient so far.
     */
    public Map<String, List<String>> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    /**
     * Replaces all the set headers, with the provided map.
     *
     * @param headers the new header-map to apply to the RestClient.
     */
    public RestClient setHeaders(Map<String, List<String>> headers) {
        this.headers.clear();
        if (headers != null) {
            this.headers.putAll(headers);
        }
        return this;
    }

    /**
     * Sets the given cookies to the request as a "Cookie" header. All previously set cookies will be removed.
     *
     * @param cookies the cookies to be added
     * @return this RestClient instance for chaining
     */
    public RestClient setCookies(List<HttpCookie> cookies) {
        this.cookies.clear();
        return addCookies(cookies);
    }

    /**
     * Appends the given cookies to the request's "Cookie" header.
     *
     * @param cookies the cookies to be added
     * @return this RestClient instance for chaining
     */
    public RestClient addCookies(List<HttpCookie> cookies) {
        if (cookies == null)
            return this;

        //Append list
        this.cookies.addAll(cookies);

        //Build current cookie string
        StringBuilder cookieString = new StringBuilder();
        for (HttpCookie cookie : this.cookies) {
            if (cookie != null) {
                cookieString.append(String.format("%s=\"%s\"; ", cookie.getName(), cookie.getValue()));
            }
        }

        //Set to urlConnection
        setHeader("Cookie", cookieString.toString());

        return this;
    }

    /**
     * @return a copy of the payload set to this RestClient
     */
    public byte[] getPayload() {
        if (payload == null) {
            return payload;
        }
        return Arrays.copyOf(payload, payload.length);
    }

    /**
     * Sets the payload to be sent in the request body. The PostBody will be processed with the charset active at the time this value is set.
     * !!To make sure that this value is encoded properly, make sure the charset is set before calling this method.!!
     *
     * @param postBody The PostBody to be sent in the request body.
     * @return this RestClient instance for chaining
     */
    public RestClient setPayload(PostBody postBody) {
        if (postBody != null) {
            try {
                this.payload = postBody.toString().getBytes(charset);
            } catch (UnsupportedEncodingException e) {
                L.w(TAG, e);
            }
        } else {
            this.payload = null;
        }
        return this;
    }

    /**
     * Sets the payload to be sent in the request body. The string will be processed with the charset active at the time this value is set.
     * !!To make sure that this value is encoded properly, make sure the charset is set before calling this method.!!
     *
     * @param payload The string to be sent in the request body.
     * @return this RestClient instance for chaining
     */
    public RestClient setPayload(String payload) {
        if (payload != null) {
            try {
                this.payload = payload.getBytes(charset);
            } catch (UnsupportedEncodingException e) {
                L.w(TAG, e);
            }
        } else {
            this.payload = null;
        }

        return this;
    }

    /**
     * @param payload The data to be sent in the request body.
     * @return this RestClient instance for chaining
     */
    public RestClient setPayload(byte[] payload) {
        this.payload = payload;
        return null;
    }

    /**
     * Sets the timeouts for the request.
     *
     * @param connect the timeout value for the connection to be established
     * @param read    the timeout value for the fetching operation to be completed
     * @return this RestClient instance for chaining
     */
    public RestClient setTimeout(int connect, int read) {
        this.timeoutConnect = connect;
        this.timeoutRead = read;
        return this;
    }

    /**
     * @return the connect timeout set to this RestClient.
     */
    public int getTimeoutConnect() {
        return timeoutConnect;
    }

    /**
     * @return the read timeout set to this RestClient.
     */
    public int getTimeoutRead() {
        return timeoutRead;
    }

    /**
     * @return the character set this RestClient uses.
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the character set to be used while processing the request. The default value is UTF-8.
     *
     * @param charset the RestClient instance should use. Doesn't accept null.
     * @return this RestClient instance for chaining
     */
    public RestClient setCharset(String charset) {
        if (charset != null) {
            this.charset = charset;
        }
        return this;
    }

    /**
     * @param onResponseListener a response listener to be called when a response is obtained.
     * @return this RestClient instance for chaining
     */
    public RestClient setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
        return this;
    }

    /**
     * @return the underlying implementation this RestClient currently uses to do its requests.
     */
    public Implementation getImplementation() {
        return implementation;
    }

    /**
     * @param implementation the implementation to use during this request. Changes the underlying engine that does the actual requests.
     */
    public RestClient setImplementation(Implementation implementation) {
        this.implementation = implementation;
        return this;
    }

    /**
     * @return the loglevel set to this RestClient.
     */
    public LogLevel getLogLevel() {
        return logLevel;
    }

    /**
     * Sets the level of logging this RestClient instance should do. Possible values:
     * OFF - Quite mode
     * NORMAL - Logs request URL, response code, and response body (DEFAULT)
     * VERBOSE - Logs URLs, headers, and bodies in both directions
     *
     * @param logLevel this RestClient instance should use. Doesn't accept null.
     * @return this RestClient instance for chaining
     */
    public RestClient setLogLevel(LogLevel logLevel) {
        if (logLevel != null) {
            this.logLevel = logLevel;
        }
        return this;
    }

    /**
     * @return the cache directory this RestClient uses. Null if none.
     */
    public File getHttpCacheDir() {
        return httpCacheDir;
    }

    /**
     * @param httpCacheDir the folder to use for httpCaching. Recommended: new File(context.getCacheDir(), "http")
     */
    public RestClient setHttpCacheDir(File httpCacheDir) {
        this.httpCacheDir = httpCacheDir;
        return this;
    }

    /**
     * Adds "Accept-Encoding" headers to the request.
     *
     * @param encoding will be added as an "Accept-Encoding" header
     * @return this RestClient instance for chaining
     */
    public RestClient setEncoding(Encoding encoding) {
        if (encoding != null) {
            setHeader("Accept-Encoding", encoding.value);
        }
        return this;
    }

    public enum Method {
        GET,
        POST,
        PUT,
        DELETE,
        HEAD,
        CONNECT,
        OPTIONS,
        TRACE,
        PATCH
    }

    public enum LogLevel {
        OFF,
        NORMAL,
        VERBOSE
    }

    public enum Encoding {
        NONE(""),
        GZIP("gzip");

        final String value;

        Encoding(String value) {
            this.value = value;
        }
    }

    public interface OnResponseListener {
        void onResponse(Response response);
    }

    /**
     * Can be used to check the Response created during "connect()", and throw exceptions based on what we got.
     * This variant can also do retries, and replace the Response returned based on how the retries went.
     *
     * @param <T> the kind of Exception this ResponseChecker will throw.
     */
    public interface RetryResponseChecker<T extends Exception> {
        /**
         * @param restClient the restclient this ResponseChecker was used on.
         * @param response   the response we need to check
         * @return the response after the retries have happened
         * @throws T the kind of Exception this ResponseChecker will throw.
         */
        Response throwIfNecessary(RestClient restClient, Response response) throws T;
    }

    /**
     * Can be used to check the Response created during "connect()", and throw exceptions based on what we got.
     *
     * @param <T> the kind of Exception this ResponseChecker will throw.
     */
    public interface ResponseChecker<T extends Exception> {
        /**
         * @param response the response we need to check
         * @throws T the kind of Exception this ResponseChecker will throw.
         */
        void throwIfNecessary(Response response) throws T;
    }
}
