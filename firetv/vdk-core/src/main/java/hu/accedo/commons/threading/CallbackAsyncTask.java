/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.threading;

import hu.accedo.commons.tools.Callback;

/**
 * A subclass of SafeAsyncTask, that returns the results of the "Result call(Params... params) throws Exception"
 * method in the provided onSuccess callback, or returns any Exception thrown in the provided onFailure callback.
 *
 * @deprecated see https://kotlinlang.org/docs/reference/coroutines-overview.html
 */
@Deprecated
public abstract class CallbackAsyncTask<Result, E extends Exception> extends SafeAsyncTask<Void, Void, Result> {
    private Callback<Result> onSuccess;
    private Callback<E> onFailure;

    public CallbackAsyncTask(Callback<Result> onSuccess, Callback<E> onFailure) {
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    @Override
    public void onSuccess(Result result) {
        if (onSuccess != null) {
            onSuccess.execute(result);
        }
    }

    @Override
    public void onFailure(Exception caughtException) {
        try {
            E e = (E) caughtException;
            if (onFailure != null) {
                onFailure.execute(e);
            }
        } catch (ClassCastException classCastException) {
            throw new RuntimeException("This should not happen, and if it does, that's a bug in the caller that should be fixed.", caughtException);
        }
    }
}