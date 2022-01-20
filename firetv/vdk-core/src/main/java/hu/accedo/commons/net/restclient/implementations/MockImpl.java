package hu.accedo.commons.net.restclient.implementations;

import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import hu.accedo.commons.net.restclient.Response;
import hu.accedo.commons.net.restclient.RestClient;

/**
 * A mock implementation for RestClient that may be used for testing, or no-network cases.
 */
public class MockImpl implements Implementation {
    protected int code = -1;
    protected byte[] body;
    protected Map<String, List<String>> headers = new HashMap<>();
    protected Exception exception;
    protected Pair<Integer, Integer> duration;

    @NonNull
    @Override
    public Response fetchResponse(@NonNull RestClient restClient) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new NetworkOnMainThreadException();
        }

        if (duration != null && duration.first < duration.second) {
            try {
                Thread.sleep(duration.first + new Random().nextInt((int) (duration.second - duration.first)));
            } catch (InterruptedException ignored) {
            }
        }

        if (exception != null) {
            return new Response(restClient.getUrl(), exception);
        } else {
            return new Response(restClient.getUrl(), restClient.getCharset(), code, body, headers);
        }
    }

    public MockImpl setCode(int code) {
        this.code = code;
        return this;
    }

    public MockImpl setBody(byte[] body) {
        this.body = body;
        return this;
    }

    public MockImpl setBody(String body) {
        this.body = body.getBytes();
        return this;
    }

    public MockImpl setHeaders(Map<String, List<String>> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
        return this;
    }

    public MockImpl setHeader(@NonNull String key, @Nullable String value) {
        headers.remove(key);
        return addHeader(key, value);
    }

    public MockImpl addHeader(@NonNull String key, @Nullable String value) {
        if (!headers.containsKey(key)) {
            headers.put(key, new ArrayList<String>());
        }
        headers.get(key).add(value);
        return this;
    }

    public MockImpl setException(Exception exception) {
        this.exception = exception;
        return this;
    }

    public void setDuration(int minimumDurationMillis, int maximumDurationMillis) {
        this.duration = Pair.create(minimumDurationMillis, maximumDurationMillis);
    }
}
