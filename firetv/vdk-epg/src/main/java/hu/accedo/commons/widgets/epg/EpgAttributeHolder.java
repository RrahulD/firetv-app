/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.widgets.epg;

import static hu.accedo.commons.tools.DateTools.ONE_DAY_MILLIS;
import static hu.accedo.commons.tools.DateTools.ONE_HOUR_MILLIS;
import static hu.accedo.commons.tools.DateTools.ONE_MINUTE_MILLIS;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import java.util.Date;
import java.util.TimeZone;

public class EpgAttributeHolder {
    protected EpgView parent;

    // Dimensions
    protected int channelsWidth;
    protected int timebarHeight;
    protected int timebarOffsetHorizontal;
    protected int minuteWidth;
    protected int hairlineTextWidth;
    protected int rowHeight;
    protected int extraPaddingBottom;

    // Paging
    protected int pageSizeVertical;
    protected PageSizeHorizontal pageSizeHorizontal;

    // Empty awareness & progress
    protected int progressViewReference;
    protected int emptyViewReference;
    protected boolean secondaryProgressEnabled;

    // Placeholders
    protected boolean placeholdersEnabled;
    protected boolean placeholdersOnFirstLoadEnabled;
    protected PageSizeHorizontal placeholdersPageSize;

    // Config
    protected int timebarMinuteStepping;
    protected int hourOffset;
    protected int minuteOffset;
    protected int daysForward;
    protected int daysBackwards;
    protected int updateFrequencySeconds;
    protected int viewCacheSize;
    protected boolean stickyProgramsEnabled;
    protected boolean timebarLabelEnabled;
    protected boolean viewClippingEnabled;
    protected long startMillisUtc;
    protected long endMillisUtc;
    protected long timezoneOffset;
    protected boolean timezoneOverwritten;
    protected boolean loopingEnabled;
    protected boolean threadSafe;

    // Diagonal scroll
    protected boolean diagonalScrollEnabled;
    protected int diagonalScrollDetectionOffset;

    // Derived values
    protected int totalDayCount;
    protected int dayWidthPixels;
    protected long firstDayStartMillisUTC;
    protected long lastDayEndMillisUTC;
    protected long serverTimeDifference = System.currentTimeMillis() - SystemClock.elapsedRealtime();

