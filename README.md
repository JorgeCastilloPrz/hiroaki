Hiroaki [![CircleCI](https://circleci.com/gh/JorgeCastilloPrz/hiroaki/tree/master.svg?style=svg&circle-token=3824cb7754fef5b81f1a67c6e86786df5db242c5)](https://circleci.com/gh/JorgeCastilloPrz/hiroaki/tree/master) [![MavenCentral/hiroaki-core](https://maven-badges.herokuapp.com/maven-central/me.jorgecastillo/hiroaki-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/me.jorgecastillo/hiroaki-core) [![MavenCentral/hiroaki-android](https://maven-badges.herokuapp.com/maven-central/me.jorgecastillo/hiroaki-android/badge.svg)](https://maven-badges.herokuapp.com/maven-central/me.jorgecastillo/hiroaki-android)
======

<img src="./art/sakura_logo.svg" width="256" height="256" />

    Japanese: 'spreading brightness'. Derived from the words 'hiro', which means 'large or wide', and 'aki', 
    which means 'bright or clear'.

The intention of Hiroaki is to achieve clarity on your **API integration tests** in an idiomatic way by leveraging the 
power of Kotlin.

It uses `MockWebServer` to provide a mock server as a target for your HTTP requests that you'll use to mock your backend.

That enables you to assert over how your program reacts to some predefined server & API behaviors. 

Usage
-----

Add the following code to your ``build.gradle``. Both dependencies are available in **Maven Central**.

```groovy
dependencies{
    implementation 'me.jorgecastillo:hiroaki-core:0.0.3'
    implementation 'me.jorgecastillo:hiroaki-android:0.0.3' // Android instrumentation tests
}
```

### Setup

To avoid your code to query the real endpoints you must configure your retrofit instance to use the mock server url. 

Also note that you must extend `MockServerSuite`, which takes care of running and shutting down the server for you. 
If you can't do that, there's also a JUnit `Rule` with the same goal.

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

Also here you have the **JUnit4 rule** with the same purpose:

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
                  .thenRespond(success(jsonFileName = "GetNews.json"))
    
       runBlocking { dataSource.getNews() }

       /*...*/
    }
}
```

**But I don\'t use Retrofit!**

You can still use **Hiroaki** without `Retrofit`. Just request the URL from your mock server instance and use it as the 
endpoint for your requests. Here you have a plain [OkHttp](http://square.github.io/okhttp/) sample.
````kotlin
val mockServerUrl = server.url("/v2/news")
dataSource = NewsDataSource(mockServerUrl)

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

### Mocking Responses

With **Hiroaki**, you can mock request responses as if it was mockito:
````kotlin
@Test
fun chainResponses() {
    server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(success(jsonFileName = "GetNews.json"))

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
                queryParams = mapOf("sources" to "crypto-coins-news",
                        "apiKey" to "a7c816f57c004c49a21bd458e11e2807"),
                jsonBodyResFile = fileBody("GetNews.json"),
                jsonBody = inlineBody("{\n" +
                        "  \"title\": \"Any Title\",\n" +
                        "  \"description\": \"Any description\",\n" +
                        "  \"source\": {\n" +
                        "    \"link\": \"http://source/123\",\n" +
                        "    \"name\": \"Some source\"\n" +
                        "  }\n" +
                        "}\n"),
                headers = headers("Cache-Control" to "max-age=640000"))
      .thenRespond(success(jsonFileName = "GetNews.json"))
```
If you try to match to both `jsonBodyResFile` and `jsonBody` at the same time you'll get an exception.

Also note in the previous snippets the `success()` function when mocking the response. function `success()` is a 
shortcut to provide a mocked successful response. You can also use `error()`  and `response()`. All of them are mocking 
functions that allow you to pass the following **optional** arguments:

* `code` **Int** return http status code for the mocked response.
* `jsonFileName` **String** resource file name to load the mocked response json body from.
* `jsonBody` **String** inlined json for your mocked response body.
* `headers` Is a **Map<String,String>** headers to attach to the mocked response.

If you don't want to use the `succes()`, `error()` or `response()` shortcut functions, you can still pass your own 
custom `MockResponse`. 

**Chaining mocked responses:**

You can also chain a bunch of mocked responses:
````kotlin
server.whenever(Method.GET, "v2/top-headlines")
                .thenRespond(success(jsonFileName = "GetNews.json"))
                .thenRespond(success(jsonFileName = "GetSingleNew.json"))
                .thenRespond(success(jsonFileName = "GetNews.json"))
````
Each time the endpoint is called under the given conditions, the server will return the next mocked response from the 
list, **following the same order**.

**Dynamic dispatch**

Sometimes you want a response to depend on the request sent. For that reason, **Hiroaki** provides the `thenDispatch` 
method:
````kotlin
server.whenever(Method.GET, "v2/top-headlines")
      .thenDispatch { request -> success(jsonBody = "{\"requestPath\" : ${request.path}" }
````   
You can combine as many `thenRespond()` and `thenDispatch()` calls as you want.
````kotlin
server.whenever(Method.GET, "v2/top-headlines")
      .thenRespond(success())
      .thenDispatch { request -> success(jsonBody = "{\"requestPath\" : ${request.path}" }
      .thenRespond(error())
````   

**Delay responses**

Mimic server response delays with `delay()`, an extension function for `MockResponse` to pass a delay in 
millis: `response.delay(millis)`:

````kotlin
server.whenever(Method.GET, "v2/top-headlines")
      .thenRespond(success(jsonFileName = "GetNews.json").delay(250))
      .thenRespond(success(jsonFileName = "GetSingleNew.json").delay(500))
      .thenRespond(success(jsonFileName = "GetNews.json").delay(1000))
      
// Also for dispatched responses
server.whenever(Method.GET, "v2/top-headlines")
      .thenDispatch { request -> success().delay(250) }
````

**Throttle response bodies**

Sometimes you want to emulate bad network conditions, so you can throttle your response body like:
````kotlin
server.whenever(GET, "v2/top-headlines").thenRespond(error().throttle(64, 1000))
````
Here, you are asking the server to throttle and write chunks of 64 bytes per second (1000 millis).

### Request verification

**Hiroaki** provides a highly configurable `verify()` function to perform verification over executed HTTP requests. 
Its arguments are **optional** so you're free to configure the assertion in a way that matches your needs. 

```kotlin
@Test
fun verifiesCall() {
    server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(success(jsonFileName = "GetNews.json"))
            .thenRespond(success(jsonFileName = "GetSingleNew.json"))
            .thenRespond(success(jsonFileName = "GetNews.json"))

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
                                "apiKey" to "a7c816f57c004c49a21bd458e11e2807"),
            jsonBody = inlineBody("{\n" +
                                  "  \"title\": \"Any Title\",\n" +
                                  "  \"description\": \"Any description\",\n" +
                                  "  \"source\": {\n" +
                                  "    \"link\": \"http://source/123\",\n" +
                                  "    \"name\": \"Some source\"\n" +
                                  "  }\n" +
                                  "}\n"))
            // Ofc you can also pass a fileBody("GetNews.json")
}
```
You can use the functions `never()`, `once()`, `twice()`, `times(num)`, `atLeast`, and `atMost` for the times param.

### Parsed Response assertions
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

### Android Instrumentation Tests

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
                .thenRespond(success(jsonFileName = "GetNews.json"))

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

**Verifying calls in Android**

Using call verification on Android instrumentation tests can also be a good idea, so you are able to assert that the 
endpoints are called as expected (including optional times / ordering) per screen. 

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

