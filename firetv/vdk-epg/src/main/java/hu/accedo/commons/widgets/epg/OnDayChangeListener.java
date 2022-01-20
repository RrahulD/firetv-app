/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.widgets.epg;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

public class OnDayChangeListener extends OnScrollListener {
    private int firstVisibleDay;
    private int lastVisibleDay;
    private int firstVisibleCalendarDay;
    private int lastVisibleCalendarDay;

    private EpgView epgView;
    private EpgAttributeHolder attrs;

    public OnDayChangeListener(EpgView epgView) {
        this.epgView = epgView;
        this.attrs = epgView.getAttributes();
        this.firstVisibleDay = -attrs.getDaysBackwards();
        this.lastVisibleDay = -attrs.getDaysBackwards();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        onScrolledAbsolute(epgView.epgLayoutManager.getScrollX(), epgView.epgLayoutManager.getScrollY());
    }

    public void onScrolledAbsolute(int scrollX, int scrollY) {
        int oldFirstVisibleDay = firstVisibleDay;
        int oldLastVisibleDay = lastVisibleDay;
        int oldFirstVisibleCalendarDay = firstVisibleCalendarDay;
        int oldLastVisibleCalendarDay = lastVisibleCalendarDay;
        int hourOffsetPixelSize = attrs.getHourOffset() * 60 * attrs.getMinuteWidth();

        firstVisibleDay = (scrollX / attrs.getDayWidthPixels()) - attrs.getDaysBackwards();
        lastVisibleDay = ((scrollX + epgView.epgLayoutManager.getWidth() - attrs.getChannelsWidth() - 1) / attrs.getDayWidthPixels()) - attrs.getDaysBackwards();
        firstVisibleCalendarDay = ((scrollX + hourOffsetPixelSize) / attrs.getDayWidthPixels()) - attrs.getDaysBackwards();
        lastVisibleCalendarDay = ((scrollX + epgView.epgLayoutManager.getWidth() - attrs.getChannelsWidth() - 1 + hourOffsetPixelSize) / attrs.getDayWidthPixels()) - attrs.getDaysBackwards();

        if ((oldFirstVisibleDay != firstVisibleDay || oldLastVisibleDay != lastVisibleDay) && firstVisibleDay == lastVisibleDay) {
            onDayChange(firstVisibleDay);
        }
        if ((oldFirstVisibleCalendarDay != firstVisibleCalendarDay || oldLastVisibleCalendarDay != lastVisibleCalendarDay) && firstVisibleCalendarDay == lastVisibleCalendarDay) {
            onCalendarDayChange(firstVisibleCalendarDay);
        }
    }

    public void onDayChange(int dayOffset) {
    }

    public void onCalendarDayChange(int dayOffset) {
    }
}
