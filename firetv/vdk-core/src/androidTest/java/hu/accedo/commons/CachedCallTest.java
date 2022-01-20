/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import hu.accedo.commons.cache.call.AsyncCachedCall;
import hu.accedo.commons.cache.call.CachedCall;
import hu.accedo.commons.threading.Cancellable;
import hu.accedo.commons.tools.Callback;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CachedCallTest {
    private Random random = new Random();

    @Test
    public void testEquals() {
        int A1 = new CachedCall<Integer, RuntimeException>("A") {
            @Override
            public Integer call() throws RuntimeException {
                return random.nextInt();
            }
        }.execute();

        int A2 = new CachedCall<Integer, RuntimeException>("A") {
            @Override
            public Integer call() throws RuntimeException {
                return random.nextInt();
            }
        }.execute();

        assertEquals(A1, A2);
    }

    @Test
    public void testNotEquals() {
        int A = new CachedCall<Integer, RuntimeException>("A") {
            @Override
            public Integer call() throws RuntimeException {
                return random.nextInt();
            }
        }.execute();

        int B = new CachedCall<Integer, RuntimeException>("B") {
            @Override
            public Integer call() throws RuntimeException {
                return random.nextInt();
            }
        }.execute();

        assertTrue(A != B);
    }

    @Test
    public void testAsyncEquals() {
        new AsyncCachedIntegerCall("A", new Callback<Integer>() {
            @Override
            public void execute(final Integer result1) {
                new AsyncCachedIntegerCall("B", new Callback<Integer>() {
                    @Override
                    public void execute(Integer result2) {
                        Assert.assertNotEquals(result1, result2);
                    }
                }, null).execute();
            }
        }, null).execute();
    }

    @Test
    public void testAsyncNotEquals() {
        new AsyncCachedIntegerCall("A", new Callback<Integer>() {
            @Override
            public void execute(final Integer result1) {
                new AsyncCachedIntegerCall("A", new Callback<Integer>() {
                    @Override
                    public void execute(Integer result2) {
                        assertSame(result1, result2);
                    }
                }, null).execute();
            }
        }, null).execute();
    }

    @Test
    public void testNotExpired() {
        int A1 = new CachedCall<Integer, RuntimeException>("A") {
            @Override
            public Integer call() throws RuntimeException {
                return random.nextInt();
            }
        }.setExpiration(1000).execute();

        int A2 = new CachedCall<Integer, RuntimeException>("A") {
            @Override
            public Integer call() throws RuntimeException {
                return random.nextInt();
            }
        }.setExpiration(1000).execute();

        assertEquals(A1, A2);
    }

    @Test
    public void testExpired() throws InterruptedException {
        int A1 = new CachedCall<Integer, RuntimeException>("A") {
            @Override
            public Integer call() throws RuntimeException {
                return random.nextInt();
            }
        }.setExpiration(10).execute();

        Thread.sleep(11);

        int A2 = new CachedCall<Integer, RuntimeException>("A") {
            @Override
            public Integer call() throws RuntimeException {
                return random.nextInt();
            }
        }.setExpiration(10).execute();

        assertTrue(A1 != A2);
    }

    /**
     * Convenence call for the tests, that always just returns a random number, in an async manner.
     */
    private class AsyncCachedIntegerCall extends AsyncCachedCall<Integer, RuntimeException> {
        public AsyncCachedIntegerCall(Object key, Callback<Integer> onSuccess, Callback<RuntimeException> onFailure) {
            super(key, onSuccess, onFailure);
        }

        @Override
        protected Cancellable call(Callback<Integer> onSuccess, Callback<RuntimeException> onFailure) {
            onSuccess.execute(random.nextInt());
            return null;
        }
    }
}
