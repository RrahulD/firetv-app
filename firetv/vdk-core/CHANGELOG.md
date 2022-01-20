# Changelog

### v4.1.2 (2021-05-04)

* Avoid preparing logs in RestClient if the logger (L.java) is disabled.

### v4.1.1 (2020-04-16)

* Adding isRangeOverlapping and isRangeInsideOf methods to MathExtender

### v4.1 (2020-02-05)

* !! minor interface change. Imagloader Implementation now has a callback for load success while loading into imageViews directly as well.

### v4.0.3 (2019-12-02)

* Removed "android.permission.WRITE_EXTERNAL_STORAGE" permission, as it is not used since Android 6.
* Added PathUrl empty constructor and path setter.
* Fixing @RecentlyNonNull annotation on PathUrl.toString()
* Added support for query parameters in baseurl in PathUrl. Not necessary a valid usecase, but it's good to handle it gracefully.

### v4.0.2 (2019-10-18)

* Added MathExtender.floatEquals().

### v4.0.1 (2019-10-18)

* Added missing http methods to RestClient.

### v4.0 (2019-09-03)

* Library migrated to AndroidX. From now on, normal releases will target AndroidX, while all releases will be released reverse-jetified as well. For example:
  * tv.accedo.vdk:vdk-core:4.0 < targets AndroidX.
  * tv.accedo.vdk:vdk-core:4.0-appcompat < targets last stable Appcompat release.

### v3.1.0 (2019-06-19)

* RestClient refactored so that you can change the implementation it uses. Has UrlConnection and OkHttp3 by default.
* Added StepLogger from MagentaTV created by Tamás Cseh. It's a very precise stopwatch that can be used to measure performance.

### v3.0.3 (2019-05-13)

* Added PathUrl.parse(url) that can parse back a built string url, into a PathUrl instance.

### v3.0.2 (2018-12-15)

* Replacing ObjectToFile with SerializableToFile, with a better interface. ObjectToFile left behind as a deprecated wrapper for the new class.

### v3.0 (2018-11-05)

* !! behavior change. RestClient has been refactored to only initialise it's urlconnection when "connect()" is called. This makes it capable of:
 * Calling connect() multiple times in a row, on the same RestClient instance.
 * You may do recalls and retries in response checkers now.
 * Extra parameter getters have been added to RestClient.
* !! behavior change. For this to work ".getUrlConnection()" has been removed. You can use ".setConnectionOverride()" to access the underlying UrlConnetion instance.
* !! behavior change. Hash.md5Hash() had a small bug in it, where it left off trailing 0-s if any, from its returned results. Now that has been fixed.

### v2.9.6 (2018-05-11)

* Added SafeAsyncTask.isCancelled().

### v2.9.5 (2018-03-12)

* ImageLoader: Added pause() and resume().
* ImageLoader: Added fade parameter.
* !!Minor interface change!! ImageLoader: made the resize parameter a Boolean instead of a boolean, to add the ability to easily alter default behavior
* CachedCall JavaDoc improvements.

### v2.9.4 (2017-11-13)

* HOTFIX: MathExtender.ceil() used to be broken. Fixed, and verified with 28 unit tests on the class.

### v2.9.3 (2017-11-09)

* Old constructor for Response readded for compatibility reasons.

### v2.9.2 (2017-11-09)
* RestClient responses now contain a field for exceptions caught during connection.
* Added ImageLoader, a Picasso/Glide like builder interface, that you can feed with custom implementations to use whatever ImageLoader you'd like.
* Added new features to MathExtender.
* Fix for tests in Android Studio 3.0

### v2.9.1 (2017-10-02)

* !! behavior change. Nonexistent parameters are now ignored, not replaced with null in PathUrl.
* VdkAnalytics.getVdkComponentVersions() added, now also included in AppDeviceInfoTools.getDeviceInformation().

### v2.9 (2017-07-01)

* VSON from MagentaMusic added. Vson is an expansion of Gson, using PrimitiveAdapterFactory, PathAdapterFactory and OverrideAdapterFactory.

### v2.8.3 (2017-06-20)

* CacheCall refactored to copy less code between sync and async. Expiration added.

### v2.8.2 (2017-05-30)

* Response.getServerTime() added, returning the parsed Date header.

### v2.8.1 (2017-04-19)

* CustomTypefaceSpan bugfix, where it messes up bold/nonbold font swaps.
* AppDeviceInfoTools.isTablet() now uses sw600dp boolean.

### v2.8 (2017-03-20)

* Analytics added.

### v2.7.9 (2017-02-22)

* Added optional AES encryption to ObjectToFile.

### v2.7.8 (2017-02-20)

* Dependency cleanup, dependency on IOUtils removed.

### v2.7.7 (2017-02-15)

* Added VdkApplication.
* Removed deprecated AspectCache and old Challenges.

### v2.7.6 (2017-02-02)

* L extended with missing calls, and calls where you can set priority by integer.

### v2.7.5 (2017-01-29)

* ComponentTools.isRtl() now uses view.getLayoutDirection() when possible. Now it respects AndroidManifest's "supportsRtl" tag.

### v2.7.4 (2017-01-28)

* AspectCache deprecated. Use CachedCall instead.
* Added retentionTime to CommonsApplication.onStop()

### v2.7.3 (2017-01-03)

* ComponentTools.isRtl() added.

### v2.7.2 (2017-01-02)

* DividedStringBuilder now works with CharSequence to allow Spannables.
* Added L.addLogListener(), loglisteners refactored.

### v2.7.1 (2016-12-01)

* Added DividedStringBuilder, that is a stringBuilder for easily creating Strings out of lists, placing dividers in between them.

