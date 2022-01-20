package hu.accedo.commons.net.restclient.implementations;

import android.net.http.HttpResponseCache;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import hu.accedo.commons.net.restclient.Response;
import hu.accedo.commons.net.restclient.RestClient;
import hu.accedo.commons.tools.IOUtilsLite;

public class UrlConnectionImpl implements Implementation {
    @NonNull
    @Override
    public Response fetchResponse(@NonNull RestClient restClient) {
        InputStream is = null;
        OutputStream os = null;
        try {
            HttpURLConnection httpUrlConnection = getUrlConnection(restClient);
            httpUrlConnection.connect();

            // Push payload
            byte[] payload = restClient.getPayload();
            if (payload != null) {
                os = httpUrlConnection.getOutputStream();
                os.write(payload);
                os.flush();
                os.close();
            }

            // Read code & body
            int code = httpUrlConnection.getResponseCode();
            is = code < 400 ? httpUrlConnection.getInputStream() : httpUrlConnection.getErrorStream();
            byte[] body = IOUtilsLite.toByteArray(is);

            // Read headers
            Map<String, List<String>> headers = new HashMap<>();
            if (httpUrlConnection.getHeaderFields() != null) {
                headers.putAll(httpUrlConnection.getHeaderFields());
            }

            // Create response
            return new Response(restClient.getUrl(), restClient.getCharset(), code, body, headers);

        } catch (Exception e) {
            return new Response(restClient.getUrl(), e);
        } finally {
            IOUtilsLite.closeQuietly(is);
            IOUtilsLite.closeQuietly(os);
        }
    }

    protected HttpURLConnection getUrlConnection(RestClient restClient) throws Exception {
        // Cache
        if (restClient.getHttpCacheDir() != null && HttpResponseCache.getInstalled() == null) {
            HttpResponseCache.install(new File(restClient.getHttpCacheDir(), "urlconnection"), RestClient.DEFAULT_CACHE_SIZE);
        }

        // Connection
        HttpURLConnection httpUrlConnection = (HttpURLConnection) (new URL(restClient.getUrl()).openConnection());

        // Set timeout, cache, method
        httpUrlConnection.setConnectTimeout(restClient.getTimeoutConnect());
        httpUrlConnection.setReadTimeout(restClient.getTimeoutRead());
        httpUrlConnection.setUseCaches(restClient.getHttpCacheDir() != null);
        httpUrlConnection.setRequestMethod(restClient.getMethod().name());

        // Add headers
        for (Entry<String, List<String>> entry : restClient.getHeaders().entrySet()) {
            for (String header : entry.getValue()) {
                httpUrlConnection.addRequestProperty(entry.getKey(), header);
            }
        }
        return httpUrlConnection;
    }
}
