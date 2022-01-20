/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @param <I> the input type
 * @param <O> the output type
 * @param <E> the error type
 */
public interface ThrowingParser<I, O, E extends Exception> {
    /**
     * Parses input, into the type O, or throws an E exception if any error occures.
     *
     * @param input the input to parse.
     * @return the parsed output of type O.
     * @throws E if any error happens during parsing.
     */
    @NonNull
    O parse(@Nullable I input) throws E;
}
