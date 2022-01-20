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
import hu.accedo.commons.widgets.epg.EpgDataSource.ViewHolderPlaceholder;
import hu.accedo.commons.widgets.epg.EpgDataSource.ViewHolderProgram;

/**
 * Defines a program item.
 */
public class EpgItemProgram<Channel, Program> implements EpgItem {
    private EpgDataSource<Channel, Program> epgDataSource;
    private EpgAttributeHolder attrs;
    private Channel channel;
    private Program program;
    private boolean loaded;

    private Rect rect = new Rect();

    public Channel getChannel() {
        return channel;
    }

    public Program getProgram() {
        return program;
    }

    @Override
    public Rect getRect() {
        return rect;
    }

    @Override
    public boolean isStickyHorizontaly() {
        return false;
    }

    @Override
    public boolean isStickyVerticaly() {
        return false;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public EpgItemProgram(EpgAttributeHolder attrs, EpgDataSource<Channel, Program> epgDataSource, Channel channel, int left, int right, boolean loaded) {
        this.attrs = attrs;
        this.epgDataSource = epgDataSource;
        this.channel = channel;
        this.loaded = loaded;

        this.rect.left = left;
        this.rect.top = attrs.getTimebarHeight();
        this.rect.right = right;
        this.rect.bottom = this.rect.top + attrs.getRowHeight();
    }

    public EpgItemProgram(EpgAttributeHolder attrs, EpgDataSource<Channel, Program> epgDataSource, Channel channel, Program program) {
        this(attrs, epgDataSource, channel, toPixels(attrs, epgDataSource.getStartTimeMillis(program, channel)), toPixels(attrs, epgDataSource.getEndTimeMillis(program)), true);
        this.program = program;
    }

    @Override
    public int getItemViewType() {
        return program != null ? EpgAdapter.ITEMTYPE_PROGRAM : EpgAdapter.ITEMTYPE_PLACEHOLDER;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        if (program != null) {
            epgDataSource.onBindProgram((ViewHolderProgram) viewHolder, channel, program);
        } else {
            epgDataSource.onBindPlaceholder((ViewHolderPlaceholder) viewHolder, channel, toTimestamp(attrs, rect.left), toTimestamp(attrs, rect.right), loaded);
        }
    }

    static int toPixels(EpgAttributeHolder attrs, long timestamp) {
        return (int) ((timestamp - attrs.getFirstDayStartMillisUTC()) / 60000 * attrs.getMinuteWidth() + attrs.getChannelsWidth());
    }

    static long toTimestamp(EpgAttributeHolder attrs, int pixels) {
        return (pixels - attrs.getChannelsWidth()) / attrs.getMinuteWidth() * 60000 + attrs.getFirstDayStartMillisUTC();
    }
}
