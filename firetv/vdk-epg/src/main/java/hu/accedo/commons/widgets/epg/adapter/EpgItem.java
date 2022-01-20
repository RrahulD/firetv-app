/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.widgets.epg.adapter;

import android.graphics.Rect;

import androidx.recyclerview.widget.RecyclerView;

public interface EpgItem {
    /**
     * Required by multitype adapters, to be able to handle different viewholder
     * types for recycling.
     *
     * @return The 0 based index of the type of this item
     */
    public int getItemViewType();

    /**
     * @param viewHolder the viewHolder that should be populated with the data provided at "position"
     */
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder);

    /**
     * @return The absolute position of the view in pixels.
     */
    public Rect getRect();

    /**
     * @return true, if the item should not be affected by horizontal scrolling
     */
    public boolean isStickyHorizontaly();

    /**
     * @return true, if the item should not be affected by vertical scrolling
     */
    public boolean isStickyVerticaly();
}
