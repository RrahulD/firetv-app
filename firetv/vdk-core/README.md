# VDK Core

The vdk-core library contains the most basic tools and utilites, that most of the other libraries use aswell. These include:

* A powerful RestClient implementation
* Mock client
* Url builders
* Threading tools
* Caches
* Typography tools
* Scaling animations
* Utility classes for various tasks

## Tech Stack

- AndroidX Appcompat
- Gson
- OkHttp

## Dependencies

```groovy
dependencies {
    implementation 'tv.accedo.vdk:vdk-core:<latest>'
}
```
The following dependencies are optional:

You have to only add GSON if you're planning to use the GSON specific functionalities, such as VSON.

```groovy
dependencies {
    implementation 'com.google.code.gson:gson:<latest>'
}
```

## Contents

### Networking

#### RestClient ####

The RestClient implementation is based on HttpUrlConnection, providing a builder style pattern to make requests. Each and every request is stateless, making requests independent from each other.

RestClient provides rich logging of requests and responses, for easier debugging.

##### Basic sync calls #####

**Sample sync call**

```java
//Basic call
Response response = new RestClient("http://www.google.com").connect();

//Parameterization
Response response = new RestClient("http://www.google.com")
    .setMethod(Method.POST)
    .setPayload("Hello")
    .addHeader("X-Session", "Wat")
    .addCookies(cookieList)
    .setCharset("UTF-8")
    .setTimeout(3000, 6000) //Connect, Read millis
    .setResponseCache(context, true) //Enables HTTP header caching
    .setOnResponseListener(onResponseListener) //Callback to call on response
    .setLogLevel(LogLevel.VERBOSE)
    .connect();
```

If you have special needs, you can always ask for the underlying engine:

**Sample for accessing underlying engine**

```java
RestClient restClient = new RestClient("http://www.google.com");

restClient.getUrlConnection().whateverWeirdStuffYouWant();

Response response = restClient
    .setResponseCache(context, true)
    .connect();
```

##### Basic async calls #####

Once a request is constructed, it can be fired either sync or async.

**Sample async call**

```java
Cancellable cancellable = new RestClient("http://www.google.com")
    .async()
    .connect(new OnResponseListener() {
        @Override
        public void onResponse(Response response) {

        }
    });
```

##### Error handling #####

By default, this RestClient never throws errors, and always returns a non-null Response object. If the request has failed, the response code will inform you of that.

However, if you'd like the RestClient to throw exceptions based on connection events, you can do that with a custom response checker.

**Sample for ResponseCheckers**

```java
ResponseChecker<WhateverException> responseChecker = new ResponseChecker<>() {
    @Override
    public void throwIfNecessary(Response response) throws WhateverException {
        if(!response.isSuccess()){
            throw new WhateverException(response.getText());
         }
     }
};

Response response = new RestClient("http://www.google.com").connect(responseChecker);
```

For error handling in async calls, it is recommended to use SafeAsyncTask or CallbackAsyncTask to wrap your sync calls.

##### Parsing #####

Fetching and parsing data usually goes hand-in-hand, especially when you're doing the requests asynchronously, directly with your restclient library, since parsing is something you'd also want to be doing on the background thread, and not in your onSuccess callback.

**Sample for GSON parsing**

```java
//Sync GSON parsing
List<Asset> assets = new RestClient("http://www.google.com")
    .connect()
    .getGsonParsedText(new TypeToken<List<Asset>>(){});

//Async GSON parsing
Cancellable cancellable = new RestClient("http://www.google.com")
    .async()
    .connectParse(new OnGsonParsedResponse<List<Asset>>() {
        @Override
        public void onResponse(Response response, List<Asset> parsedResponse) {

        }
    });
```

**Sample for custom parsing**

```java
//Defining custom, error handling parser
ThrowingParser<Response, List<Asset>, WhateverException> parser = new ThrowingParser<>() {
    @Override
    public List<Asset> parse(Response input) throws WhateverException {
        try {
            return ...parsedInput...;
        }catch(Exception pokemon){
            throw new WhateverException(pokemon);
        }
    }
};

//sync custom parsing
List<Asset> assets = new RestClient("http://www.google.com")
    .connect()
    .getParsedText(parser);
```

##### Http caching #####

