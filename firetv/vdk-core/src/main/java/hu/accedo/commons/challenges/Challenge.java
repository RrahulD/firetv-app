/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.challenges;

import android.content.Context;

/**
 * Defines a challenge, that can be chained using a ChallengeBuilder.
 */
public interface Challenge {
    /**
     * @param context           the context provided to the ChallengeBuilder.
     * @param challengeCallback must be called when the challenge has passed, or failed.
     */
    public void runChallenge(Context context, ChallengeCallback challengeCallback);

    /**
     * Callback to be invoked when the challenge has been passed, or failed.
     */
    public static interface ChallengeCallback {
        /**
         * Must be called when the challenge has been passed.
         */
        public void pass();

        /**
         * Mest be called when the challenge has been failed.
         *
         * @param reason optional object holding information about the fail. May be null.
         */
        public void fail(Object reason);
    }
}
