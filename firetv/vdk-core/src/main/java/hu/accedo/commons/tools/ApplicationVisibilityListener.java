/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

public abstract class ApplicationVisibilityListener implements Application.ActivityLifecycleCallbacks {
    private static final long LEAVE_TIMEOUT = 100;

    private int activityCount;
    private boolean isStarted = false;
    private long leaveTimeout = LEAVE_TIMEOUT;

    private Handler handler = new Handler();
    private Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            isStarted = false;
            onApplicationHidden();
        }
    };

    /**
     * Initializes with the default leave timeout that is 100 ms
     */
    public ApplicationVisibilityListener() {
    }

    /**
     * @param leaveTimeout defines how long the listener should wait before calling onApplicationHidden(),
     *                     after the last visible Activity has stopped.
     */
    public ApplicationVisibilityListener(long leaveTimeout) {
        this.leaveTimeout = leaveTimeout;
    }

    /**
     * Called when the first Activity of the application started. (When the application is brought to the foreground)
     */
    public abstract void onApplicationVisible();

    /**
     * Called when the last visible Activity of the application stopped. (When the application is sent to the background)
     */
    public abstract void onApplicationHidden();

    @Override
    public void onActivityStarted(Activity activity) {
        if (activityCount == 0) {
            handler.removeCallbacks(stopRunnable);
            if (!isStarted) {
                onApplicationVisible();
                isStarted = true;
            }
        }
        activityCount++;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCount--;
        if (activityCount == 0) {
            handler.postDelayed(stopRunnable, leaveTimeout);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
