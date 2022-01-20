/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.widgets.epg;

import android.database.DataSetObserver;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.accedo.commons.threading.ThreadingTools;
import hu.accedo.commons.tools.Callback;
import hu.accedo.commons.tools.DateTools;
import hu.accedo.commons.tools.MathExtender;

public class EpgDataManager<Channel, Program> {
    // Params
    private EpgView epgView;
    private RecyclerView recyclerView;
    private EpgDataSource<Channel, Program> epgDataSource;
    private EpgAdapter<Channel, Program> epgAdapter;
    private EpgAttributeHolder attrs;
    private LoadingListener loadingListener;

    // Pagination info
    private Set<String> pageMap = new HashSet<>();

    // Threading
    private Object channelsCancellable;
    private HashMap<String, Object> programCancellables = new HashMap<>();

    // Scrolling
    private int firstVisiblePageX;
    private int lastVisiblePageX;
    private int firstVisibleRow;
    private int lastVisibleRow;

    public EpgDataManager(EpgView epgView,
                          EpgDataSource<Channel, Program> epgDataSource,
                          EpgAdapter<Channel, Program> epgAdapter,
                          LoadingListener loadingListener) {
        this.epgView = epgView;
        this.recyclerView = epgView.getRecyclerView();
        this.epgDataSource = epgDataSource;
        this.epgAdapter = epgAdapter;
        this.attrs = epgView.getAttributes();
        this.loadingListener = loadingListener;

        this.firstVisibleRow = 0;
        this.lastVisibleRow = (attrs.getTimebarHeight() + recyclerView.getHeight()) / attrs.getRowHeight() + 1;

        this.epgDataSource.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                cancelAll();

                // Start fetching channels, or programs
                if (EpgDataManager.this.epgAdapter.isEmpty()) {
                    fetchChannels();
                } else {
                    EpgDataManager.this.epgAdapter.filter();
                    EpgDataManager.this.epgAdapter.notifyDataSetChanged();
                    onVisibleRowsChanged(firstVisiblePageX, lastVisiblePageX, firstVisibleRow, lastVisibleRow, true);
                }
            }
        });
    }

    public void reload() {
        cancelAll();
        pageMap.clear();
        epgAdapter.clear();
        fetchChannels();
    }

    public void cancelAll() {
        recyclerView.stopScroll();

        // Channels cancel
        if (channelsCancellable != null) {
            ThreadingTools.tryCancel(channelsCancellable);
            channelsCancellable = null;
        }

        // Programs cancel
        for (Map.Entry<String, Object> entry : programCancellables.entrySet()) {
            ThreadingTools.tryCancel(entry.getValue());
            pageMap.remove(entry.getKey());
        }
        programCancellables.clear();
        loadingListener.onLoadFinished(epgAdapter.isEmpty());
    }

    public void onVisibleRowsChanged(int firstVisiblePageX, int lastVisiblePageX, int firstVisibleRow, int lastVisibleRow, boolean forceRecheck) {
        if (!recyclerView.hasWindowFocus()) {
            return;
        }
        if (!forceRecheck &&
                this.firstVisiblePageX == firstVisiblePageX &&
                this.lastVisiblePageX == lastVisiblePageX &&
                this.firstVisibleRow == firstVisibleRow &&
                this.lastVisibleRow == lastVisibleRow) {
            return;
        }

        this.firstVisiblePageX = firstVisiblePageX;
        this.lastVisiblePageX = lastVisiblePageX;
        this.firstVisibleRow = firstVisibleRow;
        this.lastVisibleRow = lastVisibleRow;

        if (epgAdapter.isEmpty()) {
            fetchChannels();
        } else {
            for (int pageX = firstVisiblePageX; pageX <= lastVisiblePageX; pageX++) {
                for (int rowY = firstVisibleRow; rowY <= lastVisibleRow; rowY++) {
                    int unloopedRowY = MathExtender.mod(epgAdapter.filteredIndexToRealIndex(rowY), epgAdapter.getChannelCount(false));
                    int pageY = MathExtender.divFloor(unloopedRowY, attrs.getPageSizeVertical());

                    String key = getKey(pageX, pageY);
                    if (!pageMap.contains(key)) {
                        // Mark as fetched
                        pageMap.add(key);

                        // Fetch
                        fetchPrograms(pageX, pageY);
                    }
                }
            }
        }
    }

    private void fetchChannels() {
        if (channelsCancellable != null) {
            return;
        }

        loadingListener.onLoadStarted(true);
        ThreadingTools.tryCancel(channelsCancellable);
        channelsCancellable = epgDataSource.onRequestChannels(new Callback<List<Channel>>() {
            @Override
            public void execute(final List<Channel> channels) {
                if (attrs.isThreadSafe()) {
                    epgView.post(new Runnable() {
                        @Override
                        public void run() {
                            onChannels(channels);
                        }
                    });
                } else {
                    onChannels(channels);
                }
            }
        });
    }

    private void fetchPrograms(int pageX, int pageY) {
        long pageSizeMillis = attrs.getPageSizeHorizontal() * DateTools.ONE_HOUR_MILLIS;

        final long fromDate = attrs.getFirstDayStartMillisUTC() + pageX * pageSizeMillis;
        final long toDate = fromDate + pageSizeMillis;
        final String threadName = getKey(pageX, pageY);

        loadingListener.onLoadStarted(false);
        ThreadingTools.tryCancel(programCancellables.get(threadName));
        Object cancellable = epgDataSource.onRequestData(getChannelsForPage(pageY), fromDate, toDate, new Callback<Map<Channel, List<Program>>>() {
            @Override
            public void execute(final Map<Channel, List<Program>> result) {
                if (attrs.isThreadSafe()) {
                    epgView.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrograms(threadName, result);
                        }
                    });
                } else {
                    onPrograms(threadName, result);
                }
            }
        });
        programCancellables.put(threadName, cancellable);
    }

    // -------------------- UTILITY METHODS --------------------
    protected void onChannels(final List<Channel> channels) {
        channelsCancellable = null;

        if (channels != null) {
            // Store locally & in adapter
            pageMap.clear();
            epgAdapter.setChannels(channels);

            // ProgressBar & animate
            loadingListener.onLoadFinished(channels == null || channels.isEmpty());
            loadingListener.onChannelsLoaded();

            // Start fetching programs after everything settles
            onVisibleRowsChanged(firstVisiblePageX, lastVisiblePageX, firstVisibleRow, lastVisibleRow, true);
        } else {
            loadingListener.onLoadFinished(true);
        }
    }

    protected void onPrograms(final String threadName, final Map<Channel, List<Program>> result) {
        // Progressbar
        programCancellables.remove(threadName);
        if (programCancellables.isEmpty()) {
            loadingListener.onLoadFinished(false);
        }

        // Store
        if (result == null) {
            pageMap.remove(threadName);
        } else {
            epgAdapter.addPrograms(result);
        }
    }

    protected boolean isLoaded(int pixelX, int rowY) {
        int pageSizeHorizontalPixels = attrs.getPageSizeHorizontal() * 60 * attrs.getMinuteWidth();
        int pageX = pixelX / pageSizeHorizontalPixels;
        int pageY = MathExtender.divFloor(rowY, attrs.getPageSizeVertical());
        return pageMap.contains(getKey(pageX, pageY));
    }

    protected List<Channel> getChannelsForPage(int page) {
        int fromIndex = page * attrs.getPageSizeVertical();
        int toIndex = Math.min(fromIndex + attrs.getPageSizeVertical(), epgAdapter.getChannelCount(false));

        ArrayList<Channel> result = new ArrayList<>();
        for (int i = fromIndex; i < toIndex; i++) {
            result.add((Channel) epgAdapter.getEpgItemChannel(i, false).getChannel());
        }
        return result;
    }

    protected String getKey(int pageX, int pageY) {
        return "" + pageX + "x" + pageY;
    }

    public static interface LoadingListener {
        public void onChannelsLoaded();

        public void onLoadStarted(boolean isEmpty);

        public void onLoadFinished(boolean isEmpty);
    }
}