### v2.7 (2016-11-21)

* Challenges refactored to be a builder. Better naming conventions, better javadoc.

### v2.6.8 (2016-11-10)

* Added getter to RestClient's Response class, that returns the first header by name.

### v2.6.7 (2016-08-01)

* DeviceIdentifier now generates based on ANDROID_ID if available, also always resaves on successful read.

### v2.6.6 (2016-06-23)

* PathUrl now works with params that contain numbers.

### v2.6.5 (2016-06-16)

* CommonsApplication now doesn't call onStop() and onStart() on rotation.
* ApplicationVisiblityListener added, so application visibility events can be added to any Application.
* AppDeviceInfoTools.getDeviceInformation() added.

### v2.6.4 (2016-04-15)

* Bugfix. Fixed nullpointer on Response.getText() on request timeout, introduced in v2.6.3
* Workaround added for occasional okhttp cache corruption. (See: https://github.com/square/okhttp/issues/2281)

### v2.6.3 (2016-03-21)

* RestClient.setEncoding() added, enabling transparent GZIP support for connections.

### v2.6.2 (2016-03-09)

* RestClient uses okhttp if okhttpurlconnection is included as a dependency.
* RestClient setCache() method intreface changed. Caching is now enabled in the constructor. (Fixes urlconnection cache initialization issues)

### v2.6.1 (2015-11-11)

* Challenges, and challengelists added (read more at: https://accedobroadband.jira.com/wiki/display/VDKAND/Core)

### v2.6 (2015-10-26)

* MockClient - external storage support
* Artifactory deploy scripts added
* !! AsyncImageView and DownloadImage removed (deprecated features)
* !! All widgets and animations refactored out into commons-widgets

### v2.5.3 (2015-09-08)

* PathUrl, addEscapedFixedParam() added
* L.setEnabled(boolean) added
* Every library is now distributed with source

### v2.5.2 (2015-08-31)

* RestClient responses now contain headers
* RestClient setting null as payload now consistently removes payload

### v2.5.1 (2015-08-27)

* RestClient verbose-logging fix. Request headers were not logged, instead response headers were logged twice
* Postbody multi-adders added.

### v2.5 (2015-08-17)

* RelativeLayoutForegroundSelector material support added
* LinearLayoutForegroundSelector added
* AspectAwareImageView added
* Material color-sheets added
* CollectionTools.safeUnmodifiableCollection() methods added
* CollectionTools.isValidIndex(index, list) added
* ThreadingTools.tryCancelAll() added
* AnimationDelayer added

### v2.4.4 (2015-07-09)

* commons-gradle added, with reusable gradle script components
* onStart and onStop listener for Applications (useful for analytics), from SkySnap added
* README.md-s are now complete

### v2.4.3 (2015-07-06)

* AnimatedExpandableListView added from SunDance project, with numerous small fixes, such as
    * The view now runs on standard ExpandableListAdapters
    * Animations now run on the normal expandGroup() and collapseGroup() calls
    * Constraints have been added for rapid expansions and collapses, to avoid going into an invalid state
    * Interpolators added, configurable by XML parameter
    * Animation duration configurable by XML parameter
    * Flag for the view to enforce only one open group, configurable by XML parameter
    * Removed bug where the last group was not animated while expanding
    * Fixed bug where the view tags might get overwritten
    * Fixed bug where items that didn't fit on the screen were not showing if scrolled while animating
    * Fixed crash bug when there were more childitems than that would fit on the screen
    * Added animation duration ease for variable child-counts
    * Disabled invalid onItemClick events on children while animating
* PathUrl hashCode now works

### v2.4.2 (2015-07-01)

* Cached method wrapper "CacheCall" and "AsyncCacheCall" added.

### v2.4.1 (2015-06-30)

* CallbackAsyncTask added

### v2.4 (2015-06-25)

* AOP cache added to core, where you can annotate any method with @Cached to be cached (with tests)
* ResizeAnimation from Romtelecom added
* PathUrl now filters for multiple identical inputs, also adds null :params, validity flag added
* StringTools.join for any arbitrary objectlists
* Old Horizontal widgets deprecated

### v2.3.1 (2015-06-11)

* EPG Extra_Padding_Bottom feature added (As requested by the Viasat project)

### v2.3 (2015-06-11)

* New AppGridService, with sample code added. Supports both synchronous and asynchronous calling, offline mode, and if-modified-since.
* RestClient response caching option added
* DeviceIdentifier: DeviceId generator, that persists the generated Id added (Inspired by Péter Sánta's work for CMore and Sky)
* CommonsApplication: Context holding Application base added. (Inspired by Máté Béres' work on Romtelecom)

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

### v2.0 (2015-03-19)

* Major project cleanup
* All Jackson functionality replaced with GSON
* Widevine player removed
* FlowLayout added
* CheckedFrameLayout and CheckedRelativeLayout added
* RelativeLayoutForegroundSelector added
* RestClient can now handle async loading, and different loglevels
* MockClient added
* PostBody builder added

### v1.3.1 (2015-02-03)

* Fixed an issue with AppGrid, to send QUIT instead of STOP when logging application stop.

### v1.3 (2015-01-08)

* Removed unused assets from the library

### v1.2 (2014-12-13)

* AppGridService added
* JacksonTools typed return values
* ExpandableFrameLayout added
* FormattedTextBuilder added
* PathUrl added
* Widevine player added
* Project Gradlified

### v1.1 (2014-03-21)

* AsyncImageView improvements

### v1.0 (2014-02-18)

* Initial commit for this Accedo-public repository