    public EpgAttributeHolder(@NonNull EpgView parent, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        Resources res = parent.getContext().getResources();
        this.parent = parent;

        // Load defaults
        this.channelsWidth = res.getDimensionPixelSize(R.dimen.epg_default_channels_width);
        this.timebarHeight = res.getDimensionPixelSize(R.dimen.epg_default_timebar_height);
        this.timebarOffsetHorizontal = res.getDimensionPixelSize(R.dimen.epg_default_timebar_offset_horizontal);
        this.timebarMinuteStepping = 30;
        this.hairlineTextWidth = res.getDimensionPixelSize(R.dimen.epg_default_hairline_text_width);
        this.minuteWidth = res.getDimensionPixelSize(R.dimen.epg_default_minute_width);
        this.hourOffset = 0;
        this.minuteOffset = 0;
        this.rowHeight = res.getDimensionPixelSize(R.dimen.epg_default_row_height);
        this.extraPaddingBottom = 0;
        this.updateFrequencySeconds = 60;
        this.pageSizeVertical = 20;
        this.pageSizeHorizontal = PageSizeHorizontal.valueOf(24);
        this.viewCacheSize = 2;
        this.progressViewReference = 0;
        this.emptyViewReference = 0;
        this.secondaryProgressEnabled = true;
        this.daysForward = 6;
        this.daysBackwards = 6;
        this.stickyProgramsEnabled = false;
        this.timebarLabelEnabled = false;
        this.viewClippingEnabled = false;
        this.diagonalScrollEnabled = true;
        this.diagonalScrollDetectionOffset = res.getDimensionPixelSize(R.dimen.epg_default_diagonal_scroll_detection_offset);
        this.placeholdersEnabled = false;
        this.placeholdersOnFirstLoadEnabled = true;
        this.placeholdersPageSize = PageSizeHorizontal.valueOf(4);
        this.loopingEnabled = false;
        this.threadSafe = true;

        // Load attrs
        if (attrs != null) {
            TypedArray ta = parent.getContext().obtainStyledAttributes(attrs, R.styleable.EpgView, defStyleAttr, defStyleRes);
            this.channelsWidth = (int) ta.getDimension(R.styleable.EpgView_epgview_channels_width, channelsWidth);
            this.timebarHeight = (int) ta.getDimension(R.styleable.EpgView_epgview_timebar_height, timebarHeight);
            this.timebarOffsetHorizontal = (int) ta.getDimension(R.styleable.EpgView_epgview_timebar_offset_horizontal, timebarOffsetHorizontal);
            this.timebarMinuteStepping = ta.getInteger(R.styleable.EpgView_epgview_timebar_minute_stepping, timebarMinuteStepping);
            this.hairlineTextWidth = (int) ta.getDimension(R.styleable.EpgView_epgview_hairline_text_width, hairlineTextWidth);
            this.minuteWidth = (int) ta.getDimension(R.styleable.EpgView_epgview_minute_width, minuteWidth);
            this.hourOffset = ta.getInteger(R.styleable.EpgView_epgview_hour_offset, hourOffset);
            this.rowHeight = (int) ta.getDimension(R.styleable.EpgView_epgview_row_height, rowHeight);
            this.extraPaddingBottom = (int) ta.getDimension(R.styleable.EpgView_epgview_extra_padding_bottom, extraPaddingBottom);
            this.updateFrequencySeconds = ta.getInteger(R.styleable.EpgView_epgview_update_frequency_seconds, updateFrequencySeconds);
            this.pageSizeVertical = ta.getInteger(R.styleable.EpgView_epgview_page_size_vertical, pageSizeVertical);
            this.pageSizeHorizontal = PageSizeHorizontal.valueOf(ta.getInteger(R.styleable.EpgView_epgview_page_size_horizontal, pageSizeHorizontal.value));
            this.viewCacheSize = ta.getInteger(R.styleable.EpgView_epgview_view_cache_size, viewCacheSize);
            this.progressViewReference = ta.getResourceId(R.styleable.EpgView_epgview_progress_view, progressViewReference);
            this.emptyViewReference = ta.getResourceId(R.styleable.EpgView_epgview_empty_view, emptyViewReference);
            this.secondaryProgressEnabled = ta.getBoolean(R.styleable.EpgView_epgview_secondary_progress_enabled, secondaryProgressEnabled);
            this.daysForward = ta.getInteger(R.styleable.EpgView_epgview_days_forward, daysForward);
            this.daysBackwards = ta.getInteger(R.styleable.EpgView_epgview_days_backwards, daysBackwards);
            this.stickyProgramsEnabled = ta.getBoolean(R.styleable.EpgView_epgview_sticky_programs_enabled, stickyProgramsEnabled);
            this.timebarLabelEnabled = ta.getBoolean(R.styleable.EpgView_epgview_timebar_label_enabled, timebarLabelEnabled);
            this.viewClippingEnabled = ta.getBoolean(R.styleable.EpgView_epgview_view_clipping_enabled, viewClippingEnabled);
            this.diagonalScrollEnabled = ta.getBoolean(R.styleable.EpgView_epgview_diagonal_scroll_enabled, diagonalScrollEnabled);
            this.diagonalScrollDetectionOffset = (int) ta.getDimension(R.styleable.EpgView_epgview_diagonal_scroll_detection_offset, diagonalScrollDetectionOffset);
            this.placeholdersEnabled = ta.getBoolean(R.styleable.EpgView_epgview_placeholders_enabled, placeholdersEnabled);
            this.placeholdersOnFirstLoadEnabled = ta.getBoolean(R.styleable.EpgView_epgview_placeholders_on_firstload_enabled, placeholdersOnFirstLoadEnabled);
            this.placeholdersPageSize = PageSizeHorizontal.valueOf(ta.getInteger(R.styleable.EpgView_epgview_placeholders_page_size, placeholdersPageSize.value));
            this.loopingEnabled = ta.getBoolean(R.styleable.EpgView_epgview_looping_enabled, loopingEnabled);
            this.threadSafe = ta.getBoolean(R.styleable.EpgView_epgview_threadsafe, threadSafe);
            ta.recycle();
        }

        checkValues();
        calculateDerivedValues();
    }