Http caching is disabled by default. You can enable it by providing the RestClient constructor with a cache folder.

**Sample for custom parsing**

```java
new RestClient(url, new File(getActivity().getCacheDir(), "http"))
```

RestClient by default uses the built in HttpUrlConnection classes. HttpUrlConnection is wrapped by OkHttp on Android api levels 5.0 and up. However, if you'd like to use okHttp's advanced caching below 5.0, you can do that by simply including OkHttpUrlConnection into your project. RestClient will automatically use it.

**Sample for custom parsing**

```java
dependencies {
    compile 'com.squareup.okhttp:okhttp-urlconnection:2.7.5'
}
```

##### Encoding support #####

RestClient supports GZIP encoding of traffic transparently. When it is enabled, Response.getText() will automatically decode gzip for you. However, if you're interested in the original gzipped payload, you can also get that by calling Response.getRawResponse().

**Sample for custom parsing**

```java
new RestClient(url).setEncoding(Encoding.GZIP)
```

#### MockClient ####

MockClient can read mocked files from the assets folder, and deliver them as if they were coming from the network, including emulating delays and network failures.

##### Sync usage #####

**Sample for sync usage**

```java
//Sync
new MockClient(context, "mockfile.json")
    .setDelay(2000, 1000) //Respond somewhere between 2000-3000 millisecs
    .setFailFrequency(10) //Fail every 10th request
    .readMockFile();
```

##### Async usage #####

**Sample for async usage**

```java
//Async
new MockClient(context, "mockfile.json")
    .async()
    .readMockFileAsync(new Callback<String>() {
        @Override
        public void execute(String result) {

        }
    });
```

##### Async usage with GSON parsing #####

**Sample for async usage with GSON parsing**

```java
//Async + GSON parsing
new MockClient(context, "mockfile.json")
    .async()
    .readGsonParseMockFileAsync(new TypeToken<List<Asset>>() {}, new Callback<List<Asset>>() {
        @Override
        public void execute(List<Asset> result) {

        }
    });
```

##### Async usage with custom parsing #####

**Sample for async usage with custom parsing**

```java
//Async + custom parsing
new MockClient(context, "mockfile.json")
    .setDelay(2000, 1000)
    .async()
    .readParseMockFileAsync(parser, new Callback<List<Asset>>() { //Parser, same as defined for RestClient
        @Override
        public void execute(List<Asset> result) {

        }
    });
```

#### PathUrl ####

URL builder, supporting flexible url parameters, and query parameter building. Query params may be escaped or non-escaped

**Sample for building an URL with fixed and query params**

```java
String url = new PathUrl("/movie/:ids")
    .setBaseUrl("http://ovp-staging.cloud.accedo.tv")
    .addFixedParam(":id", id)
    .addQueryParam("pageSize", ""+pageSize)
    .toString();
```

PathUrl has the ability to resolve parameters from the members of a class it is handed. In the sample below, pathUrl will look for a member called "id" in the given asset Object, and use that for the :id param.

**Sample for building an URL with object params**

```java
Asset asset = new Asset("the-conjuring");

String url = new PathUrl("/movie/:id")
    .setBaseUrl("http://ovp-staging.cloud.accedo.tv")
    .addObjectParam(asset)
    .toString();
```

PathUrl url encodes its params by default. However, if you want to hand it already encoded params, that is also possible. Those parameters will be passed in directly.

**Sample for building an URL with preencoded parameters**

```java
String url = new PathUrl("/movie/:ids")
    .setBaseUrl("http://ovp-staging.cloud.accedo.tv")
    .addEscapedFixedParam(":id", id)
    .addEscapedQueryParam("pageSize", ""+pageSize)
    .toString();
```

PathUrl can store a flag for each instance, indicating if it's a valid url or no. Useful for constructing printable urls for logging invalid calls.

**Sample for building an invalid URL**

```java
PathUrl pathUrl = new PathUrl("/movie/:ids")
    .setBaseUrl("http://ovp-staging.cloud.accedo.tv")
    .addFixedParam(":id", id)
    .addQueryParam("pageSize", ""+pageSize)
    .setValidity(id != null && pagesize>0);
```

#### PostBody ####

Builder for creating application/x-www-form-urlencoded POST payloads.

