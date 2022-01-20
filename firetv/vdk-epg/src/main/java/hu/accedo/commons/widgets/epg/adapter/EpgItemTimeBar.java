/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.widgets.epg.adapter;

import android.graphics.Rect;

import androidx.recyclerview.widget.RecyclerView;

import hu.accedo.commons.widgets.epg.EpgAdapter;
import hu.accedo.commons.widgets.epg.EpgAttributeHolder;
import hu.accedo.commons.widgets.epg.EpgDataSource;
import hu.accedo.commons.widgets.epg.EpgDataSource.ViewHolderTimebar;

/**
 * Defines a fixed timebar item, at the top of the screen.
 */
public class EpgItemTimeBar<Channel, Program> implements EpgItem {
    private EpgDataSource<Channel, Program> epgDataSource;
    private EpgAttributeHolder attrs;
    private long timestamp;
    private Rect rect = new Rect();

    @Override
    public Rect getRect() {
        return rect;
    }

    @Override
    public boolean isStickyHorizontaly() {
        return attrs.isTimebarLabelEnabled() && timestamp < 0;
    }

    @Override
    public boolean isStickyVerticaly() {
        return true;
    }

    public EpgItemTimeBar(EpgAttributeHolder attrs, EpgDataSource<Channel, Program> epgDataSource, int left, int right, long timestamp) {
        this.attrs = attrs;
        this.epgDataSource = epgDataSource;

        this.rect.left = left;
        this.rect.top = 0;
        this.rect.right = right;
        this.rect.bottom = attrs.getTimebarHeight();

        if (timestamp >= 0) {
            this.rect.left += attrs.getTimebarOffsetHorizontal();
            this.rect.right += attrs.getTimebarOffsetHorizontal();
        }

        this.timestamp = timestamp;
    }

    @Override
    public int getItemViewType() {
        return EpgAdapter.ITEMTYPE_TIMEBAR;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        epgDataSource.onBindTimeBar((ViewHolderTimebar) viewHolder, timestamp);
    }
}
