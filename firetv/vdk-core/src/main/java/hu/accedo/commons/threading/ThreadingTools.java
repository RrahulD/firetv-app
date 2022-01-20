/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.threading;

import android.os.AsyncTask;

import java.util.concurrent.FutureTask;

/**
 * @deprecated see https://kotlinlang.org/docs/reference/coroutines-overview.html
 */
@Deprecated
public class ThreadingTools {
    /**
     * Tries to cancel the object, whatever it is. Generally used to cancel running background operations.
     *
     * @param object may be one of the following: AsyncTask, SafeAsyncTask, Cancellable, FutureTask
     * @return true, if cancelling has succeeded, false otherwise.
     */
    @SuppressWarnings("rawtypes")
    public static boolean tryCancel(Object object) {
        if (object == null) {
            return true;
        }

        if (object instanceof AsyncTask) {
            ((AsyncTask) object).cancel(true);
            return true;
        } else if (object instanceof SafeAsyncTask) {
            ((Cancellable) object).cancel();
            return true;
        } else if (object instanceof Cancellable) {
            ((Cancellable) object).cancel();
            return true;
        } else if (object instanceof FutureTask) {
            ((FutureTask) object).cancel(true);
            return true;
        }
        return false;
    }

    /**
     * Calls ThreadingTools.tryCancel(object) on all its given parameters
     *
     * @param objects a list of objects like: AsyncTask, SafeAsyncTask, Cancellable, FutureTask
     */
    public static void tryCancelAll(Object... objects) {
        if (objects != null) {
            for (Object object : objects) {
                ThreadingTools.tryCancel(object);
            }
        }
    }
}