**Sample for adding key-value pairs**

```java
String payload = new PostBody()
      .addParams(map)
      .addParam(key, value)
      .toString();
```

**Sample for adding preencoded key-value pairs**

```java
String payload = new PostBody()
      .addEscapedParams(map)
      .addEscapedParam(key, value)
      .toString();
```

### Threading

#### SafeAsyncTask ####

A special, Exception safe AsyncTask. The results of background exceution are returned in the onSuccess callback, while any exceptions thrown are returned in the onFailure callback.

#### CallbackAsyncTask ####

A derivate of SafeAsyncTask, that can take an onSuccess and an onFailure callback in it's constructor, automatically calling into them when execution is done. Can be used to reduce boilerplate code, and easily wrap sync service calls.

**Sample for wrapping a sync call with CallbackAsyncTask**

```java
@Override
Cancellable getProfileAsync(Context context, Callback<Profile> onSuccess, Callback<OvpException> onFailure) {
    return new CallbackAsyncTask<Profile, OvpException>(onSuccess, onFailure) {
        @Override
        public Profile call(Void... params) throws Exception {
            return accountService.getProfile(context); //Your sync call
        }
    }.executeAndReturn();
}
```

#### Cancellable ####

Generic interface defined for cancellable operations, such as AsyncTasks, SafeAsyncTasks, CancellableAsyncTasks.

#### ThreadingTools ####

Utility methods for safe-cancelling threads.

### Caching

#### CachedCall ####

Can be used to wrap calls, in order to make them cached. The execute command of this call returns and throws the exact same things, as what your call would have.

**Sample for wrapping a sync call with CachedCall**

```java
List<Asset> assets = new CachedCall<List<Asset>, OvpException>(key){
    @Override
    protected List<Asset> call() throws OvpException {
        return assetService.getAllMovies();
    }
}.execute();
```

Can be used for async calls aswell. However, if you have a sync call aswell, its preferred to cache that, and just calling that in the async call.

**Sample for wrapping an async call with AsyncCachedCall**

```java
Cancellable cancellable = new AsyncCachedCall<List<Asset>, OvpException>("allMovies", onSuccess, onFailure){
    @Override
    protected Cancellable call(Callback<List<Asset>> onSuccess, Callback<OvpException> onFailure) {
        return assetService.async().getAllMovies(onSuccess, onFailure);
    }
}.execute();
```

The size of the underlying LRU cache can be changed statically, that you should call either in your service's constructor, or your application's onCreate.

**Sample for changing the size of the underlying LRU cache of CachedCall**

```java
CachedCall.setCacheSize(200);
```

#### ObjectToFile ####

ObjectToFile is an utility class, that can be used to persistently store serializable objects in the device's internal private storage.

**Sample for using ObjectToFile**

```java
//Writing
ObjectToFile.write(context, objectToSave, "my_filename.file");

//Reading
Object readObject = ObjectToFile.read(context, "my_filename.file");

//Deleting
ObjectToFile.delete(context, "my_filename.file");

boolean exists = ObjectToFile.exists(context, "my_filename.file");
```

### Logging

#### L ####

Wrapper created around android.util.Log, to have some generic control over when and how things get logged. It is possible to enable-disable logs for specific builds, such as release builds, and it is also possible to set log listeners.

### Types

#### BiMap ####

Bi-directional HashMap, where you can access both value by key, or key by value. Both directions are hashed.

#### IndexedHashMap ####

Provides a LinkedHashMap implementation, where the values are both accessible by either key, or index. Both index mapping is direct, key mapping is hashed.

#### Triplet ####

Container to ease passing around a triplet of three objects. This object provides a sensible implementation of equals(), returning true if equals() is true on each of the contained objects.

Basically a three-variable-version of android.util.Pair.

### Cipher

#### AES256 helper ####

Provides generic utility methods for AES256 encoding content.

**List of provided methods**

