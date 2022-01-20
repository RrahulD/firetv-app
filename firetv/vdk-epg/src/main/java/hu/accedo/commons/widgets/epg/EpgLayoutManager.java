/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.widgets.epg;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.Recycler;
import androidx.recyclerview.widget.RecyclerView.State;

import java.util.Iterator;
import java.util.List;

import hu.accedo.commons.logging.L;
import hu.accedo.commons.tools.ComponentTools;
import hu.accedo.commons.tools.MathExtender;
import hu.accedo.commons.widgets.epg.adapter.EpgItem;
import hu.accedo.commons.widgets.epg.adapter.EpgItemChannel;
import hu.accedo.commons.widgets.epg.adapter.EpgItemHairline;
import hu.accedo.commons.widgets.epg.adapter.EpgItemProgram;
import hu.accedo.commons.widgets.epg.adapter.EpgItemTimeBar;

public class EpgLayoutManager extends RecyclerView.LayoutManager {
    private int scrollX = 0;
    private int scrollY = 0;

    private int totalWidth;
    private int totalHeight;
    private int pixelStart;
    private int pixelEnd;
    private int maxVisibleRows;
    private int pageSizeHorizontalPixels;

    private EpgView epgView;
    private EpgAdapter epgAdapter;
    private EpgDataManager epgDataManager;
    private EpgAttributeHolder attrs;

    private FocusHandler focusHandler;
    private boolean hadFocus;

    // Scroll lock
    private int scrollDirectionLock = 0;
    private int dxThisTouch;
    private int dyThisTouch;

    public EpgLayoutManager setFocusHandler(FocusHandler focusHandler) {
        this.focusHandler = focusHandler;
        return this;
    }

    public int getScrollX() {
        return scrollX;
    }

    public int getScrollY() {
        return scrollY;
    }

    public EpgLayoutManager(EpgView epgView, EpgAdapter epgAdapter, EpgDataManager epgDataManager) {
        this.epgView = epgView;
        this.epgAdapter = epgAdapter;
        this.epgDataManager = epgDataManager;
        this.attrs = epgView.getAttributes();
        this.totalWidth = 0;
        this.totalHeight = 0;
    }

    @Override
    public boolean canScrollHorizontally() {
        return attrs.isDiagonalScrollEnabled() || scrollDirectionLock != View.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public boolean canScrollVertically() {
        return attrs.isDiagonalScrollEnabled() || scrollDirectionLock != View.SCROLL_AXIS_HORIZONTAL;
    }

    @Override
    public LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            params.setLayoutDirection(getLayoutDirection());
        }

