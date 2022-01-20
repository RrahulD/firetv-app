/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.widgets.epg.adapter;

import android.content.Context;
import android.graphics.Rect;

import androidx.recyclerview.widget.RecyclerView;

import hu.accedo.commons.widgets.epg.EpgAdapter;
import hu.accedo.commons.widgets.epg.EpgAttributeHolder;
import hu.accedo.commons.widgets.epg.EpgDataSource;
import hu.accedo.commons.widgets.epg.EpgDataSource.ViewHolderHairline;

/**
 * Defines the line showing the current time. You normally need two instances or this,
 * one to go above the timebar with the text "Now" on it, and one to go above programs.
 * <p>
 * The reason for this is to have it over the timebar, but under the channels.
 * <p>
 * This visual difference of the two parts is represented by the variable "isTimebarPart"
 */
public class EpgItemHairline<Channel, Program> implements EpgItem {
    private EpgDataSource<Channel, Program> epgDataSource;
    private boolean isTimebarPart;
    private EpgAttributeHolder attrs;

    private Rect rect = new Rect();

    @Override
    public Rect getRect() {
        int minutesFromLeft = (int) ((attrs.getServerTime() - attrs.getFirstDayStartMillisUTC()) / 1000 / 60);

        rect.left = attrs.getChannelsWidth() - attrs.getHairlineTextWidth() / 2 + minutesFromLeft * attrs.getMinuteWidth();
        rect.right = rect.left + attrs.getHairlineTextWidth();

        return rect;
    }

    @Override
    public boolean isStickyHorizontaly() {
        return false;
    }

    @Override
    public boolean isStickyVerticaly() {
        return true;
    }

    public boolean isTimebarPart() {
        return isTimebarPart;
    }

    public EpgItemHairline(Context context, EpgAttributeHolder attrs, EpgDataSource<Channel, Program> epgDataSource, boolean isTimebarPart) {
        this.attrs = attrs;
        this.epgDataSource = epgDataSource;
        this.isTimebarPart = isTimebarPart;

        if (isTimebarPart) {
            this.rect.top = 0;
            this.rect.bottom = attrs.getTimebarHeight();
        } else {
            this.rect.top = 0;
            this.rect.bottom = context.getResources().getDisplayMetrics().heightPixels;
        }
    }

    @Override
    public int getItemViewType() {
        return EpgAdapter.ITEMTYPE_HAIRLINE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        epgDataSource.onBindHairline((ViewHolderHairline) viewHolder, isTimebarPart);
    }
}
