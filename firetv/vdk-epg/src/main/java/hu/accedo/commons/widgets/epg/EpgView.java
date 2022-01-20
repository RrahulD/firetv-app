/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.widgets.epg;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import hu.accedo.commons.tools.ComponentTools;
import hu.accedo.commons.tools.MathExtender;
import hu.accedo.commons.widgets.epg.EpgDataManager.LoadingListener;
import hu.accedo.commons.widgets.epg.EpgLayoutManager.FocusHandler;
import hu.accedo.commons.widgets.epg.adapter.EpgItem;
import hu.accedo.commons.widgets.epg.adapter.EpgItemHairline;

public class EpgView extends FrameLayout {
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected View progressView;
    protected View emptyView;

    protected EpgAttributeHolder epgAttributeHolder;
    protected EpgDataSource epgDataSource;
    protected EpgAdapter epgAdapter;
    protected EpgDataManager epgDataManager;
    protected EpgLayoutManager epgLayoutManager;

    protected FocusHandler focusHandler;

    protected int restoredX;
    protected int restoredY;

    private Runnable runnableUpdate = new Runnable() {
        @Override
        public void run() {
            if (epgAdapter != null) {
                epgAdapter.notifyDataSetChanged();
            }
            postDelayed(runnableUpdate, getNextUpdateDelay());
        }
    };
    private LoadingListener loadingListener = new LoadingListener() {
        @Override
        public void onChannelsLoaded() {
            if (restoredX == 0 && restoredY == 0) {
                scrollToHairline(false);
            } else {
                scrollToInternal(restoredX, restoredY);
            }
        }

        @Override
        public void onLoadStarted(boolean isEmpty) {
            if (epgAttributeHolder.isSecondaryProgressEnabled()) {
                swipeRefreshLayout.setRefreshing(!isEmpty || progressView == null);
            }
            if (progressView != null) {
                progressView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLoadFinished(boolean isEmpty) {
            if (epgAttributeHolder.isSecondaryProgressEnabled()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (progressView != null) {
                progressView.setVisibility(View.GONE);
            }
            if (emptyView != null) {
                emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }
        }
    };

    /**
     * @param onDayChangeListener a callback to be invoked when the EpgView is scrolled to another day.
     */
    public void setOnDayChangeListener(OnDayChangeListener onDayChangeListener) {
        recyclerView.setOnScrollListener(onDayChangeListener);
    }

    /**
     * @param focusHandler a class responsible for handling how the EpgView should handle DPAD input.
     */
    public void setFocusHandler(FocusHandler focusHandler) {
        this.focusHandler = focusHandler;
        if (epgLayoutManager != null) {
            epgLayoutManager.setFocusHandler(focusHandler);
        }
    }

    /**
     * @return the SwipeRefreshLayout shown by the EpgView while loading more data. Can be used to customize it's colors.
     */
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    /**
     * @return the underlaying RecyclerView this EpgView runs on.
     */
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    /**
     * @return the holder for the customization parameters specified in the layout XMLs. Can be used to override configs at runtime.
     */
    public EpgAttributeHolder getAttributes() {
        return epgAttributeHolder;
    }

    /**
     * @return the datasource provided by the user.
     */
    public EpgDataSource getDataSource() {
        return epgDataSource;
    }

    /**
     * @param epgDataSource the datasource that does the data fetching, and binding. The user should provide this for the EPG to work.
     * @param <Channel>     your POJO object representing channels. The EpgView doesn't care what it is, it just needs to know, so it can ask you to bind it to the views.
     * @param <Program>     your POJO object representing programs. The EpgView doesn't care what it is, it just needs to know, so it can ask you to bind it to the views.
     */
    public <Channel, Program> void setDataSource(final EpgDataSource<Channel, Program> epgDataSource) {
        // Get refs if any
        progressView = findViewById(epgAttributeHolder.getProgressViewReference());
        emptyView = findViewById(epgAttributeHolder.getEmptyViewReference());

        // RTL the two views if necessary
        if (progressView != null) {
            progressView.setScaleX(ComponentTools.isRtl(EpgView.this) ? -1 : 1);
        }
        if (emptyView != null) {
            emptyView.setScaleX(ComponentTools.isRtl(EpgView.this) ? -1 : 1);
        }

        // Recreate
        EpgView.this.epgDataSource = epgDataSource;
        epgAdapter = new EpgAdapter<Channel, Program>(this, epgDataSource);
        epgDataManager = new EpgDataManager<Channel, Program>(this, epgDataSource, epgAdapter, loadingListener);
        epgLayoutManager = new EpgLayoutManager(this, epgAdapter, epgDataManager).setFocusHandler(focusHandler);

        recyclerView.setLayoutManager(epgLayoutManager);
        recyclerView.setAdapter(epgAdapter);

        // Start populating data
        if (hasWindowFocus()) {
            epgDataSource.notifyDataSetChanged();
            postDelayed(runnableUpdate, getNextUpdateDelay());
        }
    }

    public EpgView(@NonNull Context context) {
        this(context, null, 0, 0);
    }

    public EpgView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public EpgView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EpgView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (isInEditMode()) {
            return;
        }

        epgAttributeHolder = new EpgAttributeHolder(this, attrs, defStyleAttr, defStyleRes);

        // PullToRefresh (Only used for progress)
        swipeRefreshLayout = new SwipeRefreshLayout(context);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) (epgAttributeHolder.getTimebarHeight() * 1.2f)); //Hack to force it to work on setRefresing(true);
        addView(swipeRefreshLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Recycler
        recyclerView = new RecyclerView(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(null);
        recyclerView.setItemViewCacheSize(epgAttributeHolder.getViewCacheSize());
        swipeRefreshLayout.addView(recyclerView, new SwipeRefreshLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (focusHandler != null) {
            focusHandler.requestChildFocus(child, focused);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            // Restart whatever we cancelled
            if (epgDataSource != null) {
                epgDataSource.notifyDataSetChanged();
            }
            // Resume updates
            postDelayed(runnableUpdate, getNextUpdateDelay());
        } else {
            // Cancel all running tasks
            if (epgDataManager != null) {
                epgDataManager.cancelAll();
            }
            // Stop updates
            removeCallbacks(runnableUpdate);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (focusHandler != null) {
            return focusHandler.dispatchKeyEvent(event);
        }

        return super.dispatchKeyEvent(event);
    }

    protected boolean dispatchSuperKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    /**
     * Scrolls the EpgView horizontally, so that the current time would be in the center of the screen.
     *
     * @param animated true, if we should smooth scroll, false if we should jump.
     */
    public void scrollToHairline(boolean animated) {
        scrollToHairline(animated, false);
    }

    /**
     * Scrolls the EpgView horizontally, so that the current time would be in the center of the screen.
     *
     * @param animated             true, if we should smooth scroll, false if we should jump.
     * @param keepVerticalPosition if vertical position must be stored during animated scroll
     */
    public void scrollToHairline(boolean animated, boolean keepVerticalPosition) {
        if (epgLayoutManager == null || epgAdapter == null) {
            return;
        }

        // Get first hairline item from Adapter
        @SuppressWarnings("unchecked")
        List<EpgItem> epgItemHairlines = epgAdapter.getEpgItemHairlines();
        if (epgItemHairlines != null && !epgItemHairlines.isEmpty() && epgItemHairlines.get(0) != null) {
            int x = epgItemHairlines.get(0).getRect().left - epgAttributeHolder.getHairlineTextWidth() / 2 - epgLayoutManager.getWidth() / 2;
            // Calculate its left edge

            // Scroll to it
            if (animated) {
                epgLayoutManager.onScrollStateChanged(RecyclerView.SCROLL_STATE_DRAGGING);
                recyclerView.smoothScrollBy(x - epgLayoutManager.getScrollX(), keepVerticalPosition ? 0 : 0 - epgLayoutManager.getScrollY());
            } else {
                scrollToInternal(x, epgLayoutManager.getScrollY());
            }
            if (focusHandler != null) {
                focusHandler.onScrollToHairline(x);
            }
        }
    }

    /**
     * Scrolls to the given day's given hour, so that it'd be at the left side of the screen.
     *
     * @param dayOffset the day to scroll to. 0 means today, 1 means tomorrow, -1 means yesterday, and so on.
     * @param hour      the day of the hour to scroll to.
     * @param animated  true, if we should smoothscroll, false if we should jump.
     */
    public void scrollToDayOffset(int dayOffset, int hour, boolean animated) {
        if (epgLayoutManager == null || epgAdapter == null) {
            return;
        }

        // Calculate target X
        int hourWidth = epgAttributeHolder.getMinuteWidth() * 60;
        int dayWidth = hourWidth * 24;
        int dayStartLeft = (dayOffset + epgAttributeHolder.getDaysBackwards()) * dayWidth;
        int targetX = dayStartLeft + hour * hourWidth;

        if (animated) {
            epgLayoutManager.onScrollStateChanged(RecyclerView.SCROLL_STATE_DRAGGING);
            recyclerView.smoothScrollBy(targetX - epgLayoutManager.getScrollX(), 0);
        } else {
            scrollToInternal(targetX, epgLayoutManager.getScrollY());
        }
    }

    /**
     * Scrolls to a given UTC timestamp, so that it'd be at the left side of the screen.
     *
     * @param millisUtc the timestamp to scroll to. Will be trimmed to the bounds of the EPG.
     * @param animated  true, if we should smoothscroll, false if we should jump.
     */
    public void scrollToMillisUtc(long millisUtc, boolean animated) {
        if (epgLayoutManager == null || epgAdapter == null) {
            return;
        }

        int targetX = getTargetX(millisUtc);
        if (animated) {
            epgLayoutManager.onScrollStateChanged(RecyclerView.SCROLL_STATE_DRAGGING);
            recyclerView.smoothScrollBy(targetX - epgLayoutManager.getScrollX(), 0);
        } else {
            scrollToInternal(targetX, epgLayoutManager.getScrollY());
        }
    }

    /**
     * Scrolls to the given channelIndex, or the given row to be specific.
     *
     * @param channelIndex the channel to scroll to. Counted as rownumber, so takes filtering into consideration.
     * @param gravity      tells if the focused channel should be positioned to the TOP, CENTER or BOTTOM of the screen. Only these 3 gravity values are supported.
     * @param animated     uses smoothscroll, if true. Jumps otherwise.
     */
    public void scrollToChannel(int channelIndex, int gravity, boolean animated) {
        if (epgLayoutManager == null || epgAdapter == null) {
            return;
        }

        int targetY = getTargetY(channelIndex, gravity);
        if (animated) {
            epgLayoutManager.onScrollStateChanged(RecyclerView.SCROLL_STATE_DRAGGING);
            recyclerView.smoothScrollBy(0, targetY - epgLayoutManager.getScrollY());
        } else {
            scrollToInternal(epgLayoutManager.getScrollX(), targetY);
        }
    }

    /**
     * Scrolls to the given channelIndex, or the given row to be specific, and also to a given UTC timestamp.
     * Should be used instead of individual calls to scrollToChannel() and scrollToMillisUtc() when animated,
     * to avoid the two smooth-scrolls to block each other.
     *
     * @param millisUtc    the timestamp to scroll to. Will be trimmed to the bounds of the EPG.
     * @param channelIndex the channel to scroll to. Counted as rownumber, so takes filtering into consideration.
     * @param gravity      tells if the focused channel should be positioned to the TOP, CENTER or BOTTOM of the screen. Only these 3 gravity values are supported.
     * @param animated     true, if we should smoothscroll, false if we should jump.
     */
    public void scrollToPosition(long millisUtc, int channelIndex, int gravity, boolean animated) {
        if (epgLayoutManager == null || epgAdapter == null) {
            return;
        }

        int targetX = getTargetX(millisUtc);
        int targetY = getTargetY(channelIndex, gravity);
        if (animated) {
            epgLayoutManager.onScrollStateChanged(RecyclerView.SCROLL_STATE_DRAGGING);
            recyclerView.smoothScrollBy(targetX - epgLayoutManager.getScrollX(), targetY - epgLayoutManager.getScrollY());
        } else {
            scrollToInternal(targetX, targetY);
        }
    }

    /**
     * @param x Horizontal position of an item. Can be used for the hairline for example.
     * @return true, when the given value is between the channel list and the container's end (in the current scroll position).
     */
    public boolean isVisibleInProgramWindow(int x) {
        return isVisibleInProgramWindow(x, x, false);
    }

    /**
     * RTL support is just experimental, please test!
     *
     * @param start     The start position of the item horizontally (e.g. for a program)
     * @param end       The end position of the item horizontally (e.g. for a program)
     * @param inclusive Use true when the program window should be inclusive. With true you would like to know the given item is partially visible at least. With false start and end should be fully visible.
     * @return true, when the given values are visible in the program window (between the channel list's edge and the container's end in the current scroll position).
     */
    public boolean isVisibleInProgramWindow(int start, int end, boolean inclusive) {
        int windowStartEdge = epgLayoutManager.getScrollX() + epgAttributeHolder.getChannelsWidth();
        int windowEndEdge = epgLayoutManager.getScrollX() + recyclerView.getWidth();
        if (!inclusive) {
            return MathExtender.isRangeInsideOf(windowStartEdge, windowEndEdge, start, end);
        } else {
            return MathExtender.isRangeOverlapping(windowStartEdge, windowEndEdge, start, end);
        }
    }

    /**
     * It calculates horizontally the center of the hairline views. Currently the hairline is always centered below the NOW text.
     *
     * @return the X position of the now() hairline. If it’s unknown, -1 is returned.
     */
    public int getHairlineX() {
        int position = -1;
        List<EpgItemHairline> epgItemHairlines = epgAdapter.getEpgItemHairlines();
        if (epgItemHairlines != null && !epgItemHairlines.isEmpty() && epgItemHairlines.get(0) != null) {
            position = Math.round((epgItemHairlines.get(0).getRect().right + epgItemHairlines.get(0).getRect().left) / 2);
        }
        return position;
    }

    /**
     * Calls recyclerView.stopScroll();
     */
    public void stopScroll() {
        recyclerView.stopScroll();
    }

    /**
     * Calls datasource.onBindProgram() for the items visible, without calling notifyDatasetChanged.
     * <p>
     * Good for propagating smaller changes (backgroundcolor or labels) without the flashing of a full notifyDatasetChanged call.
     */
    public void rebindVisibleItems() {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            EpgItem epgItem = (EpgItem) child.getTag(R.id.epg_item);

            if (epgItem != null) {
                epgItem.onBindViewHolder((ViewHolder) child.getTag(R.id.viewholder));
            }
        }
    }

    /**
     * Refreshes the visible content of the EpgView, calling the EpgDataSource's bind methods onto them
     */
    public void refresh() {
        if (epgAdapter != null) {
            epgAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Forces the EpgView to dismiss it's content, and call onRequestChannels and onRequestData on it's EpgDataSource again
     */
    public void reload() {
        if (epgDataManager != null) {
            epgDataManager.reload();
        }

        //Refresh these values because they're statically set from attrs
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) (epgAttributeHolder.getTimebarHeight() * 1.2f)); //Hack to force it to work on setRefresing(true);
    }

    // STATE Handling
    @Override
    public Parcelable onSaveInstanceState() {
        EpgSavedState epgSavedState = new EpgSavedState(super.onSaveInstanceState());

        if (epgLayoutManager != null) {
            if (epgLayoutManager.getScrollX() == 0 && epgLayoutManager.getScrollY() == 0) {
                epgSavedState.X = restoredX;
                epgSavedState.Y = restoredY;
            } else {
                epgSavedState.X = epgLayoutManager.getScrollX();
                epgSavedState.Y = epgLayoutManager.getScrollY();
            }
        }
        return epgSavedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        EpgSavedState epgSavedState = (EpgSavedState) state;
        restoredX = epgSavedState.X;
        restoredY = epgSavedState.Y;
        super.onRestoreInstanceState(epgSavedState.getSuperState());
    }

    protected void scrollToInternal(int x, int y) {
        //Hack to tell the focushandler that there's a scroll going on that it should follow.
        epgLayoutManager.onScrollStateChanged(RecyclerView.SCROLL_STATE_DRAGGING);
        epgLayoutManager.scrollTo(x, y);
        recyclerView.requestLayout();
        post(new Runnable() {
            @Override
            public void run() {
                epgLayoutManager.onScrollStateChanged(RecyclerView.SCROLL_STATE_IDLE);
            }
        });
    }

    protected int getTargetX(long millisUtc) {
        // Enforce bounds
        millisUtc = Math.max(epgAttributeHolder.getStartMillisUtc(), Math.min(millisUtc, epgAttributeHolder.getEndMillisUtc()));

        // Calculate where to scroll & return
        long millisFromStart = millisUtc - epgAttributeHolder.getStartMillisUtc();
        return (int) (millisFromStart / 60000f * epgAttributeHolder.getMinuteWidth());
    }

    protected int getTargetY(int channelIndex, int gravity) {
        // Calculate target
        int remainingHeight = getHeight() - epgAttributeHolder.getTimebarHeight() - epgAttributeHolder.getRowHeight();
        int targetY = channelIndex * epgAttributeHolder.getRowHeight(); // Gravity.TOP

        // Add gravity
        if ((gravity & Gravity.CENTER) == Gravity.CENTER) {
            targetY -= remainingHeight / 2;

        } else if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            targetY -= remainingHeight;
        }

        return targetY;
    }

    protected long getNextUpdateDelay() {
        //Don't need server time here. This is just so that the hairline would move at the same time as the system clock ticks.
        long updateMillis = epgAttributeHolder.getUpdateFrequencySeconds() * 1000;
        long timeSinceLastUpdate = System.currentTimeMillis() % updateMillis;

        return updateMillis - timeSinceLastUpdate;
    }
}