        return params;
    }

    @Override
    public void onLayoutChildren(Recycler recycler, State state) {
        totalWidth = attrs.getChannelsWidth() + attrs.getMinuteWidth() * attrs.getTotalDayCount() * 24 * 60;
        totalHeight = attrs.getTimebarHeight() + attrs.getExtraPaddingBottom() + epgAdapter.getChannelCount(true) * attrs.getRowHeight();
        pixelStart = (int) ((attrs.getStartMillisUtc() - attrs.getFirstDayStartMillisUTC()) / 60000 * attrs.getMinuteWidth());
        pixelEnd = (int) ((attrs.getEndMillisUtc() - attrs.getFirstDayStartMillisUTC()) / 60000 * attrs.getMinuteWidth()) + attrs.getChannelsWidth();
        maxVisibleRows = MathExtender.divCeil(getHeight() - attrs.getTimebarHeight(), attrs.getRowHeight()) + 1;
        pageSizeHorizontalPixels = attrs.getPageSizeHorizontal() * 60 * attrs.getMinuteWidth();

        if (focusHandler != null) {
            focusHandler.onLayoutChildren(totalWidth, totalHeight, pixelStart, pixelEnd);
        }

        // Corrigate scroll if necessary after dataset change
        scrollTo(scrollX, scrollY);

        // Update view
        hadFocus = epgView.hasFocus();
        detachAndScrapAttachedViews(recycler);
        updateView(recycler, state);
    }

    private void updateView(Recycler recycler, State state) {
        /*
         * Notify epgDataManager of the visible area changed
         */
        int firstVisiblePageX = scrollX / pageSizeHorizontalPixels;
        int lastVisiblePageX = (scrollX + getWidth() - attrs.getChannelsWidth() - 1) / pageSizeHorizontalPixels;
        int firstVisibleRow = MathExtender.divFloor(scrollY, attrs.getRowHeight());
        int lastVisibleRow;
        if (epgAdapter.getChannelCount(true) == 0) {
            lastVisibleRow = -1;
        } else {
            lastVisibleRow = MathExtender.divFloor(scrollY - attrs.getTimebarHeight() + getHeight(), attrs.getRowHeight());

            if (!attrs.isLoopingEnabled() || !isLoopingPossible()) {
                lastVisibleRow = Math.min(lastVisibleRow, epgAdapter.getChannelCount(true) - 1);
            }
        }
        epgDataManager.onVisibleRowsChanged(firstVisiblePageX, lastVisiblePageX, firstVisibleRow, lastVisibleRow, false);

        if (focusHandler != null) {
            focusHandler.onUpdateView(scrollX, scrollY, firstVisibleRow, lastVisibleRow);
        }

        /*
         * Then, we will hash all views into a viewcache by position
         */

        SparseArray<View> viewCache = new SparseArray<View>(getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            viewCache.put((int) child.getTag(R.id.position), child);
        }

        /*
         * Now we display the visible items. Create if necessary, otherwise just reattach and remove from cache.
         */
        if (epgAdapter != null) {
            List<EpgItemHairline> epgItemHairlines = epgAdapter.getEpgItemHairlines();
            List<EpgItemTimeBar> epgItemTimebars = epgAdapter.getEpgItemTimebars();

            // Programs
            for (int row = firstVisibleRow; row <= lastVisibleRow; row++) {
                Iterator<EpgItemProgram> iterator = epgAdapter.getEpgItemProgramsIterator(row, scrollX, scrollX + getWidth(), true);

                while (iterator.hasNext()) {
                    updateEpgItem(recycler, state, viewCache, iterator.next(), row);
                }
            }

            // Hairline bottom
            if (epgItemHairlines != null && !epgItemHairlines.isEmpty()) {
                updateEpgItem(recycler, state, viewCache, epgItemHairlines.get(0), 0);
            }

            // Channels
            for (int i = firstVisibleRow; i <= lastVisibleRow; i++) {
                updateEpgItem(recycler, state, viewCache, epgAdapter.getEpgItemChannel(i, true), i);
            }

            // Timebar
            for (EpgItem item : epgItemTimebars) {
                updateEpgItem(recycler, state, viewCache, item, 0);
            }

            // Hairline top
            if (epgItemHairlines != null && epgItemHairlines.size() > 1) {
                updateEpgItem(recycler, state, viewCache, epgItemHairlines.get(1), 0);
            }
        }

        /*
         * Finally, we ask the Recycler to scrap and store any views that we did not remove. These are views that are no longer visible.
         */
        for (int i = 0; i < viewCache.size(); i++) {
            View view = viewCache.valueAt(i);
            detachView(view);
            recycler.recycleView(view);
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        // Focus
        if (focusHandler != null) {
            focusHandler.onScrollStateChanged(state);
        }

        // If not touching, unlock
        if (!attrs.isDiagonalScrollEnabled() && state != RecyclerView.SCROLL_STATE_DRAGGING) {
            scrollDirectionLock = 0;
            dxThisTouch = 0;
            dyThisTouch = 0;
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, Recycler recycler, State state) {
        // Lock if necessary, only decide to lock after scroll lock offset has been
        // passed, don't do anything otherwise
        if (!attrs.isDiagonalScrollEnabled()) {
            this.dxThisTouch += Math.abs(dx);
            if (this.dxThisTouch < attrs.getDiagonalScrollDetectionOffset()) {
                return dx;
            } else {
                scrollDirectionLock = View.SCROLL_AXIS_HORIZONTAL;
            }
        }

        // RTL flip
        if (ComponentTools.isRtl(epgView)) {
            dx *= -1;
        }

        // Bounds
        int oldScrollX = scrollX;
        scrollTo(scrollX + dx, scrollY);
        int effectiveDx = scrollX - oldScrollX;

        // RTL flipback for overscroll && apply (because offsetChildrenHorizontal() is already RTL flipped)
        if (ComponentTools.isRtl(epgView)) {
            dx *= -1;
            effectiveDx *= -1;
        }

        // Apply scroll
        offsetChildrenHorizontal(-effectiveDx);

        // Update
        updateView(recycler, state);

        return dx;
    }

    @Override
    public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
        // Lock if necessary, only decide to lock after scroll lock offset has been passed, don't do anything otherwise
        if (!attrs.isDiagonalScrollEnabled()) {
            this.dyThisTouch += Math.abs(dy);
            if (this.dyThisTouch < attrs.getDiagonalScrollDetectionOffset()) {
                return dy;
            } else {
                scrollDirectionLock = View.SCROLL_AXIS_VERTICAL;
            }
        }

        // Bounds
        int oldScrollY = scrollY;
        scrollTo(scrollX, scrollY + dy);
        int effectiveDy = scrollY - oldScrollY;

        // Apply scroll
        offsetChildrenVertical(-effectiveDy);

        // Update
        updateView(recycler, state);

        return dy;
    }

    @Override
    public void offsetChildrenHorizontal(int dx) {
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            final EpgItem item = (EpgItem) child.getTag(R.id.epg_item);
            if (!item.isStickyHorizontaly()) {
                child.offsetLeftAndRight(dx);
            }
        }
    }

    @Override
    public void offsetChildrenVertical(int dy) {
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            final EpgItem item = (EpgItem) child.getTag(R.id.epg_item);
            if (!item.isStickyVerticaly()) {
                child.offsetTopAndBottom(dy);
            }
        }
    }

    public void scrollTo(int x, int y) {
        scrollX = x;
        scrollX = Math.min(scrollX, pixelEnd - getWidth());
        scrollX = Math.max(scrollX, pixelStart);

        scrollY = y;
        if (!attrs.isLoopingEnabled() || !isLoopingPossible()) {
            scrollY = Math.min(scrollY, totalHeight - getHeight());
            scrollY = Math.max(scrollY, 0);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onAdapterChanged(Adapter oldAdapter, Adapter newAdapter) {
        removeAllViews();

        try {
            epgAdapter = (EpgAdapter) newAdapter;
        } catch (ClassCastException e) {
            L.e(e, "The RecyclerView of EpgLayoutManager must only use adapters descending from RecyclerView.ViewHolder, ? extends IEpgItem>");
        }
    }

    private void updateEpgItem(Recycler recycler, State state, SparseArray<View> viewCache, EpgItem epgItem, int row) {
        int rowOffset = row * attrs.getRowHeight();

        int left = epgItem.getRect().left;
        int top = epgItem.getRect().top + rowOffset;
        int right = epgItem.getRect().right;
        int bottom = epgItem.getRect().bottom + rowOffset;

        if ((right >= scrollX || epgItem.isStickyHorizontaly())
                && (left <= (scrollX + getWidth()) || epgItem.isStickyHorizontaly())
                && (bottom >= scrollY || epgItem.isStickyVerticaly())
                && (top <= scrollY + getHeight() || epgItem.isStickyVerticaly())) {

            int itemIndex = epgAdapter.getItemIndex(epgItem);
            View view = viewCache.get(itemIndex);

            if (view == null) {
                if (0 <= itemIndex && itemIndex < state.getItemCount()) {
                    view = recycler.getViewForPosition(itemIndex);
                    ViewCompat.setLayoutDirection(view, ViewCompat.getLayoutDirection(epgView));
                    view.getLayoutParams().width = right - left;
                    view.getLayoutParams().height = bottom - top;
                    measureChildWithMargins(view, 0, 0);
                    view.forceLayout();
                    addView(view);
                    positionEpgItemView(view, epgItem, left, top, right, bottom, true);
                }
            } else {
                // detach+attach = bringtofront
                detachView(view);
                attachView(view);
                viewCache.remove(itemIndex);

                // When the view is already on the page, we only reposition clipped items
                positionEpgItemView(view, epgItem, left, top, right, bottom, false);
            }

            if (focusHandler != null) {
                focusHandler.onUpdateEpgItem(view, epgItem);
            }
        }
    }

    private void positionEpgItemView(View view, EpgItem epgItem, int left, int top, int right, int bottom, boolean force) {
        //Clip reasons
        boolean clippableTimebar = epgItem instanceof EpgItemTimeBar && attrs.isTimebarLabelEnabled() && attrs.isViewClippingEnabled() && left > 0;
        boolean clippableProgramLeft = epgItem instanceof EpgItemProgram && (attrs.isStickyProgramsEnabled() || attrs.isViewClippingEnabled());
        boolean clippableProgramTop = epgItem instanceof EpgItemProgram && attrs.isViewClippingEnabled();
        boolean clippableChannel = epgItem instanceof EpgItemChannel && attrs.isViewClippingEnabled();
        boolean clippableHairline = epgItem instanceof EpgItemHairline && attrs.isViewClippingEnabled() && (!((EpgItemHairline) epgItem).isTimebarPart() || attrs.isTimebarLabelEnabled());
        if (!clippableChannel && !clippableProgramLeft && !clippableProgramTop && !clippableTimebar && !clippableHairline && !force) {
            return;
        }

        // Clip left then clip top
        if (clippableTimebar || clippableProgramLeft || clippableHairline) {
            if (left < scrollX + attrs.getChannelsWidth()) {
                left = scrollX + attrs.getChannelsWidth();
            }
        }
        if (clippableProgramTop || clippableChannel) {
            if (top < scrollY + attrs.getTimebarHeight()) {
                top = scrollY + attrs.getTimebarHeight();
            }
        }

        if (epgItem instanceof EpgItemProgram && attrs.isStickyProgramsEnabled() && view.getLayoutParams().width != right - left) {
        // Sticky programs also need to be remeasured so they would ellipsize
            view.getLayoutParams().width = right - left;
            measureChildWithMargins(view, 0, 0);
        }

        // RTL flip rendering only
        boolean isRtl = ComponentTools.isRtl(epgView);
        int start = isRtl ? (getWidth() - right) : left;
        int end = isRtl ? (getWidth() - left) : right;
        int rtlScrollX = isRtl ? -scrollX : scrollX;

        // Position
        layoutDecorated(view, start - (epgItem.isStickyHorizontaly() ? 0 : rtlScrollX),
                top - (epgItem.isStickyVerticaly() ? 0 : scrollY),
                end - (epgItem.isStickyHorizontaly() ? 0 : rtlScrollX),
                bottom - (epgItem.isStickyVerticaly() ? 0 : scrollY));
    }

    private boolean isLoopingPossible() {
        return epgAdapter.getChannelCount(true) >= maxVisibleRows;
    }

    @Override
    public View onInterceptFocusSearch(View focused, int direction) {
        if (focusHandler != null) {
            return focusHandler.onInterceptFocusSearch(focused, direction);
        }

        return super.onInterceptFocusSearch(focused, direction);
    }

    @Nullable
    @Override
    public View onFocusSearchFailed(View focused, int direction, Recycler recycler, State state) {
        if (focusHandler != null) {
            return focusHandler.onFocusSearchFailed(focused, direction, recycler, state);
        }
        return super.onFocusSearchFailed(focused, direction, recycler, state);
    }

    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate) {
        return requestChildRectangleOnScreen(parent, child, rect, immediate, false);
    }

    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
        if (focusHandler != null) {
            return focusHandler.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
        } else {
            rect.top -= attrs.getTimebarHeight();
            rect.left -= attrs.getChannelsWidth();
            return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
        }
    }

    public static abstract class FocusHandler {
        protected EpgView epgView;

        public boolean hadFocus() {
            return epgView.epgLayoutManager.hadFocus;
        }

        public EpgAdapter getAdapter() {
            return epgView.epgAdapter;
        }

        public EpgLayoutManager getLayoutManager() {
            return epgView.epgLayoutManager;
        }

        public FocusHandler(EpgView epgView) {
            this.epgView = epgView;
        }

        public View getView(EpgItem epgItem) {
            if (epgItem != null) {
                for (int i = 0; i < epgView.recyclerView.getChildCount(); i++) {
                    if (epgView.recyclerView.getChildAt(i).getTag(R.id.epg_item) == epgItem) {
                        return epgView.recyclerView.getChildAt(i);
                    }
                }
            }
            return null;
        }

        // LayoutManager
        public void onLayoutChildren(int totalWidth, int totalHeight, int pixelStart, int pixelEnd) {
        }

        public void onUpdateView(int scrollX, int scrollY, int firstVisibleRow, int lastVisibleRow) {
        }

        public void onUpdateEpgItem(View view, EpgItem epgItem) {
        }

        public View onInterceptFocusSearch(View focused, int direction) {
            return null;
        }

        public View onFocusSearchFailed(View focused, int direction, Recycler recycler, State state) {
            return null;
        }

        public void onScrollStateChanged(int state) {
        }

        // EpgView
        public void requestChildFocus(View child, View focused) {
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
            return epgView.dispatchSuperKeyEvent(event);
        }

        public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, Boolean focusedChildVisible) {
            return false;
        }

        public void onScrollToHairline(int hairlineX) {
        }
    }
}
