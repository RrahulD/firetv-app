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
import hu.accedo.commons.widgets.epg.EpgDataSource.ViewHolderChannel;

/**
 * Defines a channel header at the left side of the screen.
 */
public class EpgItemChannel<Channel, Program> implements EpgItem {
    private EpgDataSource<Channel, Program> epgDataSource;
    private Channel channel;
    private Rect rect = new Rect();

    public Channel getChannel() {
        return channel;
    }

    @Override
    public Rect getRect() {
        return rect;
    }

    @Override
    public boolean isStickyHorizontaly() {
        return true;
    }

    @Override
    public boolean isStickyVerticaly() {
        return false;
    }

    public EpgItemChannel(EpgAttributeHolder attrs, EpgDataSource<Channel, Program> epgDataSource, Channel channel) {
        this.epgDataSource = epgDataSource;
        this.channel = channel;

        this.rect.left = 0;
        this.rect.top = attrs.getTimebarHeight();
        this.rect.right = this.rect.left + attrs.getChannelsWidth();
        this.rect.bottom = this.rect.top + attrs.getRowHeight();
    }

    @Override
    public int getItemViewType() {
        return EpgAdapter.ITEMTYPE_CHANNEL;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        epgDataSource.onBindChannel((ViewHolderChannel) viewHolder, channel);
    }
}
