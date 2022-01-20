/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.widgets.epg;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import hu.accedo.commons.tools.MathExtender;
import hu.accedo.commons.types.IndexedHashMap;
import hu.accedo.commons.widgets.epg.adapter.EpgItem;
import hu.accedo.commons.widgets.epg.adapter.EpgItemChannel;
import hu.accedo.commons.widgets.epg.adapter.EpgItemHairline;
import hu.accedo.commons.widgets.epg.adapter.EpgItemProgram;
import hu.accedo.commons.widgets.epg.adapter.EpgItemProgramIterator;
import hu.accedo.commons.widgets.epg.adapter.EpgItemTimeBar;

/**
 * Multitype adapter responsible for feeding EpgLayoutManager with data.
 */
public class EpgAdapter<Channel, Program> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEMTYPE_PROGRAM = 0;
    public static final int ITEMTYPE_CHANNEL = 1;
    public static final int ITEMTYPE_TIMEBAR = 2;
    public static final int ITEMTYPE_HAIRLINE = 3;
    public static final int ITEMTYPE_PLACEHOLDER = 4;

    private IndexedHashMap<EpgItemChannel, TreeMap<Integer, EpgItemProgram>> programItems = new IndexedHashMap<>();
    private ArrayList<EpgItemHairline> hairlineItems = new ArrayList<>();
    private ArrayList<EpgItemTimeBar> timebarItems = new ArrayList<>();

    private SparseArray<EpgItem> itemsByIndex = new SparseArray<>();
    private Map<EpgItem, Integer> indexByItem = new HashMap<>();

    private List<Integer> filteredIndexes = new ArrayList<>();

    private Context context;
    private EpgView epgView;
    private EpgDataSource<Channel, Program> epgDataSource;
    private EpgAttributeHolder attrs;
    private EpgItemProgramIterator epgItemProgramIterator = new EpgItemProgramIterator();

    public void setChannels(List<Channel> channels) {
        // Channels are the base of everything, if they change we clear.
        clear();

        // Add timebar and hairline first
        for (int i = 0; i < 60 * 24 * attrs.getTotalDayCount(); i += attrs.getTimebarMinuteStepping()) {
            timebarItems.add(new EpgItemTimeBar<>(
                    attrs,
                    epgDataSource,
                    attrs.getChannelsWidth() + i * attrs.getMinuteWidth(),
                    attrs.getChannelsWidth() + (i + attrs.getTimebarMinuteStepping()) * attrs.getMinuteWidth(),
                    i * 60000L + attrs.getHourOffset() * 60 * 60 * 1000L + attrs.getMinuteOffset() * 60 * 1000L)); //Minutes in millis + houroffset in millis
        }
        timebarItems.add(new EpgItemTimeBar<>(attrs, epgDataSource, 0, attrs.getChannelsWidth(), -1));

        // Add hairline
        if (epgDataSource != null) {
            hairlineItems.add(new EpgItemHairline<>(context, attrs, epgDataSource, false));
            hairlineItems.add(new EpgItemHairline<>(context, attrs, epgDataSource, true));
        }

        // Finally add the channels
        for (Channel channel : channels) {
            programItems.put(new EpgItemChannel<>(attrs, epgDataSource, channel), new TreeMap<Integer, EpgItemProgram>());
        }

        // Update
        if (attrs.isPlaceholdersOnFirstLoadEnabled()) {
            addPlaceholdersIfNecessary();
        }
        index();
        filter();
        notifyDataSetChanged();
    }

    public void addPrograms(Map<Channel, List<Program>> result) {
        // Iterate channels
        for (Map.Entry<Channel, List<Program>> entry : result.entrySet()) {
            Channel channel = entry.getKey();
            List<Program> programs = entry.getValue();

            // Iterate programs of channel
            if (channel != null && programs != null) {
                for (EpgItemChannel channelItem : programItems.keySet()) {
                    if (channel.equals(channelItem.getChannel())) {
                        TreeMap<Integer, EpgItemProgram> treeMap = programItems.get(channelItem);

                        // Add programitems
                        for (Program program : programs) {
                            EpgItemProgram epgItemProgram = new EpgItemProgram<>(attrs, epgDataSource, channel, program);
                            treeMap.put(epgItemProgram.getRect().left, epgItemProgram);
                        }

                        break;
                    }
                }
            }
        }

        // Update
        addPlaceholdersIfNecessary();
        index();
        notifyDataSetChanged();
    }

    public EpgAdapter(EpgView epgView, EpgDataSource<Channel, Program> epgDataSource) {
        this.epgView = epgView;
        this.context = epgView.getContext();
        this.attrs = epgView.getAttributes();
        this.epgDataSource = epgDataSource;
        setHasStableIds(true);
    }

    public void clear() {
        programItems.clear();
        hairlineItems.clear();
        timebarItems.clear();
        itemsByIndex.clear();
        indexByItem.clear();
        filteredIndexes.clear();
        notifyDataSetChanged();
    }

    public EpgItemProgramIterator getEpgItemProgramsIterator(int channelIndex, final int startX, final int endX, boolean filter) {
        return epgItemProgramIterator.init(programItems.get(getEpgItemChannel(channelIndex, filter)), startX, endX, true);
    }

    public EpgItemChannel getEpgItemChannel(int channelIndex, boolean filter) {
        channelIndex = MathExtender.mod(channelIndex, getChannelCount(filter));

        return programItems.getKey(filter ? filteredIndexes.get(channelIndex) : channelIndex);
    }

    public int filteredIndexToRealIndex(int index) {
        if (filteredIndexes.isEmpty()) {
            return index;
        }
        return filteredIndexes.get(MathExtender.mod(index, getChannelCount(true)));
    }

    public List<EpgItemHairline> getEpgItemHairlines() {
        return hairlineItems;
    }

    public List<EpgItemTimeBar> getEpgItemTimebars() {
        return timebarItems;
    }

    public int getItemIndex(EpgItem item) {
        return indexByItem.get(item);
    }

    public boolean isEmpty() {
        return itemsByIndex.size() == 0;
    }

    public int getChannelCount(boolean filter) {
        return filter ? filteredIndexes.size() : programItems.size();
    }

    public int getChannelIndex(@NonNull Channel channel, boolean filter) {
        int channelCount = getChannelCount(filter);
        for (int i = 0; i < channelCount; i++) {
            if (channel.equals(getEpgItemChannel(i, filter).getChannel())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public long getItemId(int position) {
        return itemsByIndex.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return itemsByIndex.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemsByIndex.get(position).getItemViewType();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        EpgItem epgItem = itemsByIndex.get(position);

        viewHolder.itemView.setTag(R.id.position, position);
        viewHolder.itemView.setTag(R.id.epg_item, epgItem);
        viewHolder.itemView.setTag(R.id.viewholder, viewHolder);

        epgItem.onBindViewHolder(viewHolder);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ITEMTYPE_CHANNEL:
                return epgDataSource.onCreateChannelViewHolder(viewGroup);
            case ITEMTYPE_TIMEBAR:
                return epgDataSource.onCreateTimeBarViewHolder(viewGroup);
            case ITEMTYPE_HAIRLINE:
                return epgDataSource.onCreateHairlineViewHolder(viewGroup);
            case ITEMTYPE_PLACEHOLDER:
                return epgDataSource.onCreatePlaceholderViewHolder(viewGroup);
            default:
                return epgDataSource.onCreateProgramViewHolder(viewGroup);
        }
    }

    public void filter() {
        filteredIndexes.clear();

        for (int i = 0; i < programItems.size(); i++) {
            Channel channel = (Channel) programItems.getKey(i).getChannel();

            if (epgDataSource.isChannelAllowed(channel)) {
                filteredIndexes.add(i);
            }
        }
    }

    protected void index() {
        itemsByIndex.clear();
        indexByItem.clear();

        for (EpgItem epgItem : timebarItems) {
            itemsByIndex.put(itemsByIndex.size(), epgItem);
            indexByItem.put(epgItem, indexByItem.size());
        }

        for (EpgItem epgItem : hairlineItems) {
            itemsByIndex.put(itemsByIndex.size(), epgItem);
            indexByItem.put(epgItem, indexByItem.size());
        }

        for (EpgItemChannel epgItem : programItems.keySet()) {
            itemsByIndex.put(itemsByIndex.size(), epgItem);
            indexByItem.put(epgItem, indexByItem.size());
        }

        for (EpgItemChannel epgItemChannel : programItems.keySet()) {
            for (EpgItem epgItem : programItems.get(epgItemChannel).values()) {
                itemsByIndex.put(itemsByIndex.size(), epgItem);
                indexByItem.put(epgItem, indexByItem.size());
            }
        }
    }

    protected void addPlaceholdersIfNecessary() {
        if (!attrs.isPlaceholdersEnabled()) {
            return;
        }

        for (EpgItemChannel epgItemChannel : programItems.keySet()) {
            int channelIndex = programItems.getIndex(epgItemChannel);
            Channel channel = (Channel) programItems.getKey(channelIndex).getChannel();
            TreeMap<Integer, EpgItemProgram> treeMap = programItems.get(epgItemChannel);

            // Collect existing placeholders colliding with programs:
            // We have to make sure all related placeholders will be removed, special regard to chunks.
            EpgItemProgram lastProgramItem = null;
            EpgItemProgram lastPlaceholderItem = null;
            Set<EpgItemProgram> toRemove = new HashSet<>();
            for (EpgItemProgram item : treeMap.values()) {
                // Keep the last known things whatever we found
                if (item.getProgram() == null) {
                    item.setLoaded(epgView.epgDataManager.isLoaded(item.getRect().left, channelIndex));
                    lastPlaceholderItem = item;
                } else {
                    lastProgramItem = item;
                }

                // Mark to remove only when we can safely compare them AND their periods overlapped
                if (lastProgramItem != null && lastPlaceholderItem != null && MathExtender.isRangeOverlapping(
                        lastProgramItem.getRect().left,
                        lastProgramItem.getRect().right,
                        lastPlaceholderItem.getRect().left,
                        lastPlaceholderItem.getRect().right
                )) {
                    toRemove.add(lastPlaceholderItem);
                }
            }

            // Remove collected placeholders
            for (Iterator<EpgItemProgram> iterator = programItems.get(epgItemChannel).values().iterator(); iterator.hasNext(); ) {
                if (toRemove.contains(iterator.next())) {
                    iterator.remove();
                }
            }

            // Add new placeholders for new gaps
            int endPixel = attrs.getChannelsWidth() + 24 * 60 * attrs.getMinuteWidth() * attrs.getTotalDayCount();
            int lastEnd = attrs.getChannelsWidth();
            ArrayList<EpgItemProgram> programs = new ArrayList<>(treeMap.values());
            for (EpgItemProgram program : programs) {
                if (program.getRect().left > lastEnd + 1) {
                    addPlaceholdersForInterval(treeMap, channel, channelIndex, lastEnd, program.getRect().left);
                }
                lastEnd = program.getRect().right;
            }
            addPlaceholdersForInterval(treeMap, channel, channelIndex, lastEnd, endPixel);
        }
    }

    protected void addPlaceholdersForInterval(TreeMap<Integer, EpgItemProgram> into, Channel channel, int channelIndex, int from, int to) {
        if (from < 0 || to < 0 || to <= from) {
            return;
        }
        int pageSizePixels = attrs.getPlaceholdersPageSize() * 60 * attrs.getMinuteWidth();

        //If our divider is too big, cut down to sizable chunks defined by placeholdersPageSize, by default 4 hours.
        //We'll be pushing "from" as we cut. So that the last part is placed exactly the same as normal items
        if (to - from > pageSizePixels) {
            int firstDivider = offsetFloor(from, pageSizePixels, attrs.getChannelsWidth()) + pageSizePixels;
            for (int divider = firstDivider; divider < to; divider += pageSizePixels) {
                into.put(from, new EpgItemProgram<>(attrs, epgDataSource, channel, from, divider, epgView.epgDataManager.isLoaded(from, channelIndex)));
                from = divider;
            }
        }

        into.put(from, new EpgItemProgram<>(attrs, epgDataSource, channel, from, to, epgView.epgDataManager.isLoaded(from, channelIndex)));
    }

    protected int offsetFloor(int value, int divBy, int offset) {
        return MathExtender.floor(value - offset, divBy) + offset;
    }
}