```java
byte[] generateIV()

SecretKey generateSecretKey(String password, String salt)
SecretKey generateRandomSecretKey()

CipherOutputStream encrypt(SecretKey secretKey, byte[] iv, OutputStream os)
CipherOutputStream encrypt(String password, String salt, byte[] iv, OutputStream os)

CipherInputStream decrypt(SecretKey secretKey, byte[] iv, InputStream is)
CipherInputStream decrypt(String password, String salt, byte[] iv, InputStream is)

byte[] encrypt(SecretKey secretKey, byte[] iv, byte[] input)
byte[] decrypt(SecretKey secretKey, byte[] iv, byte[] input)

byte[] encrypt(String password, String salt, byte[] iv, byte[] input)
byte[] decrypt(String password, String salt, byte[] iv, byte[] input)
```

### Application tracking

The class VdkApplication is a descendant of "android.app.Application". Added features are:

* Provides a statically always accessible application context
* Provides onStart() and onStop() methods, that get triggered when the first activity is started, and all of the activities are stopped. This translates to, onStart() gets called when the application becomes visible, and onStop() gets called, when the application is closed or sent to the background. Useful for analytics and screentime tracking.

### Challenges

Provides an interface that wraps an async precondition for an action. For example "if the user is not logged in, show the login dialog, and log them in".
These challenges can then be linked together, to form a chain of challenges before an action.

**Examples**

* User needs to be logged in, if not, the login dialog should show before playback

* User needs to enter a parental pin, and playback should only start on a proper pin

This can easily be done by encapsulating the two dialogs as async challenges, and placing them infront of the playback call like:

**Example for login and parentalPin challenges**

```java
new ChallengeBuilder(context)
    .addChallenge(new LoginChallenge())
    .addChallenge(new PatentalChallenge())
    .setOnPassedListener(new OnPassedListener() {
        @Override
        public void challengesPassed() {
            // Start player...
        }
    })
    .run();
```

An optional failure path may be added, where the calling thread can be informed about the error that broke the challenge chain.

**Example for challengeList with failure path**

```java
new ChallengeBuilder(context)
    .addChallenge(new LoginChallenge())
    .setOnPassedListener(new OnPassedListener() {
        @Override
        public void challengesPassed() {
            // Start player.
        }
    })
    .setOnFailedListener(new OnFailedListener() {
        @Override
        public void challengeFailed(Challenge challenge, Object reason) {
            // Do something else instead.
        }
    })
    .run();
```

### VSON

Vson is an expansion of Gson, using PrimitiveAdapterFactory, PathAdapterFactory and OverrideAdapterFactory.

**VSON usage**

```java
//Normal usage
new Vson().toGson().fromJson(...)

//Optional parameters
new Vson(gsonBuilder) //You can add a gsonBuilder, if you want to customise gson parameters available there.
        .addOverride(..) //Add JSON modification rules for a type, for OverrideAdapterFactory
        .addHierarchyOverride(..)  //Add JSON modification rules for a type and its subtypes, for OverrideAdapterFactory
        .addPrimitiveParser(..) //Override primitive parsing rules for PrimitiveAdapterFactory
        .setPrimitiveAdapterFactoryEnabled(..) //Enables/disables primitive preprocessing
        .setPathAdapterFactoryEnabled(..) //Enables/disables path parsing
        .registerTypeAdapter() //Convenience setter calling gsonBuilder.registerTypeAdapter()
        .registerTypeAdapterFactory() //Convenience setter calling gsonBuilder.registerTypeAdapterFactory()
        .toGson()
```

#### PathAdapterFactory ####

Provides path mapping functionality to GSON. You can annotate fields like:

```java
@Path("asset/image/href")
private String imageUrl;

@Path("asset/images[0]/href")
private String imageUrl; //Contains the first image's url from the images array.

@Path("asset/images[type=teaser]/href")
private String teaserImageUrl; //Contains the first image's url from the images array, that has the type field set to "teaser".
```

#### OverrideAdapterFactory ####

A TypeAdapterFactory which allows you to modify the raw input-json and its type, based on its contents, before it's actually fed into GSON for auto-mapping. For example:

**Input**

```java
{
    "theKeysAreValues": {
        "Jack": {
            "gender": "male",
            "birthday": "tuesday"
        },
        "Jill": {
            "gender": "female",
            "birthday": "winter"
        }
    }
}
```

Lets turn that into a list of object like:

**Desired input**

```java
{
    "theKeysAreValues": [
          {
            "name": "Jack",
            "gender": "male",
            "birthday": "tuesday"
        },
        {
            "name": "Jill",
            "gender": "female",
            "birthday": "winter"
        }
    ]
}
```

