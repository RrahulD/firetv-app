# VDK EPG

Reusable EPG grid component. As seen in Studio Pay TV:

![](../_images/epg_paytv.jpg)

## Tech Stack

- vdk-core - VDK library core functionalities
- SwipeRefreshLayout
- RecyclerView

## Features

* Data independent
* Customizable layouts
* Horizontal and vertical pagination
* Adjustable page size
* Can scroll through multiple days
* Online and offline filtering
* Rotation support
* Different timezones support
* Empty awareness
* Optional vertical looping
* Placeholders

## Dependencies

```groovy
dependencies {
    implementation 'tv.accedo.vdk:vdk-core:<latest>'
    implementation 'tv.accedo.vdk:vdk-epg:<latest>'
    implementation 'androidx.recyclerview:recyclerview:1.0.0'
}
```

## Basic implementation (How to start)

First add the view to your page layout:

**Sample for adding the EpgView to your layout**

```java
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >

    <hu.accedo.commons.widgets.epg.EpgView
        android:id="@+id/epgView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</RelativeLayout>
```

Then feed it with a datasource on your page:

**Sample for adding a DataSource to your EpgView**

```java
public class EpgFragment extends Fragment {
    private EpgView epgView;
    private DataSource dataSource;

    public View onCreateView(LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_epg, null);

        dataSource = new DataSource();

        epgView = (EpgView) view.findViewById(R.id.epgView);
        epgView.setDataSource(dataSource);

        return view;
    };
}
```

The datasource is a class where you define where the EPG gets its data from, and how it binds it to it's views (Binding is similar to what you do in getView() in an adapter).

**Sample for implementing a DataSource**

```java
 public class DataSource extends EpgDataSource<Channel, Program>{
    @Override
    public Object onRequestChannels(final Callback<List<Channel>> callback) {
        return epgService.fetchAllChannels(new Callback<List<Channel>>(){
            @Override
            public void execute(List<Channel> result) {
                callback.execute(result);
            }
        }, new Callback<Exception>(){
            @Override
            public void execute(Exception result) {
                callback.execute(null);
            }
        });
    }

    @Override
    public Object onRequestData(List<Channel> channels, long fromDate, long toDate, final Callback<Map<Channel, List<Program>>> callback) {
        return epgService.fetchProgramsForChannels(channels, fromDate, toDate, new Callback<Map<Channel, List<Program>>>(){
            @Override
            public void execute(Map<Channel, List<Program>> result) {
                callback.execute(result);
            }
        }, new Callback<Exception>(){
            @Override
            public void execute(Exception result) {
                callback.execute(null);
            }
        });
    }

    @Override
    public long getStartTimeMillis(Program program) {
        return program.getStartDate();
    }

    @Override
    public long getEndTimeMillis(Program program) {
        return program.getEndDate();
    }

    @Override
    public void onBindProgram(ViewHolderProgram viewHolder, Channel channel, Program program) {
        long timestamp = System.currentTimeMillis();
        boolean isNow = getStartTimeMillis(program)<timestamp && timestamp<getEndTimeMillis(program);

        viewHolder.textView.setText(program.getName());
        viewHolder.textView.setBackgroundColor(isNow? 0xFFD1C4E9 : 0xFFE2E6FB);
    }

    @Override
    public void onBindChannel(ViewHolderChannel viewHolder, Channel channel) {
        ImageLoader.load(viewHolder.imageView, channel.getLogo());
    }
}
```

Once this is done, you have a fully functional EPG in your app, congratulations! :)

## Customization

### Basic customization (DataSource overrides)

The most basic way you can customise your EPG is to tweak it's looks by code, in the onBind() methods of it's DataSource. Available onBind calls are:

**List of overridable onBind() calls**

```java
void onBindProgram(ViewHolderProgram viewHolder, Channel channel, Program program);
void onBindChannel(ViewHolderChannel viewHolder, Channel channel);
void onBindTimeBar(ViewHolderTimebar viewHolder, long timestamp);
void onBindHairline(ViewHolderHairline viewHolder, boolean isTimebarPart);
void onBindPlaceholder(ViewHolderPlaceholder viewHolder, long startTime, long endTime, boolean loaded);
```

