/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

import java.util.Date;
import java.util.TimeZone;

/**
 * Utility methods for converting UTC to local time.
 */
public final class DateTools {
    public static final long ONE_DAY_MILLIS = 1000 * 60 * 60 * 24;
    public static final long ONE_HOUR_MILLIS = 1000 * 60 * 60;
    public static final long ONE_MINUTE_MILLIS = 1000 * 60;

    /**
     * Takes the modulates the current time by 24 hours, and shifts it with as many days as specified.
     *
     * @param dayOffset 0 means today, -1 is yesterday, +1 is tomorrow, and so on.
     * @return timestamp - timestamp%ONE_DAY_MILLIS + ONE_DAY_MILLIS*dayOffset
     */
    public static long getStartOfDayUTC(int dayOffset) {
        long timestamp = System.currentTimeMillis();
        return timestamp - timestamp % ONE_DAY_MILLIS + ONE_DAY_MILLIS * dayOffset;
    }

    /**
     * Returns the timezone offset for the device's default timezone. The input is required for deciding if we're in daylight savings or no.
     *
     * @param input the Date instance to calculate the timezone offset of.
     * @return the timezone offset in milliseconds.
     */
    public static long getTimezoneOffset(Date input) {
        boolean isInDaylightTime = TimeZone.getDefault().inDaylightTime(input);
        return TimeZone.getDefault().getRawOffset() + (isInDaylightTime ? ONE_HOUR_MILLIS : 0);
    }
}
