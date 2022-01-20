/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

import android.app.Application;
import android.content.Context;

public class VdkApplication extends Application {
    private static Context mContext;
    private int activityCount;
    private long startTime;

    /**
     * Should NOT be used for inflating UI, showing Toast messages, registering from broadcast events.. etc.
     *
     * @return the context of the single, global Application object of the current process, that was stored in Application.onCreate().
     */
    public static Context getContext() {
        return mContext;
    }

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        registerActivityLifecycleCallbacks(new ApplicationVisibilityListener() {
            @Override
            public void onApplicationVisible() {
                startTime = System.currentTimeMillis();
                onStart();
            }

            @Override
            public void onApplicationHidden() {
                long now = System.currentTimeMillis();
                onStop(0 < startTime && startTime < now ? now - startTime : 0);
                onStop();
            }
        });
    }

    /**
     * Called when the first Activity of the application is started
     */
    protected void onStart() {

    }

    /**
     * Called when all of the Activities of the application are stopped
     */
    protected void onStop() {

    }

    /**
     * Called when all of the Activities of the application are stopped
     *
     * @param retentionMillis the amount of time spent inside the application
     */
    protected void onStop(long retentionMillis) {

    }
}
