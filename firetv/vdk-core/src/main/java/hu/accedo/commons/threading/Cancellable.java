/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.threading;

/**
 * Generic interface defined for cancellable operations, such as AsyncTasks, SafeAsyncTasks, CancellableAsyncTasks.
 *
 * @deprecated see https://kotlinlang.org/docs/reference/coroutines-overview.html
 */
@Deprecated
public interface Cancellable {
    public void cancel();
}
