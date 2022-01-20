/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

/**
 * Generic callback, that can be used for various async operations.
 */
public interface Callback<T> {
    public void execute(T result);
}
