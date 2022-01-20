/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.widgets.epg;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import hu.accedo.commons.tools.Callback;

public abstract class EpgDataSource<Channel, Program> {
    public static final SimpleDateFormat DEFAULT_TIMEBAR_DATEFORMAT = new SimpleDateFormat("H:mm", Locale.ENGLISH);

    static {
        DEFAULT_TIMEBAR_DATEFORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    protected final EpgView epgView;

    public EpgDataSource(EpgView epgView) {
        this.epgView = epgView;
    }

    /**
     * The EPG will call this method to figure out the starttime of a Program object.
     *
     * @param program one of the Program items provided to the EPG in the onRequestData() method.
     * @return should return the startTime of the program in UTC.
     */
    public abstract long getStartTimeMillis(Program program, Channel channel);

    /**
     * The EPG will call this method to figure out the endtime of a Program object.
     *
     * @param program one of the Program items provided to the EPG in the onRequestData() method.
     * @return should return the endTime of the program in UTC.
     */
    public abstract long getEndTimeMillis(Program program);

    /**
     * The EPG will call this method while initialising or reloading, to get the list of all channels it should display.
     *
     * @param callback should be called with the list of channels fetched, or with null if channel fetching has failed.
     * @return the AsyncTask or other sort of Cancellable or FutureTask used to make this request. The EPG might try to cancel requests when they're not needed anymore.
     */
    public abstract Object onRequestChannels(Callback<List<Channel>> callback);

    /**
     * The EPG will call this method when it needs a tile of programs.
     *
     * @param channels the list of channels the EPG needs programs for.
     * @param fromDate the date in UTC that the EPG needs programs from.
     * @param toDate   the date in UTC that the EPG needs programs to.
     * @param callback should be called with the list of programs fetched, or with null if program fetching has failed.
     * @return the AsyncTask or other sort of Cancellable or FutureTask used to make this request. The EPG might try to cancel requests when they're not needed anymore.
     */
    public abstract Object onRequestData(List<Channel> channels, long fromDate, long toDate, Callback<Map<Channel, List<Program>>> callback);

    /**
     * The EPG will call this for all channels, to determine if it should show or hide the given channel. Can be used for filtering.
     *
     * @param channel one of the Channel items provided to the EPG in the onRequestChannels() method.
     * @return true if the channel should be displayed, false otherwise
     */
    public boolean isChannelAllowed(Channel channel) {
        return true;
    }

    /**
     * @param timestamp the timestamp to format on the timebar. The start of the timebar is counted as 0.
     * @return the text that is going to be displayed in the timebar at the top of the screen
     */
    public String getTimeBarText(long timestamp) {
        return DEFAULT_TIMEBAR_DATEFORMAT.format(timestamp);
    }

    /**
     * @param channel the channel to find the Adapter index for. Useful for calling epgView.scrollToChannel()
     * @return the adapter-index of the channel, or -1 if not found.
     */
    public int getChannelIndex(@NonNull Channel channel) {
        if (epgView.epgAdapter != null) {
            return epgView.epgAdapter.getChannelIndex(channel, true);
        }
        return -1;
    }

    //Binds

    /**
     * @param viewHolderProgram the viewholder of a view representing a Program.
     * @param channel           one of the Channel items provided to the EPG in the onRequestChannels() method.
     * @param program           one of the Program items provided to the EPG in the onRequestData() method.
     */
    public abstract void onBindProgram(ViewHolderProgram viewHolderProgram, Channel channel, Program program);

    /**
     * @param viewHolderChannel the viewholder of a view representing a Channel.
     * @param channel           one of the Channel items provided to the EPG in the onRequestChannels() method.
     */
    public abstract void onBindChannel(ViewHolderChannel viewHolderChannel, Channel channel);

    /**
     * @param viewHolderTimebar the viewholder of a view representing a time label on the timebar.
     * @param timestamp         the timestamp to display on the timebar. The start of the timebar is counted as 0. The default implementation formats this with ge.tTimeBarText()
     */
    public void onBindTimeBar(ViewHolderTimebar viewHolderTimebar, long timestamp) {
        if (viewHolderTimebar.textView != null) {
            viewHolderTimebar.textView.setText(timestamp >= 0 ? getTimeBarText(timestamp) : "");
        }
    }

    /**
     * @param viewHolderHairline the viewholder of a view representing either the top or the line part of the hairline.
     * @param isTimebarPart      if true, then this view is the part of the hairline containing the "NOW" text at the top
     */
    public void onBindHairline(ViewHolderHairline viewHolderHairline, boolean isTimebarPart) {
        if (viewHolderHairline.textViewNow != null) {
            viewHolderHairline.textViewNow.setText("NOW");
            viewHolderHairline.textViewNow.setVisibility(isTimebarPart ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * @param viewHolderPlaceholder the viewholder of a view representing a time period with no programs on.
     * @param channel               one of the Channel items provided to the EPG in the onRequestChannels() method.
     * @param startTime             the start time of the empty duration.
     * @param endTime               the end time of the empty duration.
     * @param loaded                false, if the space is empty because the data there is still loading. True if its really a period with nothing on.
     */
    public void onBindPlaceholder(ViewHolderPlaceholder viewHolderPlaceholder, Channel channel, long startTime, long endTime, boolean loaded) {
    }

    //Creates

    /**
     * @param viewGroup the parent view of the ViewHolder created.
     * @return the ViewHolder created, that will later be fed into onBind.
     */
    public ViewHolderProgram onCreateProgramViewHolder(ViewGroup viewGroup) {
        return new ViewHolderProgram(viewGroup, R.layout.view_epg_default_program);
    }

    /**
     * @param viewGroup the parent view of the ViewHolder created.
     * @return the ViewHolder created, that will later be fed into onBind.
     */
    public ViewHolderChannel onCreateChannelViewHolder(ViewGroup viewGroup) {
        return new ViewHolderChannel(viewGroup, R.layout.view_epg_default_channel);
    }

    /**
     * @param viewGroup the parent view of the ViewHolder created.
     * @return the ViewHolder created, that will later be fed into onBind.
     */
    public ViewHolderTimebar onCreateTimeBarViewHolder(ViewGroup viewGroup) {
        return new ViewHolderTimebar(viewGroup, R.layout.view_epg_default_timebar);
    }

    /**
     * @param viewGroup the parent view of the ViewHolder created.
     * @return the ViewHolder created, that will later be fed into onBind.
     */
    public ViewHolderHairline onCreateHairlineViewHolder(ViewGroup viewGroup) {
        return new ViewHolderHairline(viewGroup, R.layout.view_epg_default_hairline);
    }

    /**
     * @param viewGroup the parent view of the ViewHolder created.
     * @return the ViewHolder created, that will later be fed into onBind.
     */
    public ViewHolderPlaceholder onCreatePlaceholderViewHolder(ViewGroup viewGroup) {
        return new ViewHolderPlaceholder(viewGroup, R.layout.view_epg_default_placeholder);
    }

    // NotifyDataSetChanged
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    /**
     * Notifies the attached observers that the underlying data is no longer valid
     * or available. Once invoked this adapter is no longer valid and should
     * not report further data set changes.
     */
    public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }

    // ViewHolders
    public static class ViewHolderProgram extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolderProgram(ViewGroup viewGroup, int layoutId) {
            this(inflate(viewGroup, layoutId));
        }

        public ViewHolderProgram(View itemView) {
            super(itemView);
            textView = (TextView) this.itemView.findViewById(R.id.textView);
        }
    }

    public static class ViewHolderPlaceholder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolderPlaceholder(ViewGroup viewGroup, int layoutId) {
            this(inflate(viewGroup, layoutId));
        }

        public ViewHolderPlaceholder(View itemView) {
            super(itemView);
            textView = (TextView) this.itemView.findViewById(R.id.textView);
        }
    }

    public static class ViewHolderChannel extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolderChannel(ViewGroup viewGroup, int layoutId) {
            this(inflate(viewGroup, layoutId));
        }

        public ViewHolderChannel(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public static class ViewHolderTimebar extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolderTimebar(ViewGroup viewGroup, int layoutId) {
            this(inflate(viewGroup, layoutId));
        }

        public ViewHolderTimebar(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    public static class ViewHolderHairline extends RecyclerView.ViewHolder {
        public TextView textViewNow;
        public View viewLine;

        public ViewHolderHairline(ViewGroup viewGroup, int layoutId) {
            this(inflate(viewGroup, layoutId));
        }

        public ViewHolderHairline(View itemView) {
            super(itemView);
            textViewNow = (TextView) itemView.findViewById(R.id.textViewNow);
            viewLine = itemView.findViewById(R.id.viewLine);
        }
    }

    protected static View inflate(ViewGroup viewGroup, int layoutId) {
        return LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
    }
}
