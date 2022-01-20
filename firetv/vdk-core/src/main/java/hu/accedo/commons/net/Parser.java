/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @deprecated use {@link ThrowingParser} instead.
 */
@Deprecated
public interface Parser<I, O> {
    /**
     * Parses input, into the type O
     *
     * @param input the input to parse.
     * @return the parsed output of type O.
     */
    @NonNull
    O parse(@Nullable I input);
}