And the override that does exactly that for us:

**Override**

```java
GsonOverride gsonOverride = new GsonOverride() {
    @Override
    public JsonElement jsonOverride(JsonElement raw) {
        JsonArray output = new JsonArray();

        for (Entry<String, JsonElement> entry : raw.getAsJsonObject().entrySet()) {
            JsonObject value = entry.getValue().getAsJsonObject();
            value.addProperty("name", entry.getKey());
            output.add(value);
        }

        return output;
    }
    ...
}

class MyPojo {
    List<Person> theKeysAreValues;
}

new Vson().addHierarchyOverride(new TypeToken<List<Person>>() {}, gsonOverride);
```

#### PrimitiveAdapterFactory ####

Provides more flexible mapping for primitives.

* String values containing parseable primitives "15" as a string instead of 15 in a json.
* "null" or null passed down in primitive json fields. Skips the field in this case, leaving in its default value provided in the class.
* Supported types: byte, double, float, int, long, short, boolean; both boxed and unboxed.
* May be extended with more PrimitiveParser-s.

### DeviceIdentifier generator

Provides an utility method for generating a mostly persistent device ID.

Tries to retrieve any stored device identifier, or generates or stores a new one if necessary.

* Stores deviceId in internal storage - capable of surviving external storage formatting
* And also stores on the external storage - capable of surviving app uninstall and reinstall

### Tools/Utilities

#### AppDeviceInfoTools ####

Provides utility methods to extract information about the application and the device it is ran on. Current utilities are:

* isTablet()
* getApplicationVersionName()
* getApplicationVersionCode()
* getMACAddress()
* getMACAddress(interfaceName)

#### CollectionTools ####

Provides an extension over "java.util.Collections".

* Finding an item in a list with equals()
* Finding an item in a list with a custom comparator
* Index checking
* Null-safe UnModifiableCollection methods

#### ColorTools ####

Provides color and bitmap manipulation utility methods such as:

* invert colors
* adjust colors
* multiply colors
* blend colors

#### ComponentTools ####

A collection of utility methods for measuring and manipulating Views and ViewGroups

#### DateTools ####

Provides utility methods for converting timestamps into UTC or local time

#### Hash ####

Provides utility methods for the following hashes: md5, sha1, hmacSHA1

#### MathExtender ####

Provides a mathematically correct implementation of modulus.

#### RootChecker ####

Can be used to check if the device is rooted or no. Since there are no sure-to-work methods available for this, this method tries several common approaches.

#### StringParser ####

Provides null safe parser methods for converting strings to primitives.

#### StringTools ####

Contains string manipulation utility methods such as: Joining a list of strings with a given separator.

### Typography

#### FormattedTextBuilder ####

Used to easily build spannable texts, to include different formatting in one TextView.

**Sample usage for FormattedTextBuilder**

```java
FormattedTextBuilder ftb = new FormattedTextBuilder()
    .setHtml(false)
    .setSeparator(" ")
    .addStrings("Hello", "world")
    .addRelativeSizes(1f, 2f)
    .addTypefaces(Typeface.DEFAULT, Typeface.DEFAULT_BOLD)
    .addColors(0xff00ff00, 0xffff0000);

textView.setText(ftb);
```

#### DividedStringBuilder ####

Used to concatenate lists of items with dividers in a smart way.

It handles null items in a list. It can be used to extract fields from model objects.

**Simple example**

```java
CharSequence result = new DividedStringBuilder()
    .append("Hello")
    .divider(" ")
    .append("world!")
    .append(null)
    .divider(", ")
    .append(null)
    .build();

// Results in "Hello world!"
```

**Model object example**

```java
List<Category> categories;

CharSequence result = new DividedStringBuilder()
    .append(getString(R.string.categories))
    .divider(": ")
    .appendObjects(list, ", ", new Formatter<Category>() {
        @Override
        public String format(Category item) {
            return item.getTitle();
        }
    })
    .build();

// Results in "Categories: action, romance, horror
// Even if some categories are null, or some categories have a null title.
```

## QA Reference

The Viki application uses this library for all networking (fetching any sort of data from the backend), and most of the util classes.
