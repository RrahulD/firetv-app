/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.net.mock;

import android.os.AsyncTask;

import com.google.gson.reflect.TypeToken;

import hu.accedo.commons.net.Parser;
import hu.accedo.commons.threading.CancellableAsyncTask;
import hu.accedo.commons.tools.Callback;

/**
 * @deprecated use {@link hu.accedo.commons.net.restclient.implementations.MockImpl} instead.
 */
@Deprecated
public class AsyncMockClient {
    private MockClient mockClient;

    public AsyncMockClient(MockClient mockClient) {
        this.mockClient = mockClient;
    }

    /**
     * Reads a mock file in an async manner, returning the contents of the mock file specified in the constructor, in the specified callback.
     *
     * @param callback called when the given file has been read as string.
     * @return the asynctask doing the background work, used for cancelling
     */
    public CancellableAsyncTask<Void, Void, String> readMockFileAsync(final Callback<String> callback) {
        CancellableAsyncTask<Void, Void, String> asyncTask = new CancellableAsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return mockClient.readMockFile();
            }

            @Override
            protected void onPostExecute(String result) {
                callback.execute(result);
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return asyncTask;
    }


    /**
     * Reads a mock file in an async manner, returning the parsed contents of the mock file specified in the constructor, in the specified callback.
     *
     * @param parser   the parser to use to parse the contents of the file
     * @param callback called when the given file has been read and parsed
     * @param <T>      the type the parser will parse in
     * @return the asynctask doing the background work, used for cancelling
     */
    public <T> AsyncTask<Void, Void, T> readParseMockFileAsync(final Parser<String, T> parser, final Callback<T> callback) {
        AsyncTask<Void, Void, T> asyncTask = new AsyncTask<Void, Void, T>() {
            @Override
            protected T doInBackground(Void... params) {
                return parser.parse(mockClient.readMockFile());
            }

            @Override
            protected void onPostExecute(T result) {
                callback.execute(result);
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return asyncTask;
    }

    /**
     * Reads a mock file in an async manner, returning the GSON parsed contents of the mock file specified in the constructor, in the specified callback.
     *
     * @param typeToken the GSON typetoken to use for parsing
     * @param callback  called when the given file has been read and parsed
     * @param <T>       the type of the typeToken
     * @return the asynctask doing the background work, used for cancelling
     */
    public <T> CancellableAsyncTask<Void, Void, T> readGsonParseMockFileAsync(final TypeToken<T> typeToken, final Callback<T> callback) {
        CancellableAsyncTask<Void, Void, T> asyncTask = new CancellableAsyncTask<Void, Void, T>() {
            @Override
            protected T doInBackground(Void... params) {
                return mockClient.readGsonParseMockFile(typeToken);
            }

            @Override
            protected void onPostExecute(T result) {
                callback.execute(result);
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return asyncTask;
    }
}
