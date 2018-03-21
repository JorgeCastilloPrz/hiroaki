Hiroaki [![CircleCI](https://circleci.com/gh/JorgeCastilloPrz/hiroaki/tree/master.svg?style=svg&circle-token=3824cb7754fef5b81f1a67c6e86786df5db242c5)](https://circleci.com/gh/JorgeCastilloPrz/hiroaki/tree/master) [![MavenCentral/hiroaki-core](https://maven-badges.herokuapp.com/maven-central/me.jorgecastillo/hiroaki-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/me.jorgecastillo/hiroaki-core) [![MavenCentral/hiroaki-android](https://maven-badges.herokuapp.com/maven-central/me.jorgecastillo/hiroaki-android/badge.svg)](https://maven-badges.herokuapp.com/maven-central/me.jorgecastillo/hiroaki-android)
======

<img src="./art/sakura_logo.svg" width="256" height="256" />

    Japanese: 'spreading brightness'. Derived from the words 'hiro', which means 'large or wide', and 'aki', 
    which means 'bright or clear'.

The intention of Hiroaki is to achieve clarity on your **API integration tests** in an idiomatic way by simplifying the way you **prepare 
your test environment prior to test execution and the assertions you perform in the end**. 

How it works
------------

It uses Kotlin features like **extension functions**, **type aliases**, **delegation**, 
**package level functions** and many other ones like **custom hamcrest** matchers to achieve the wanted behavior.

When you are testing you want to isolate your tests from external frameworks that can provoke flakiness. **Hiroaki** 
relies on `MockWebServer` to provide a mock server as a target for your HTTP requests that you'll be able to use to mock 
your server behavior.

That enables you to assert over how your app or system will react to some predefined server & API behaviors. 

Where is the magic?
-------------------
Most of the features provided by `Hiroaki` are **extension functions** declared over the  `MockWebServer` type.
 
That means you can easily program and chain mock responses for given request conditions to certain endpoints (à la mockito), mock responses with one liners, assert over recorded request conditions 
or over the response data parsed just with a simple `MockWebServer` instance.

Usage
-----

Add the following code to your ``build.gradle``. Both dependencies are deployed in **Maven Central**.

```groovy
dependencies{
    implementation 'me.jorgecastillo:hiroaki-core:0.0.3'
    implementation 'me.jorgecastillo:hiroaki-android:0.0.3' // Android instrumentation tests
}
```

### Connecting your app to the mock server

Your app will query the real endpoints if you don't configure your retrofit instance to use the 
mock server url. To do that, you can wake up a **mock retrofit service** passing in your **service 
interface** and the **converter** you want to use. 

Finally, you are free to pass this mocked service to your `ApiClient`, `NetworkDataSource`, or 
whatever your collaborator handling your network logic is called.

You'll have to extend the base class `MockServerSuite`, which takes care of running and shutting 
down the server for you. But there's also a JUnit `Rule` if you don't want to.

```kotlin
class GsonNewsNetworkDataSourceTest : MockServerSuite() {

    private lateinit var dataSource: GsonNewsNetworkDataSource

    @Before
    override fun setup() {
        super.setup()
        dataSource = GsonNewsNetworkDataSource(server.retrofitService(
                GsonNewsApiService::class.java,
                GsonConverterFactory.create()))
    }
        
    /*...*/
}
```
This will use a default `OkHttpClient` instance created for you with basic configuration. 
If you need further configuration on the http client, `retrofitService()` extension function offers 
an optional parameter to pass a custom `OkHttpClient`:

```kotlin
val customClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS) // For testing purposes
        .readTimeout(2, TimeUnit.SECONDS) // For testing purposes
        .writeTimeout(2, TimeUnit.SECONDS)
        .build()

dataSource = GsonNewsNetworkDataSource(server.retrofitService(
                GsonNewsApiService::class.java,
                GsonConverterFactory.create(),
                okHttpClient = customClient))
```

If you can't extend `MockServerSuite`, **Hiroaki** also provides a **JUnit rule** called 
`MockServerRule` with the same purpose:

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

**Using Hiroaki without Retrofit (just OkHttp)**

If you're not using `Retrofit` but just [OkHttp](http://square.github.io/okhttp/) you can still use **Hiroaki**. Just 
request the URL from the mock server provided by the base class or the rule, and pass it to your collaborator to create your OkHttp requests.
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
This ensures that **whenever** the endpoint `v2/top-headlines` is called with a `GET` http method, 
the you'll get a response like the one we are mocking there.

You can also program responses for expected query params:
````kotlin
server.whenever(method = Method.GET,
                sentToPath = "v2/top-headlines",
                queryParams = mapOf("sources" to "crypto-coins-news",
                                    "apiKey" to "a7c816f57c004c49a21bd458e11e2807"))
      .thenRespond(success(jsonFileName = "GetNews.json"))
````

This is the declaration of the `whenever()` function, so you can program your mocked responses 
expecting the following request configuration specs: 
````kotlin
fun MockWebServer.whenever(sentToPath: String,
                           queryParams: QueryParams? = null,
                           jsonBodyResFile: JsonBodyFile? = null,
                           jsonBody: JsonBody? = null,
                           headers: Headers? = null,
                           method: Method? = null) {
    /*...*/                           
}
````
You're able to match over path, query params, json body (inline or by resource file), headers, and 
HTTP method.

Also note in the previous snippets the `success()` function when mocking the response. function 
`success()` is a shortcut to provide a successful response. You can also use `error()`  and 
`response()`. All of them are mocking functions that allow you to pass the following **optional** 
arguments:

* `code` **Int** return http status code for the mocked response.
* `jsonFileName` **String** resource file name to load the mocked response json body from.
* `jsonBody` **String** inlined json for your mocked response body.
* `headers` Is a **Map<String,String>** headers to attach to the mocked response.

If you don't want to use the `succes()`, `error()` or `response()` shortcut functions, you can 
still pass your own custom `MockResponse` from `MockWebServer`, the same way you have been doing 
until now. 

**Chaining mocked responses:**

You can also chain a bunch of mocked responses:
````kotlin
server.whenever(Method.GET, "v2/top-headlines")
                .thenRespond(success(jsonFileName = "GetNews.json"))
                .thenRespond(success(jsonFileName = "GetSingleNew.json"))
                .thenRespond(success(jsonFileName = "GetNews.json"))
````
* The first time the endpoint is called under the given conditions, it will return a 
`MockResponse` with the body obtained from the file `GetNews.json`. 
* The second time it gets called, it will return the second mocked response, which on this example 
is reading it's body from `GetSingleNew.json`. 
* The third time it gets called it'll return the third mocked response.

You can chain as many responses as you want, just remember that those **will be dispatched in the 
same order**.

**Dispatching dynamic responses**

Sometimes you want a response to depend on the structure of the request sent. For that reason, 
**Hiroaki** provides the `thenDispatch` method:
````kotlin
server.whenever(Method.GET, "v2/top-headlines")
      .thenDispatch { request -> success(jsonBody = "{\"requestPath\" : ${request.path}" }
````   
With this feature, you could attach the same requesteded item Id to one of the items contained into 
the response body, for example. I guess you can imagine different use cases.

Feel free to combine as many `thenRespond()` and `thenDispatch()` calls as you want.
````kotlin
server.whenever(Method.GET, "v2/top-headlines")
      .thenRespond(success())
      .thenDispatch { request -> success(jsonBody = "{\"requestPath\" : ${request.path}" }
      .thenRespond(error())
````   

**Delay responses**

Sometimes you need to mimic server response delays. `MockWebServer` already provides a function 
`MockResponse.setBodyDelay()` to achieve it, which you can append to any `MockResponse` you create. 
But **Hiroaki** also provides an extension function for `MockResponse` to pass a delay in millis: 
`response.delay(millis)`:

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

Here you have some examples:

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
            method = Method.GET,
            queryParams = params(
                    "sources" to "crypto-coins-news",
                    "apiKey" to "a7c816f57c004c49a21bd458e11e2807"),
            headers = headers("Cache-Control" to "max-age=640000"))
}
```
You can use the functions `never()`, `once()`, `twice()`, `times(num)`, `atLeast`, and `atMost` for the times param.

You can also provide a json body to verify the body sent on your requests (`POST`, `PUT`, `PATCH`). Here you have an inlined body used for the assertion. 

```kotlin
server.verify("v2/top-headlines").called(
            times = once(),
            method = Method.POST,
            headers = headers("Cache-Control" to "max-age=640000"),
            jsonBody = inlineBody("{\n" +
                                    "  \"title\": \"Any Title\",\n" +
                                    "  \"description\": \"Any description\",\n" +
                                    "  \"source\": {\n" +
                                    "    \"link\": \"http://source/123\",\n" +
                                    "    \"name\": \"Some source\"\n" +
                                    "  }\n" +
                                    "}\n"))
````
You can also provide json body for post requests from a file saved on your `/test/resources` directory.
```kotlin
server.verify("v2/top-headlines").called(
            method = Method.POST,
            jsonBodyResFile = fileBody("PublishHeadline.json"))
```

### Parsed Response assertions
After any test that requests data from network you'll probably need to **assert over the parsed 
response** to double check whether the data was received and parsed properly.

To achieve that, **Hiroaki** provides syntax for asserting over equality, so you'd do the following:
```kotlin
@Test
fun parsesNewsProperly() {
    server.enqueueSuccessResponse("GetNews.json")

    val news = runBlocking { dataSource.getNews() }

    news eq expectedNews() // eq is an infix function for assertEquals()
}
``` 
Here `eq` Is just an `infix` function to run `assertEquals` on both objects. Here we are building the list of expected objects with the function `expectedNews()`. 
The objects are being compared using the `equals` operator so you **better use data classes for DTOs or redefine `equals`** properly. 

### Android Instrumentation Tests

You must extend `AndroidMockServerSuite` or use `AndroidMockServerRule` instead. Those classes are equivalent to 
`MockServerSuite` and `MockServerRule`, but they also take care of passing the instrumentation Android `Context` into 
**Hiroaki** so it can load resource files from `androidTest/assets/` for json body files. 

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

