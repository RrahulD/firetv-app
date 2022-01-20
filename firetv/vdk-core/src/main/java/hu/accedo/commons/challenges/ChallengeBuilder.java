/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.challenges;

import android.content.Context;

import java.util.ArrayList;

import hu.accedo.commons.challenges.Challenge.ChallengeCallback;

/**
 * Runs a chain of Challenge-s, as a chain of callbacks.
 * <p>
 * For example, let's say playback requires login and parental check:
 * new ChallengeBuilder().addChallenge(new LoginChallenge()).addChallenge(new ParentalChallenge()).setOnPassedListener(onPassedListenerPlay).run();
 * <p>
 * Here prentalChallenge will only be called if loginChallenge passed, and onPassedListenerPlay will only be called if both challenges have passed.
 */
public class ChallengeBuilder {
    private ArrayList<Challenge> challengeList = new ArrayList<>();
    private OnPassedListener onPassedListener;
    private OnFailedListener onFailedListener;
    private Context context;

    /**
     * @param context can be useful for fragment operations, and showing dialogs. May be null if your Challenges don't use it, but not recommended.
     */
    public ChallengeBuilder(Context context) {
        this.context = context;
    }

    /**
     * @param onPassedListener callback to be invoked when all challenges have been passed.
     * @return this builder instance for chaining.
     */
    public ChallengeBuilder setOnPassedListener(OnPassedListener onPassedListener) {
        this.onPassedListener = onPassedListener;
        return this;
    }

    /**
     * @param onFailedListener callback to be invoked when a challenge has failed, and thus the challenge-chain has broken.
     * @return this builder instance for chaining.
     */
    public ChallengeBuilder setOnFailedListener(OnFailedListener onFailedListener) {
        this.onFailedListener = onFailedListener;
        return this;
    }

    /**
     * @param challenge the challenge to be added to the challenge chain.
     * @return this builder instance for chaining.
     */
    public ChallengeBuilder addChallenge(Challenge challenge) {
        if (challenge != null) {
            challengeList.add(challenge);
        }
        return this;
    }

    /**
     * Starts executing the challenge chain, up until all have been passed, or one of them fails, thus breaking the chain.
     */
    public void run() {
        run(0);
    }

    private void run(final int index) {
        // Exit condition
        if (index >= challengeList.size()) {
            if (onPassedListener != null) {
                onPassedListener.challengesPassed();
            }
            return;
        }

        // Run given challenge
        final Challenge challenge = challengeList.get(index);
        challenge.runChallenge(context, new ChallengeCallback() {
            @Override
            public void pass() {
                run(index + 1);
            }

            @Override
            public void fail(Object reason) {
                if (onFailedListener != null) {
                    onFailedListener.challengeFailed(challenge, reason);
                }
            }
        });
    }

    /**
     * Callback to be invoked when all challenges have been passed.
     */
    public static interface OnPassedListener {
        public void challengesPassed();
    }

    /**
     * Callback to be invoked when a challenge has failed, and thus the challenge-chain has broken.
     */
    public static interface OnFailedListener {
        /**
         * @param challenge the challenge that failed
         * @param reason    optional object holding information about the fail. May be null.
         */
        public void challengeFailed(Challenge challenge, Object reason);
    }
}
