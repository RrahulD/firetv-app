/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.threading;

import android.os.AsyncTask;

import java.util.concurrent.Executor;

/**
 * A special, Exception safe AsyncTask.
 * <p>
 * The results of background exceution are returned in the onSuccess callback,
 * while any exceptions thrown are returned in the onFailure callback.
 *
 * @deprecated see https://kotlinlang.org/docs/reference/coroutines-overview.html
 */
@Deprecated
public abstract class SafeAsyncTask<Params, Progress, Result> implements Cancellable {
    private AsyncTask<Params, Progress, Result> asyncTask;

    @Override
    public void cancel() {
        if (asyncTask != null) {
            asyncTask.cancel(true);
            asyncTask = null;
        }
    }

    public SafeAsyncTask<Params, Progress, Result> executeAndReturn(Params... params) {
        return executeAndReturn(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    public SafeAsyncTask<Params, Progress, Result> executeAndReturn(Executor executor, Params... params) {
        cancel();
        asyncTask = new AsyncTask<Params, Progress, Result>() {
            private Exception caughtException;

            @Override
            protected Result doInBackground(final Params... params) {
                try {
                    return call(params);
                } catch (Exception e) {
                    caughtException = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final Result result) {
                if (caughtException != null) {
                    onFailure(caughtException);
                } else {
                    onSuccess(result);
                }
            }

            //Wrap asynctask calls
            @Override
            protected void onCancelled() {
                SafeAsyncTask.this.onCancelled();
            }

            @Override
            protected void onCancelled(Result result) {
                SafeAsyncTask.this.onCancelled(result);
            }

            @Override
            protected void onPreExecute() {
                SafeAsyncTask.this.onPreExecute();
            }

            @Override
            protected void onProgressUpdate(Progress... values) {
                SafeAsyncTask.this.onProgressUpdate(values);
            }
        };
        asyncTask.executeOnExecutor(executor, params);
        return this;
    }

    /*
     * --------------- Asynctask wrapper methods ---------------
     */

    /**
     * Runs on the UI thread before {@link #call}.
     *
     * @see #call
     */
    protected void onPreExecute() {
    }

    /**
     * Runs on the UI thread after publishProgress() is invoked.
     * The specified values are the values passed to publishProgress().
     *
     * @param values The values indicating progress.
     * @see #call
     */
    protected void onProgressUpdate(Progress... values) {
    }

    /**
     * @return Returns the value of asyncTask.isCancelled(), or true if asyncTask is null.
     */
    protected boolean isCancelled() {
        return asyncTask != null ? asyncTask.isCancelled() : true;
    }

    /**
     * Applications should preferably override {@link #onCancelled}.
     * <p>
     * Runs on the UI thread after {@link #cancel()} is invoked and {@link #call} has finished.
     *
     * @see #onCancelled
     * @see #cancel
     * @see #isCancelled
     */
    protected void onCancelled() {
    }

    /**
     * Runs on the UI thread after cancel(boolean) is invoked and call(Params...) has finished.
     *
     * @param result The result, if any, computed in call(Params...), can be null
     * @see #cancel
     * @see #isCancelled
     */
    protected void onCancelled(Result result) {
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #executeAndReturn} by the caller of this task.
     * <p>
     * This method can call #publishProgress to publish updates on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @throws Exception any exception thrown by the background task.
     * @see #onPreExecute
     * @see #onSuccess
     * @see #onFailure
     */
    public abstract Result call(Params... params) throws Exception;

    /**
     * Runs on the UI thread after {@link #call}. The
     * specified result is the value returned by {@link #call}.
     * <p>
     * This method won't be invoked if the task was cancelled.
     *
     * @param result The result of the operation computed by {@link #call}.
     * @see #onPreExecute
     * @see #call
     * @see #onCancelled
     */
    public void onSuccess(Result result) {
    }

    /**
     * Runs on the UI thread after {@link #call}. The
     * specified result is the exception thrown by {@link #call}.
     * <p>
     * This method won't be invoked if the task was cancelled.
     *
     * @param caughtException The exception thrown of the operation {@link #call}.
     * @see #onPreExecute
     * @see #call
     * @see #onCancelled
     */
    public void onFailure(Exception caughtException) {
    }
}