    protected void checkValues() {
        // Dimensions
        assertPositive("channelsWidth", channelsWidth);
        assertPositive("timebarHeight", timebarHeight);
        assertBetween("timebarOffsetHorizontal", timebarOffsetHorizontal, -200, 200); // with ad hoc numbers
        assertPositive("minuteWidth", minuteWidth);
        assertPositive("hairlineTextWidth", hairlineTextWidth);
        assertPositive("rowHeight", rowHeight);
        assertNotNegative("extraPaddingBottom", extraPaddingBottom);

        // Paging
        assertPositive("pageSizeVertical", pageSizeVertical);

        // Config
        assertPositive("timebarMinuteStepping", timebarMinuteStepping);
        assertNotNegative("daysForward", daysForward);
        assertNotNegative("daysBackwards", daysBackwards);
        assertPositive("updateFrequencySeconds", updateFrequencySeconds);
        assertPositive("viewCacheSize", viewCacheSize);
        assertNotNegative("endMillisUtc-startMillisUtc", endMillisUtc - startMillisUtc);

        // Diagonal scroll
        assertPositive("diagonalScrollDetectionOffset", diagonalScrollDetectionOffset);
    }

    protected void assertPositive(String name, long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Parameter " + name + " (" + value + ") must be positive.");
        }
    }

    protected void assertNotNegative(String name, int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Parameter " + name + " (" + value + ") must be non negative.");
        }
    }

    protected void assertNotNegative(String name, long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Parameter " + name + " (" + value + ") must be non negative.");
        }
    }

    protected void assertBetween(String name, long value, int min, int max) {
        if (value < min || max < value) {
            throw new IllegalArgumentException("Parameter " + name + " (" + value + ") must be between " + min + " and " + max);
        }
    }

    protected void calculateDerivedValues() {
        calculateDerivedValues(false);
    }

    protected void calculateDerivedValues(boolean forceUpdateBoundMillisUtc) {
        long now = getServerTime();

        if (!timezoneOverwritten) {
            boolean isInDaylightTime = TimeZone.getDefault().inDaylightTime(new Date(now));
            this.timezoneOffset = TimeZone.getDefault().getRawOffset() + (isInDaylightTime ? ONE_HOUR_MILLIS : 0);
        }

        long localNow = now + timezoneOffset;

        this.dayWidthPixels = 24 * 60 * minuteWidth;
        this.firstDayStartMillisUTC = localNow - localNow % ONE_DAY_MILLIS + ONE_DAY_MILLIS * -daysBackwards - ((now / ONE_DAY_MILLIS) - (localNow / ONE_DAY_MILLIS));
        this.firstDayStartMillisUTC += hourOffset * ONE_HOUR_MILLIS + minuteOffset * ONE_MINUTE_MILLIS - timezoneOffset;

        int correctedDaysBackwards = getDaysBackwards() + ((int) ((now / ONE_DAY_MILLIS) - (localNow / ONE_DAY_MILLIS)));
        int correctedDaysForward = getDaysForward() + ((int) ((localNow / ONE_DAY_MILLIS) - (now / ONE_DAY_MILLIS)));

        this.totalDayCount = correctedDaysBackwards + 1 + correctedDaysForward;

        this.lastDayEndMillisUTC = firstDayStartMillisUTC + totalDayCount * ONE_DAY_MILLIS;

        //Only calculate these if they're not initialised. (No, 0 is not a valid timestamp, I don't care about EPG-s from 1970.01.01)
        if (startMillisUtc == 0 || endMillisUtc == 0 || forceUpdateBoundMillisUtc) {
            this.startMillisUtc = firstDayStartMillisUTC;
            this.endMillisUtc = lastDayEndMillisUTC;
        }
    }

    // Getters
    public int getChannelsWidth() {
        return channelsWidth;
    }

    public int getTimebarHeight() {
        return timebarHeight;
    }

    public int getTimebarOffsetHorizontal() {
        return timebarOffsetHorizontal;
    }

    public int getMinuteWidth() {
        return minuteWidth;
    }

    public int getHairlineTextWidth() {
        return hairlineTextWidth;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public int getExtraPaddingBottom() {
        return extraPaddingBottom;
    }

    public int getPageSizeVertical() {
        return pageSizeVertical;
    }

    public int getPageSizeHorizontal() {
        return pageSizeHorizontal.value == 0 ? totalDayCount * 24 : pageSizeHorizontal.value;
    }

    public int getProgressViewReference() {
        return progressViewReference;
    }

    public int getEmptyViewReference() {
        return emptyViewReference;
    }

    public boolean isSecondaryProgressEnabled() {
        return secondaryProgressEnabled;
    }

    public int getTimebarMinuteStepping() {
        return timebarMinuteStepping;
    }

    public int getHourOffset() {
        return hourOffset;
    }

    public int getMinuteOffset() {
        return minuteOffset;
    }

    public int getDaysForward() {
        return daysForward;
    }

    public int getDaysBackwards() {
        return daysBackwards;
    }

    public int getUpdateFrequencySeconds() {
        return updateFrequencySeconds;
    }

    public int getViewCacheSize() {
        return viewCacheSize;
    }

    public boolean isStickyProgramsEnabled() {
        return stickyProgramsEnabled;
    }

    public boolean isTimebarLabelEnabled() {
        return timebarLabelEnabled;
    }

    public boolean isViewClippingEnabled() {
        return viewClippingEnabled;
    }

    public int getTotalDayCount() {
        return totalDayCount;
    }

    public int getDayWidthPixels() {
        return dayWidthPixels;
    }

    public long getFirstDayStartMillisUTC() {
        return firstDayStartMillisUTC;
    }

    public long getLastDayEndMillisUTC() {
        return lastDayEndMillisUTC;
    }

    public boolean isDiagonalScrollEnabled() {
        return diagonalScrollEnabled;
    }

    public int getDiagonalScrollDetectionOffset() {
        return diagonalScrollDetectionOffset;
    }

    public long getStartMillisUtc() {
        return startMillisUtc;
    }

    public long getEndMillisUtc() {
        return endMillisUtc;
    }

    public boolean isPlaceholdersEnabled() {
        return placeholdersEnabled;
    }

    public boolean isPlaceholdersOnFirstLoadEnabled() {
        return placeholdersOnFirstLoadEnabled;
    }

    public int getPlaceholdersPageSize() {
        return placeholdersPageSize.value == 0 ? totalDayCount * 24 : placeholdersPageSize.value;
    }

    public boolean isLoopingEnabled() {
        return loopingEnabled;
    }

    public boolean isThreadSafe() {
        return threadSafe;
    }

    public long getTimezoneOffset() {
        return timezoneOffset;
    }

    public long getServerTime() {
        return SystemClock.elapsedRealtime() + serverTimeDifference;
    }

    // Setters
    public void setChannelsWidth(int channelsWidth) {
        this.channelsWidth = channelsWidth;
        checkValues();
        parent.reload();
    }

    public void setTimebarHeight(int timebarHeight) {
        this.timebarHeight = timebarHeight;
        checkValues();
        parent.reload();
    }

    /**
     * Sets a horizontal offset on every timebar elements except on the date indicator (where timestamp < 0)
     */
    public EpgAttributeHolder setTimebarOffsetHorizontal(int timebarOffsetHorizontal) {
        this.timebarOffsetHorizontal = timebarOffsetHorizontal;
        checkValues();
        return this;
    }

    public void setMinuteWidth(int minuteWidth) {
        this.minuteWidth = minuteWidth;
        checkValues();
        calculateDerivedValues();
        parent.reload();
    }

    public void setHairlineTextWidth(int hairlineTextWidth) {
        this.hairlineTextWidth = hairlineTextWidth;
        checkValues();
        parent.refresh();
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
        checkValues();
        parent.reload();
    }

    public void setExtraPaddingBottom(int extraPaddingBottom) {
        this.extraPaddingBottom = extraPaddingBottom;
        checkValues();
        parent.refresh();
    }

    public void setPageSizeVertical(int pageSizeVertical) {
        this.pageSizeVertical = pageSizeVertical;
        checkValues();
        parent.reload();
    }

    public void setPageSizeHorizontal(PageSizeHorizontal pageSizeHorizontal) {
        this.pageSizeHorizontal = pageSizeHorizontal;
        parent.reload();
    }

    public void setSecondaryProgressEnabled(boolean secondaryProgressEnabled) {
        this.secondaryProgressEnabled = secondaryProgressEnabled;
        checkValues();
    }

    public void setTimebarMinuteStepping(int timebarMinuteStepping) {
        this.timebarMinuteStepping = timebarMinuteStepping;
        checkValues();
        parent.reload();
    }

    public void setHourAndMinuteOffset(int hourOffset, int minuteOffset) {
        this.hourOffset = hourOffset;
        this.minuteOffset = minuteOffset;
        checkValues();
        calculateDerivedValues(true);
        parent.reload();
    }

    public void setDaysForward(int daysForward) {
        this.daysForward = daysForward;
        checkValues();
        calculateDerivedValues(true);
        parent.reload();
    }

    public void setDaysBackwards(int daysBackwards) {
        this.daysBackwards = daysBackwards;
        checkValues();
        calculateDerivedValues(true);
        parent.reload();
    }

    public void setUpdateFrequencySeconds(int updateFrequencySeconds) {
        this.updateFrequencySeconds = updateFrequencySeconds;
        checkValues();
    }

    public void setViewCacheSize(int viewCacheSize) {
        this.viewCacheSize = viewCacheSize;
        checkValues();
        parent.getRecyclerView().setItemViewCacheSize(viewCacheSize);
    }

    public void setStickyProgramsEnabled(boolean stickyProgramsEnabled) {
        this.stickyProgramsEnabled = stickyProgramsEnabled;
        parent.refresh();
    }

    public void setTimebarLabelEnabled(boolean timebarLabelEnabled) {
        this.timebarLabelEnabled = timebarLabelEnabled;
        parent.refresh();
    }

    public void setViewClippingEnabled(boolean viewClippingEnabled) {
        this.viewClippingEnabled = viewClippingEnabled;
        parent.refresh();
    }

    public void setDiagonalScrollEnabled(boolean diagonalScrollEnabled) {
        this.diagonalScrollEnabled = diagonalScrollEnabled;
        checkValues();
    }

    public void setDiagonalScrollDetectionOffset(int diagonalScrollDetectionOffset) {
        this.diagonalScrollDetectionOffset = diagonalScrollDetectionOffset;
        checkValues();
    }

    public void setStartMillisUtc(long startMillisUtc) {
        setBoundMillisUtc(startMillisUtc, this.endMillisUtc);
    }

    public void setEndMillisUtc(long endMillisUtc) {
        setBoundMillisUtc(this.startMillisUtc, endMillisUtc);
    }

    public void setBoundMillisUtc(long startMillisUtc, long endMillisUtc) {
        // Corrigate values if they are out of bounds
        if (startMillisUtc < firstDayStartMillisUTC) {
            startMillisUtc = firstDayStartMillisUTC;
        }
        if (endMillisUtc > lastDayEndMillisUTC) {
            endMillisUtc = lastDayEndMillisUTC;
        }

        this.startMillisUtc = startMillisUtc;
        this.endMillisUtc = endMillisUtc;
        checkValues();
        calculateDerivedValues();

        parent.refresh();
    }

    public void setPlaceholdersEnabled(boolean placeholdersEnabled) {
        this.placeholdersEnabled = placeholdersEnabled;
        parent.reload();
    }

    public void setPlaceholdersOnFirstLoadEnabled(boolean placeholdersOnFirstLoadEnabled) {
        this.placeholdersOnFirstLoadEnabled = placeholdersOnFirstLoadEnabled;
    }

    public void setPlaceholdersPageSize(PageSizeHorizontal placeholdersPageSize) {
        this.placeholdersPageSize = placeholdersPageSize;
        parent.reload();
    }

    public void setLoopingEnabled(boolean loopingEnabled) {
        this.loopingEnabled = loopingEnabled;
        parent.refresh();
    }

    public void setThreadSafe(boolean threadSafe) {
        this.threadSafe = threadSafe;
    }

    /**
     * The default implementation returns the the device default timezone's offset. However, you're free to change this behavior.
     *
     * @param timezoneOffset the timezone offset set to the EPG. When set to 0, the hairline and all programs will be displayed according to UTC time.
     */
    public void setTimezoneOffset(long timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
        this.timezoneOverwritten = true;
        calculateDerivedValues(true);
    }

    /**
     * Used to sync the server time with the time difference stored in this attributeHolder.
     *
     * @param serverTime the current server time in millis.
     */
    public void setServerTime(long serverTime) {
        this.serverTimeDifference = serverTime - SystemClock.elapsedRealtime();
        calculateDerivedValues(true);
    }

    public enum PageSizeHorizontal {
        NO_PAGINATION(0), _1H(1), _2H(2), _4H(4), _6H(6), _12H(12), _24H(24);

        final int value;

        PageSizeHorizontal(int value) {
            this.value = value;
        }

        static PageSizeHorizontal valueOf(int hours) {
            for (PageSizeHorizontal item : PageSizeHorizontal.values()) {
                if (item.value == hours) {
                    return item;
                }
            }
            throw new IllegalArgumentException("Invalid pagesize hourcount. Hourcount must be a divider of 24.");
        }
    }
}
