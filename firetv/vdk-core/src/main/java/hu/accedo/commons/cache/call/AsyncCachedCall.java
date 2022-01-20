/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.cache.call;

import hu.accedo.commons.threading.Cancellable;
import hu.accedo.commons.tools.Callback;

/**
 * Provides a decorator around any arbitrary ASYNC method call, caching whatever it returns to the onSuccess call, by the key provided.
 * If you want to cache async calls without an onFailure clause, simply use null as the onFailure callback.
 */
public abstract class AsyncCachedCall<T, E extends Exception> extends BaseCachedCall<T, E> {
    private Callback<T> onSuccess;
    private Callback<E> onFailure;
    private long expiration = defaultExpiration;

    /**
     * Sets the expiration explicitly for this one call. The items will be returned based on when they were put in the cache like:
     * {@code dateAdded + expiration > currentTime --> return from cache}
     * <p>
     * -1 (default) always returns the cached item
     * 0 always refetches and recaches
     * higher than 0 returns based on the formula above
     *
     * @param expiration the amount of milliseconds for how long the data will be cached
     * @return this instance for method chaining
     */
    public AsyncCachedCall<T, E> setExpiration(long expiration) {
        this.expiration = expiration;
        return this;
    }

    /**
     * @param key       if the key is null, the caching will be ignored
     * @param onSuccess provide your original success callbacks here.
     * @param onFailure provide your original failure callback here.
     */
    public AsyncCachedCall(Object key, Callback<T> onSuccess, Callback<E> onFailure) {
        super(key);
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    /**
     * Override this method, and call your async call inside it, that you want to cache the results of.
     *
     * @param onSuccess make sure to use this success callback in your call.
     * @param onFailure make sure to use this failure callback in your call.
     * @return the Cancellable used for threading in the call.
     */
    protected abstract Cancellable call(Callback<T> onSuccess, Callback<E> onFailure);

    /**
     * Executes the task, and calls the provided onSuccess callback either the results of the call, or a cached version of it, if available.
     *
     * @return the Cancellable used for threading in the call
     */
    public Cancellable execute() {
        // Keycheck
        if (key == null) {
            return call(onSuccess, onFailure);
        }

        // Try from cache
        T result = get(key, expiration);
        if (result != null) {
            if (onSuccess != null) {
                onSuccess.execute(result);
            }
            return null;
        }

        // Fetch and put
        return call(new Callback<T>() {
            @Override
            public void execute(T result) {
                put(key, result);

                if (onSuccess != null) {
                    onSuccess.execute(result);
                }
            }
        }, onFailure);
    }
}