There are other calls you can override that are:

**List of other overridable DataSource methods**

```java
 //Provides the text that is going to be displayed in the timebar at the top of the screen
String getTimeBarText(long timestamp)

//The EPG will call this for all channels. If you return false for a channel, the EPG won't display that channel. Can be used for filtering.
boolean isChannelAllowed(Channel channel)
```

### Advanced customization (Layout tweaks)

You will probably notice that the EPG has a very nice Saints purple look. To easily change this, what you can do is copy the layout files the EPG is using from the library to your project, and customize the colors there.

The library will automatically use the layout files in your file, instead of it's own ones.

The layout files are located at:

[https://bitbucket.org/accedo/vdkmob-android/src/master/vdk-epg/src/main/res/layout/](https://bitbucket.org/accedo/vdkmob-android/src/master/vdk-epg/src/main/res/layout/)

### Full customization (Layout override)

If you want to add extra items into these layouts, or completely change how a cell looks like (for example, display the channel-name in the channel field aswell), that is also possible.

What you need to do is create a layout to your liking, and override the method where the EPG creates its cells.

That can be done in the DataSource, by overriding some of the following methods:

**List of overridable ViewHolder creator methods**

```java
ViewHolderProgram onCreateProgramViewHolder(ViewGroup viewGroup);
ViewHolderChannel onCreateChannelViewHolder(ViewGroup viewGroup);
ViewHolderTimebar onCreateTimeBarViewHolder(ViewGroup viewGroup);
ViewHolderHairline onCreateHairlineViewHolder(ViewGroup viewGroup);
ViewHolderPlaceholder onCreatePlaceholderViewHolder(ViewGroup viewGroup);
```

What you have to do in these methods, is inflate your custom layout, and create a ViewHolder for your view. You may either extend one of the predefined viewholders, or even create a completely new one.

**Sample for overriding an epg cell's ViewHolder**

```java
@Override
public ViewHolderProgram onCreateProgramViewHolder(ViewGroup viewGroup) {
    return new ViewHolderCustomProgram(viewGroup);
}

public static class ViewHolderCustomProgram extends ViewHolderProgram {
    public final ImageView imageView;
    public final FrameLayout frameLayout;

    public ViewHolderCustomProgram(ViewGroup viewGroup) {
        super(viewGroup, R.layout.view_epg_default_program);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
        frameLayout = (FrameLayout) itemView.findViewById(R.id.frameLayout);
    }
}
```

After this is done, the EPG will inflate, and automatically pass your custom ViewHolders into the onBind() methods of your datasource.

### Extra customization features (XML params)

There are extra parameters you can define through the layout file for the EPG. A list of those can be found here:

**Sample EpgView layout featuring all the available XML parameters**

```
<hu.accedo.commons.widgets.epg.EpgView
    android:id="@+id/epgView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"

    app:epgview_channels_width="96dp"                    //Width of the channels column
    app:epgview_timebar_height="32dp"                    //Height of the time display at the top
    app:epgview_timebar_minute_stepping="30"             //Show text on time display every 30 mintues
    app:epgview_hairline_text_width="42dp"               //The width of the NOW marker
    app:epgview_minute_width="4dp"                       //The width of a minute, basically affect how condensed the EPG is
    app:epgview_hour_offset="0"                          //Tells at what time a day should start. Default is 00:00
    app:epgview_row_height="64dp"                        //Height of a program row
    app:epgview_update_frequency_seconds="60"            //Refresh the view every x seconds
    app:epgview_page_size_vertical="20"                  //Tells how many rows the EPG should load at once
    app:epgview_page_size_horizontal="_24h"              //Tells how many hours the EPG should load at once. Is an enum of predefined values.
    app:epgview_view_cache_size="100"                    //Advanced optimalisation. The EPG keeps 100 cells in memory.
    app:epgview_progress_view="@+id/progressBar"         //See "Empty awareness & progressbars"
    app:epgview_empty_view="@+id/textView"               //See "Empty awareness & progressbars"
    app:epgview_secondary_progress_enabled="true"        //Enables-disables slide-in progressbar when loading pages
    app:epgview_days_forward="6"                         //How many days forward the EPG is scrollable
    app:epgview_days_backwards="6"                       //How many days backwards the EPG is scrollable
    app:epgview_extra_padding_bottom="0dp"               //The EPG keeps this many free space at the bottom. Useful for transparent controls
    app:epgview_sticky_programs_enabled="false"          //When enabled, the EPG will stick the titles of the programs on-screen
    app:epgview_timebar_label_enabled="false"            //When true, the EPG displays an extra Timebar item in the top-left corner, which is always visible, and can be used to display additional information.
    app:epgview_diagonal_scroll_enabled="true"           //Enables or disables diagonal scrolling. Default is true.
    app:epgview_diagonal_scroll_detection_offset="16dp"  //If diagonal scrolling is disabled, this is the distance that is used to decide the direction of a diagonal scroll. Can be used for fine-tuning if flings feel sticky.
    app:epgview_placeholders_enabled="false"             //When true, the EPG will generate extra cells for places that don't have any programs
    app:epgview_placeholders_page_size="_4h"            //Tells how many hours wide a placeholder can be at maximum. If longer, the EPG will cut placeholders up at exact hours of the day. Is an enum of predefined values.
    app:epgview_looping_enabled="false"                  //When true, the EPG will loop vertically, resulting in an infinite scroll.
    app:epgview_threadsafe="true"                        //When true, the EPG will post calls from the callback provided in it's datasource to the main thread, otherwise it will call them directly.
    app:epgview_view_clipping_enabled="false">           //When true, the EPG will make sure to trim cells that may overlap. This is useful for semi-transparent designs, however it has a slight performance cost.
```

## Empty awareness & progressbars

Making the EPG Empty aware is fairly simple. What you have to do is define what views to show while loading, and what view to show when the loading has failed, and add them to the EPG. Everything else will be handled internally for you.

This can be done in the layout XML file of the page.

**Sample for an empty aware EpgView layout**

```java
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >

    <hu.accedo.commons.widgets.epg.EpgView
        android:id="@+id/epgView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:epgview_empty_view="@+id/textView"
        app:epgview_progress_view="@+id/progressBar" >

        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

        <TextView
            android:id="@+id/textView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:text="No data." />
    </hu.accedo.commons.widgets.epg.EpgView>
</RelativeLayout>
```

Of course, you are free to add more complex views here, for example a refresh button.

## EpgView direct methods

**List of EpgView direct methods**

```
//Register a callback to be invoked when the EpgView is scrolled to another day.
void setOnDayChangeListener(OnDayChangeListener onDayChangeListener)

//Get the SwipeRefreshLayout the EPG shows while fetching additional pages. Can be used to customize and retheme this view.
SwipeRefreshLayout getSwipeRefreshLayout()

//Scrolls back the EPG to the current position
void scrollToHairline(boolean animated)

//Scrolls the EPG to a specific day and hour
void scrollToDayOffset(int dayOffset, int hour, boolean animated)

//Stops the EPG from scrolling
void stopScroll()

//Refreshes all the views in the EPG, calling onBind() onto them
void refresh()

//Wipes the contents of the EPG, and reloads all data
void reload()

//Returns the holder for the customization parameters specified in the layout XMLs. Can be used to override configs at runtime.
public EpgAttributeHolder getAttributes()
```

## QA Reference

Viki contains an EPG page using the following features:

* Data independent, two datasources available (mock and AccedoOVP, cannot be switched runtime though)
* Horizontal pagination by 24h, Vertical pagination by 30 rows.
* Empty awareness, if no channels are returned, an error message and a retry button are shown.
* Rotation support, the component handles device rotation gracefully.
* The EPG uses sticky programs, where visible programs are cut on the left if bigger than the screen, for readability reasons.
* EPG shows 7 days forward, and 7 days backwards, with a total of 15 days shown.
