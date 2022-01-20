/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;

public class Shared {
    public static Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getContext();
    }
}
