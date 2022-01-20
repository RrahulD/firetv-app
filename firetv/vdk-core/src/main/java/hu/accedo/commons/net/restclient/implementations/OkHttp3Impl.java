package hu.accedo.commons.net.restclient.implementations;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import hu.accedo.commons.net.restclient.Response;
import hu.accedo.commons.net.restclient.RestClient;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;

/**
 * Default RestClient implementation that uses OkHttp3.
 */
public class OkHttp3Impl implements Implementation {
    @NonNull
    @Override
    public Response fetchResponse(@NonNull RestClient restClient) {
        try {
            // Create request
            byte[] payload = restClient.getPayload();
            if (HttpMethod.permitsRequestBody(restClient.getMethod().name()) && payload == null) {
                payload = "".getBytes(restClient.getCharset()); // Just to match UrlConnection behavior
            }
            RequestBody requestBody = payload != null ? RequestBody.create(null, payload) : null;
            Request.Builder requestBuilder = new Request.Builder()
                    .url(restClient.getUrl())
                    .method(restClient.getMethod().name(), requestBody);

            // Add headers
            for (Entry<String, List<String>> entry : restClient.getHeaders().entrySet()) {
                for (String header : entry.getValue()) {
                    requestBuilder.addHeader(entry.getKey(), header);
                }
            }

            // Do Request, build response
            okhttp3.Response response = getOkHttpClientBuilder(restClient).build().newCall(requestBuilder.build()).execute();
            return new Response(
                    restClient.getUrl(),
                    restClient.getCharset(),
                    response.code(),
                    response.body() != null ? response.body().bytes() : null,
                    response.headers().toMultimap());

        } catch (Exception e) {
            return new Response(restClient.getUrl(), e);
        }
    }

    protected OkHttpClient.Builder getOkHttpClientBuilder(RestClient restClient) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // Cache
        if (restClient.getHttpCacheDir() != null) {
            builder.cache(new Cache(new File(restClient.getHttpCacheDir(), "okhttp3"), RestClient.DEFAULT_CACHE_SIZE));
        }

        // Timeouts
        builder.connectTimeout(restClient.getTimeoutConnect(), TimeUnit.MILLISECONDS);
        builder.readTimeout(restClient.getTimeoutRead(), TimeUnit.MILLISECONDS);

        return builder;
    }
}
