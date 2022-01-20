/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.cache.call;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.os.SystemClock;
import android.util.LruCache;
import android.util.Pair;

import hu.accedo.commons.logging.L;

/**
 * Base class for CachedCall and AsyncCachedCall, storing the shared LruCache instance that they use.
 */
abstract class BaseCachedCall<T, E extends Exception> {
    protected static final String TAG = "CachedCall";

    protected static final LruCache<Object, Pair<Object, Long>> lruCache = new LruCache<>(300);

    protected static long defaultExpiration = -1;

    /**
     * Calls resize(size) on the lruCache backing CachedCall and AsyncCachedCall.
     * If you intend to use this, do so at either service creation, or in the onCreate of your Application.
     *
     * @param size the number of items that should be stored in the cache.
     */
    @TargetApi(VERSION_CODES.LOLLIPOP)
    public static void setCacheSize(int size) {
        lruCache.resize(size);
    }

    /**
     * Overrides the default expiration of -1, which means never expires for cached calls.
     * If you intend to use this, do so at either service creation, or in the onCreate of your Application.
     *
     * @param defaultExpiration the default expiration time, if no expiration is specified for cachedcalls.
     */
    public static void setDefaultExpiration(long defaultExpiration) {
        BaseCachedCall.defaultExpiration = defaultExpiration;
    }

    protected Object key;

    public BaseCachedCall(Object key) {
        this.key = key;
    }

    protected boolean put(Object key, Object value) {
        if (key == null) {
            return false; // Cache switched off
        }
        if (value == null) {
            L.i(TAG, "Result null, not caching: " + key);
            return false;
        }

        lruCache.put(key, Pair.create((Object) value, SystemClock.elapsedRealtime()));
        L.i(TAG, "Caching: " + key);

        return true;
    }

    protected T get(Object key, long expiration) {
        try {
            Pair<Object, Long> cacheItem = lruCache.get(key);

            if (cacheItem != null) {
                if (expiration < 0 || cacheItem.second + expiration > SystemClock.elapsedRealtime()) {
                    T result = (T) cacheItem.first;
                    L.i(TAG, "Returning from cache: " + key);
                    return result;
                } else {
                    L.i(TAG, "Cache entry expired for: " + key);
                    return null;
                }
            }

        } catch (ClassCastException e) {
            L.w(TAG, e);
        }

        return null;
    }
}
