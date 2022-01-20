/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.cache.call;

/**
 * Provides a decorator around any arbitrary SYNC method call, caching it's return value by the key provided.
 * If you want to cache non-exception-throwing calls, simply set the generic E parameter as RuntimeException.
 */
public abstract class CachedCall<T, E extends Exception> extends BaseCachedCall<T, E> {
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
    public CachedCall<T, E> setExpiration(long expiration) {
        this.expiration = expiration;
        return this;
    }

    /**
     * @param key if the key is null, caching will be ignored
     */
    public CachedCall(Object key) {
        super(key);
    }

    /**
     * Override this method, and make it do whatever you want to cache the results of.
     *
     * @return the value that will be cached for the given key.
     * @throws E nothing will be cached, if your call throws.
     */
    protected abstract T call() throws E;

    /**
     * Executes the task wrapped by this CachedCall.
     *
     * @return either the results of the call, or a cached version of it, if available.
     * @throws E may be thrown by the call wrapped.
     */
    public T execute() throws E {
        // Keycheck
        if (key == null) {
            return call();
        }

        // Try from cache
        T result = get(key, expiration);
        if (result != null) {
            return result;
        }

        // Fetch and put
        result = call();
        put(key, result);
        return result;
    }
}
