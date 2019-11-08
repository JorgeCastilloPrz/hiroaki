Hiroaki [![CircleCI](https://circleci.com/gh/JorgeCastilloPrz/hiroaki/tree/master.svg?style=svg&circle-token=3824cb7754fef5b81f1a67c6e86786df5db242c5)](https://circleci.com/gh/JorgeCastilloPrz/hiroaki/tree/master)
======

<img src="./art/sakura_logo.svg" width="256" height="256" />

    Japanese: 'spreading brightness'. Derived from the words 'hiro', which means 'large or wide', and 'aki',
    which means 'bright or clear'.

The intention of Hiroaki is to achieve clarity on your **API integration tests** in an idiomatic way by leveraging the
power of Kotlin.

It uses `MockWebServer` to provide a mock server as a target for your HTTP requests that you'll use to mock your backend.

That enables you to assert over how your program reacts to some predefined server & API behaviors.

Dependency
----------

For Android, add the following dependencies to your `build.gradle`. Both dependencies are available in **Maven Central**.

```groovy
dependencies{
    testImplementation 'me.jorgecastillo:hiroaki-core:0.2.3'
    androidTestImplementation 'me.jorgecastillo:hiroaki-android:0.2.3' // Android instrumentation tests
}
```

Note that Hiroaki **only targets AndroidX**. It does not provide support for Android support libraries anymore.

If you do plain Java or Kotlin you'll just need the core artifact on its `0.2.3` version.

Setup
-----

To work with **Hiroaki** you must extend `MockServerSuite` on your test class, which takes care of running and shutting
down the server for you. If you can't do that, there's also a JUnit4 `Rule` called `MockServerRule` with the same goal.

To target the mock server with your requests, you'll need to request the URL from it and pass it to your endpoint
creation system / collaborator / entity.

Here you have a plain [OkHttp](http://square.github.io/okhttp/) sample.
````kotlin
class GsonNewsNetworkDataSourceTest : MockServerSuite() {

 private lateinit var dataSource: GsonNewsNetworkDataSource

    @Before
    override fun setup() {
        super.setup()
        val mockServerUrl = server.url("/v2/news")
        dataSource = NewsDataSource(mockServerUrl)
    }

    /*...add tests here!...*/
}

/*Some random data source, probably on a different file*/
class NewsDataSource(var baseUrl: HttpUrl) {

  fun getNews(): String? {
      val client = OkHttpClient()
      val request = Request.Builder()
              .url(baseUrl)
              .build()

      val response = client.newCall(request).execute()
      return response.body()?.string()
  }
}
````
If you have an **endpoint factory**, or even a **DI system** providing injected endpoints, you'll need to have a good
design on your app to pass the mock server url to it. That's on you and is different for every project.

Syntax for Retrofit
-------------------
However, **Hiroaki** provides syntax for waking up mock `Retrofit` services in case you need one for writing some unit
tests for your api client / data source as the subject under test.

```kotlin
class GsonNewsNetworkDataSourceTest : MockServerSuite() {

    private lateinit var dataSource: GsonNewsNetworkDataSource

    @Before
    override fun setup() {
        super.setup()
        // Use server.retrofitService() to build the service targeting the mock URL
        dataSource = GsonNewsNetworkDataSource(server.retrofitService(
                GsonNewsApiService::class.java,
                GsonConverterFactory.create()))
    }

    /*...*/
}
```
This will use a default `OkHttpClient` instance created for you with basic configuration. For more detailed
configuration, `retrofitService()` function offers an optional parameter to pass a custom `OkHttpClient`:

```kotlin
val customClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.SECONDS)
        .build()

dataSource = GsonNewsNetworkDataSource(server.retrofitService(
                GsonNewsApiService::class.java,
                GsonConverterFactory.create(),
                okHttpClient = customClient))
```

JUnit4 Rule
-----------
As mentioned before, here you have the alternative **JUnit4 rule** to avoid using extension if that's your need:

```kotlin
@RunWith(MockitoJUnitRunner::class)
class RuleNetworkDataSourceTest {

    private lateinit var dataSource: JacksonNewsNetworkDataSource
    @get:Rule val rule: MockServerRule = MockServerRule()

    @Before
    fun setup() {
        dataSource = JacksonNewsNetworkDataSource(rule.server.retrofitService(
                JacksonNewsApiService::class.java,
                JacksonConverterFactory.create()))
    }

    @Test
    fun sendsGetNews() {
       // you'll need to call the server through the rule
       rule.server.whenever(GET, "v2/top-headlines")
                  .thenRespond(success(jsonBody = fileBody("GetNews.json")))
                  // Can also inline a body or use the json DSL

       runBlocking { dataSource.getNews() }

       /*...*/
    }
}
```

Mocking Responses
-----------------

With **Hiroaki**, you can mock request responses as if it was mockito:
````kotlin
@Test
fun chainResponses() {
    server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))
            // Can also inline a body or use the json DSL

    val news = runBlocking { dataSource.getNews() }

    /*...*/
}
````
This ensures that **whenever** the endpoint `v2/top-headlines` is called with the given conditions the server will
respond with the mocked response we're providing.

These are all the supported params for `whenever` that you can match to. All of them are optional except `sentToPath`:
```kotlin
server.whenever(method = Method.GET,
                sentToPath = "v2/top-headlines",
                queryParams = params("sources" to "crypto-coins-news",
                        "apiKey" to "21a12ef352b649caa97499bed2e77350"),
                jsonBody = fileBody("GetNews.json"), // (file, inline, or JsonDSL)
                headers = headers("Cache-Control" to "max-age=640000"))
      .thenRespond(success(jsonFileName = "GetNews.json"))
```

Also note in the previous snippets the `success()` function when mocking the response. function `success()` is a
shortcut to provide a mocked successful response. You can also use `error()`  and `response()`. All of them are mocking
functions that allow you to pass the following **optional** arguments:

* `code` **Int** return http status code for the mocked response.
* `jsonBody` **JsonBody**, **JsonFileBody**, **Json** or **JsonArray**: json for your mocked response body.
* `headers` Is a **Map<String,String>** headers to attach to the mocked response.

If you don't want to use the `succes()`, `error()` or `response()` shortcut functions, you can still pass your own
custom `MockResponse`.

Chaining Mocked Responses
-------------------------

You can also chain a bunch of mocked responses:
````kotlin
server.whenever(Method.GET, "v2/top-headlines")
                .thenRespond(success(jsonBody = fileBody("GetNews.json")))
                .thenRespond(success(jsonBody = fileBody("GetSingleNew.json")))
                .thenRespond(success(jsonBody = fileBody("GetNews.json")))
````
Each time the endpoint is called under the given conditions, the server will return the next mocked response from the
list, **following the same order**.

Dynamic dispatch
----------------

Sometimes you want a response to depend on the request sent. For that reason, **Hiroaki** provides the `thenDispatch`
method:
````kotlin
server.whenever(Method.GET, "v2/top-headlines")
      .thenDispatch { request -> success(jsonBody = inlineBody("{\"requestPath\" : ${request.path}}")) }
````   
You can combine as many `thenRespond()` and `thenDispatch()` calls as you want.
````kotlin
server.whenever(Method.GET, "v2/top-headlines")
      .thenRespond(success())
      .thenDispatch { request -> success(jsonBody = inlineBody("{\"requestPath\" : ${request.path}}")) }
      .thenRespond(error())
````   

Delay Responses
---------------

Mimic server response delays with `delay()`, an extension function for `MockResponse` to pass a delay in
millis: `response.delay(millis)`:

````kotlin
server.whenever(Method.GET, "v2/top-headlines")
      .thenRespond(success(jsonBody = fileBody("GetNews.json")).delay(250))
      .thenRespond(success(jsonBody = fileBody("GetSingleNew.json")).delay(500))
      .thenRespond(success(jsonBody = fileBody("GetNews.json")).delay(1000))

// Also for dispatched responses
server.whenever(Method.GET, "v2/top-headlines")
      .thenDispatch { request -> success().delay(250) }
````

Throttle response bodies
------------------------

Sometimes you want to emulate bad network conditions, so you can throttle your response body like:
````kotlin
server.whenever(GET, "v2/top-headlines").thenRespond(error().throttle(64, 1000))
````
Here, you are asking the server to throttle and write chunks of 64 bytes per second (1000 millis).

Verifying Requests
------------------

**Hiroaki** provides a highly configurable `verify()` function to perform verification over executed HTTP requests.
Its arguments are **optional** so you're free to configure the assertion in a way that matches your needs.

```kotlin
@Test
fun verifiesCall() {
    server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))
            .thenRespond(success(jsonBody = fileBody("GetSingleNew.json")))
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

    runBlocking {
        dataSource.getNews()
        dataSource.getSingleNew()
        dataSource.getNews()
    }

    server.verify("v2/top-headlines").called(
            times = times(2),
            order = order(1, 3),
            method = Method.POST,
            headers = headers("Cache-Control" to "max-age=640000"),
            queryParams = params(
                                "sources" to "crypto-coins-news",
                                "apiKey" to "21a12ef352b649caa97499bed2e77350"),
            jsonBody = inlineBody("{\n" +
                                  "  \"title\": \"Any Title\",\n" +
                                  "  \"description\": \"Any description\",\n" +
                                  "  \"source\": {\n" +
                                  "    \"link\": \"http://source/123\",\n" +
                                  "    \"name\": \"Some source\"\n" +
                                  "  }\n" +
                                  "}\n"))
}
```
You can use the functions `never()`, `once()`, `twice()`, `times(num)`, `atLeast`, and `atMost` for the times param.

Parsed response assertions
--------------------------

After any test that requests data from network you'll probably need to **assert over the parsed response** to double
check whether the data was received and parsed properly.

```kotlin
@Test
fun parsesNewsProperly() {
    server.enqueueSuccessResponse("GetNews.json")

    val news = runBlocking { dataSource.getNews() }

    news eq expectedNews() // eq is an infix function for assertEquals()
}
```
`eq` is just an `infix` function to run `assertEquals` on both objects. Here we are building the list of expected
objects with the function `expectedNews()`. The objects are being compared using the `equals` operator so you
**better use data classes for DTOs or redefine `equals`** properly.

Android Instrumentation tests
-----------------------------

Extend `AndroidMockServerSuite` or use `AndroidMockServerRule` instead.

Basic sample of Android instrumentation tests:
````kotlin
@LargeTest
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest : AndroidMockServerSuite() {

    @get:Rule val testRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    override fun setup() {
        super.setup()
        val mockService = server.retrofitService(
                MoshiNewsApiService::class.java,
                MoshiConverterFactory.create())
        getApp().service = mockService
    }

    private fun startActivity(): MainActivity {
        return testRule.launchActivity(Intent())
    }

    @Test
    fun showsEmptyCaseIfThereAreNoSuperHeroes() {
        server.whenever(GET, "v2/top-headlines")
                .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        startActivity()

        onView(withText(expectedNews()[0].title)).check(matches(isDisplayed()))
        onView(withText(expectedNews()[0].description)).check(matches(isDisplayed()))
    }
}
````
I'm being intentionally simple here on how I pass the mocked service to the application class (setup method), which is
being replaced by a mock application on the androidTest environment. But you would use a dependency injector/container
to replace the service most likely.

**Important: Json Body files location:**
For Android instrumentation tests you'll need to put your json body files into `androidTest/assets/` folder. That's due
to how android loads resources.

Call verification on Android
----------------------------

Using call verification on Android instrumentation tests can also be a good idea, so you are able to assert that the
endpoints are called as expected (including optional times / ordering) per screen.

Json Body DSL
-------------

Anywhere where **Hiroaki** requests a `JsonBody` from you (matchers, assertions, wherever), you can use 3 options:
* `fileBody("Filename.json")`: To pass a json from a file resource (`/test/resources` or `androidTest/assets`)
* `inlineBody("{...}")`: To pass an inlined body.
* `JsonDSL`: A fancy DSL to create your inlined json bodies in a very idiomatic way. Some examples:

```kotlin
json {
    "status" / "ok"
    "totalResults" / 2342
    "articles" / jsonArray(json {
        "source" / json {
            "id" / request.path.length
            "name" / "Lifehacker.com"
        }
        "author" / "Jacob Kleinman"
        "title" / "How to Get Android P's Screenshot Editing Tool on Any Android Phone"
        "description" / "Last year, Apple brought advanced screenshot editing tools to the iPhone with iOS 11, and, this week, Google fired back with a similar Android feature called Markup. The only catch is that this new tool is limited to Android P, which launches later this year …"
        "url" / "https://lifehacker.com/how-to-get-android-ps-screenshot-editing-tool-on-any-an-1823646122"
        "urlToImage" / "https://i.kinja-img.com/gawker-media/image/upload/s--Y-5X_NcT--/c_fill,fl_progressive,g_center,h_450,q_80,w_800/nxmwbkwzoc1z1tmak7s4.jpg"
        "publishedAt" / "2018-03-09T20:30:00Z"
    })
}

jsonArray("Something", "More stuff", "Something more"))

jsonArray(
    json {
      "status" / "ok"
      "title" / "How to Get Android P's Screenshot Editing Tool on Any Android Phone"
      "ids" / jsonArray(1, 2, 3)
    },
    json {
      "status" / "ok"
      "title" / "How to Get Android P's Screenshot Editing Tool on Any Android Phone"
      "ids" / jsonArray(1, 2, 3)
    },
    json {
      "status" / "ok"
      "title" / "How to Get Android P's Screenshot Editing Tool on Any Android Phone"
      "ids" / jsonArray(1, 2, 3)
    })
```
You can combine `jsonArray{}` and `json{}` blocks arbitrarily. **Hiroaki** will create a properly formatted json for you.
Also feel free to use `jsonArray` as the root node for your json if you need to.
````kotlin
server.whenever(Method.GET, "my-fake-service/1")
                .thenRespond(success(jsonBody = jsonArray(1, 2, 3)))

server.whenever(Method.GET, "my-fake-service/1")
                .thenRespond(success(jsonBody =
                    json {
                       "status" / "ok"
                       "totalResults" / 2342
                       "articles" / jsonArray(json {
                           "source" / json {
                               "id" / request.path.length
                               "name" / "Lifehacker.com"
                           }
                           "author" / "Jacob Kleinman"
                           "title" / "How to Get Android P's Screenshot Editing Tool on Any Android Phone"
                           "description" / "Last year, Apple brought advanced screenshot editing tools to the iPhone with iOS 11, and, this week, Google fired back with a similar Android feature called Markup. The only catch is that this new tool is limited to Android P, which launches later this year …"
                           "url" / "https://lifehacker.com/how-to-get-android-ps-screenshot-editing-tool-on-any-an-1823646122"
                           "urlToImage" / "https://i.kinja-img.com/gawker-media/image/upload/s--Y-5X_NcT--/c_fill,fl_progressive,g_center,h_450,q_80,w_800/nxmwbkwzoc1z1tmak7s4.jpg"
                           "publishedAt" / "2018-03-09T20:30:00Z"
                       })
                   }))
````

Do you want to contribute?
--------------------------

I would love to get contributions from anybody. So if you feel that the library is lacking any features
you consider key, please open an issue asking for it or a pull request providing an implementation for it.

The library is using **CircleCI 2.0** to enforce passing tests and code style quality.

Any PR's must pass CI and that includes code style. Run the following commands to check code style or
automatically format it. (You can use the graddle wrapper (`gradlew`) instead)
```groovy
// check code style
gradle app:ktlint
gradle hiroaki-core:ktlint
gradle hiroaki-android:ktlint

// autoformat
gradle app:ktlintFormat
gradle hiroaki-core:ktlintFormat
gradle hiroaki-android:ktlintFormat
```

Tests are also required to pass. You can run them like:
```groovy
gradle test
```

License
-------

    Copyright 2018 Jorge Castillo Pérez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
