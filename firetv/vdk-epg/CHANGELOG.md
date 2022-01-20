# Changelog

### v4.1.8 (2021-05-27)

* Fix calculating start and end times when timezone set programmatically. This was also causing wrong dates to show “today/yesterday/tomorrow”.

### v4.1.7 (2021-03-29)

* Fix updating startMillisUtc on changing day range programmatically

### v4.1.6 (2021-01-07)

* Adding emptiness check on epgItemHairlines to getHairlineX in EpgView

### v4.1.5 (2020-10-08)

* Force view to be remeasured to avoid text overflow

### v4.1.4 (2020-06-30)

* Updating the usage of timebarOffsetHorizontal, avoiding to use it on the date indicator

### v4.1.3 (2020-06-12)

* Fixed issue when extreme long programs (like 24h or longer) and placeholders are overlapped.
* Introducing timebarOffsetHorizontal in attributes
* Making epgView protected in the abstract FocusHandler class
* Fix: ScrollToHairline with animation won't scroll to top if we keepVerticalPosition is set true

### v4.1.2 (2020-04-16)

* EpgView extended with getHairlinePosition and isVisibleInProgramWindow methods.

### v4.1.1 (2020-02-24)

* EpgView now uses its defStyleAttr and defStyleRes parameters correctly.

### v4.1 (2019-09-10)

* !! interface change: Datasource now requires EpgView in constructor.
* Added scrollToMillisUtc() and scrollToPosition() to EpgView.
* Added getChannelIndex() method to DataSource.

### v4.0 (2019-09-03)

* Library migrated to AndroidX. From now on, normal releases will target AndroidX, while all releases will be released reverse-jetified as well. For example:
  * tv.accedo.vdk:vdk-core:4.0 < targets AndroidX.
  * tv.accedo.vdk:vdk-core:4.0-appcompat < targets last stable Appcompat release.

### v3.1.2 (2019-07-30)

* LayoutManager.requestChildRectangleOnScreen() added to focushandler, with a default "return false" implementation. This elliminates some eradic behavior in some cases, where long programs scroll onto the screen wrong.


### v3.1.1 (2019-06-19)

* Optimised how placeholders are populated, only throwing away the colliding ones, and not regenerating the whole set.
* Minor SimpleDateFormat optimisation in EpgDataSource.
* Added an XML parameter to override default placeholder trim size.
* Added an XML parameter to enable or disable placeholders on first load.

### v3.1.0 (2019-06-17)

* Made placeholder max width an XML attribute.

### v3.0.9 (2019-05-21)

* Added stableId-s to EpgItems.
* Added epgview_threadsafe XML parameter, where you can decide if the EpgView should force calls to the EpgDataSource callbacks onto the main thread. True by default.

### v3.0.8 (2019-05-14)

* Hotfix for onRequestChannels() quickly called twice on first load.

### v3.0.7 (2019-05-07)

* Added epgView.getAttributes().setServerTime(long timestamp), that allows the EPG to use server-time instead of System.currentTimeMillis().

### v3.0.6 (2018-10-09)

* Fix for Android P, where horizontal flings get reversed on EpgView, when in RTL mode.

### v3.0.5 (2018-07-18)

* Hotfix for EpgAdapter using == instead of .equals() on channels.
* Hotfix for Epg calling update at every round minutes, instead of every minute from start.

### v3.0.4 (2018-06-04)

* Hotfix for placeholders crashing if a program has extremely long runtimes (eg. Long.MAX_VALUE).

### v3.0.3 (2018-05-14)

* !! interface change : Added Channel parameter to onBindPlaceholder().

### v3.0.2 (2018-04-04)

* Hotfix epgView.reload() crashing.

### v3.0.1 (2018-02-22)

* Hotfix reenabling Viewholder creation by View aswell, not only layoutResId.

### v3.0 (2018-01-10)

* Major engine rewrite to improve performance, and open APIs up for new features.
  * Despite the minor interface changes, updating should be quite straightforward though, no major changes have been added to how the component is used.
* Added interchangeable focushandlers with one default implementation in the samples app.
* Added attribute "epgview_view_clipping_enabled" for transparency support. When it's enabled, the EPG won't overdraw, enabling fully transparent layouts.
* Added an optional label for the top-left corner of the EPG where the timebar and the channels meet.
* Added a new type to the datasource, which may be switched on with the "epgview_placeholders_enabled", filling all holes in the EPG, where no programs are available.
* Added looping feature to epg. When "epgview_looping_enabled" is switched on, the content loops vertically.
* !! interface change : ScrollListeners now inherit the default RecyclerView ones.
* !! interface change : Minor datasource changes to improve usability.

### v2.6.1 (2017-02-20)

* Dependency cleanup, dependency on IOUtils removed.

### v2.6 (2017-02-15)

* Added setStartMillisUtc() and setEndMillisUtc() to limit EPG to a certain time window.

### v2.5.9 (2017-02-01)

* Removed buggy dpad support, code cleanup.
* Yet another RTL mirroring fix.

### v2.5.8 (2017-01-29)

* RTL progress and error view mirroring bug fix.
* EPG now sets layoutDirection on its child views.

### v2.5.7 (2017-01-03)

* RTL support added.

### v2.5.6 (2016-11-09)

* Sanity checks added for EpgAttributeHolder params.

### v2.5.5 (2015-04-07)

* OnDayChangeListener now has onCalendarDayChange() callback that is always called at midnight, also when you use hour offsets.

### v2.5.4 (2015-11-04)

* Text now ellipsizes properly, when epgview_sticky_programs_enabled is set to true

### v2.5.3 (2015-10-26)

* Artifactory deploy scripts added
* Dependency on core's R.id.position removed

### v2.5.2 (2015-09-21)

* Added boolean flag to enable or disable diagonal scrolling.

### v2.5.1 (2015-08-27)

* Default focuasble layouts added

### v2.5 (2015-08-17)

* D-Pad support added. This enables usage with Android TVs and set-top boxes
* Programatical setters for XML parameters added

### v2.4 (2015-06-25)

* EPG separated into standalone module

### v2.3.1 (2015-06-11)

* EPG Extra_Padding_Bottom feature added (As requested by the Viasat project)

### v2.3 (2015-06-11)

* No or minor EPG changes

### v2.2.4 (2015-06-04)

* EPG now supports onSaveInstanceState and onRestoreInstanceState making it rotation friendly
* Fixed the bug where the EPG updates only have started after putting the EPG in the background once
* Fixed the bug where the programs past midnight flickered in the EPG

### v2.2.3 (2015-05-21)

* It is now possible to change the timezone that the EPG runs on

### v2.2.2 (2015-05-19)

* Updated the project to use Gradle 2.4
* Made threading for the requests and their callbacks more safe in the EPG
* Added reload functionality to the EPG
* Fixed a bug in the EPG where the day after the last day is fetched if we reach the end
* Removed a Pokémon try-catch from the Epg's LayoutManager, which caused exceptions to be swallowed in the DataSource

### v2.2.1 (2015-04-30)

* Added proper request cancelling to the EPG

### v2.2 (2015-04-27)

* Added horizontal paging to the EPG

### v2.1 (2015-04-27)

* Added the Romtelecom EPG as a generic component.
* EPG is day-based, supports clientside filtering, and vertical paging
