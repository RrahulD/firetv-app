/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.threading;

import android.os.AsyncTask;

/**
 * Simple AsyncTask subclass, implementing the Cancellable interface.
 *
 * @deprecated see https://kotlinlang.org/docs/reference/coroutines-overview.html
 */
@Deprecated
public abstract class CancellableAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> implements Cancellable {
    @Override
    public void cancel() {
        cancel(true);
    }
}
