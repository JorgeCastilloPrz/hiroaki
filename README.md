Hiroaki [![CircleCI](https://circleci.com/gh/JorgeCastilloPrz/hiroaki/tree/master.svg?style=svg&circle-token=3824cb7754fef5b81f1a67c6e86786df5db242c5)](https://circleci.com/gh/JorgeCastilloPrz/hiroaki/tree/master)
======

<img src="./art/sakura_logo.svg" width="256" height="256" />

    Hiroaki is a Japanese name that literally means 'spreading brightness'. It is derived from the 
    words 'hiro', which means 'large or wide', and 'aki', which means 'bright or clear'.

The intention of Hiroaki is to achieve clarity on your **API integration tests** in an idiomatic way by simplifying the way you **prepare 
your test environment prior to test execution and the assertions you perform in the end**. 

How it works
------------

It uses Kotlin features like extension functions, type aliases, delegation, package level functions and many other features 
like custom hamcrest matchers to achieve the wanted behavior.

When you are testing you want to isolate your tests from external frameworks that can provoke flakiness. 
**Hiroaki** relies on `MockWebServer` to provide a mock server as a target for your HTTP requests 
that you'll be able to use to mock your server behavior.

That enables you to assert over how your app or system will react to some predefined server & API 
behaviors. 

Usage
-----

Add the following code to your ``build.gradle``.

```groovy
dependencies{
    implementation 'com.jorgecastillo:hiroaki:0.0.1'
}
```

### Connecting your app to the mock server

Your app will query the real endpoints if you don't configure your retrofit instance to use the 
mock server url. To do that, you can wake up a **mock retrofit service** passing in your **service 
interface** and the **converter** you want to use. 

Finally, you are free to pass this mocked service to your `ApiClient`, `NetworkDataSource`, or 
whatever your collaborator using it is called.

Note that you'll also need to extend the base class `MockServerSuite`, which takes care of running 
and shutting down the server for you.

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

If you can't extend `MockServerSuite`, **Hiroaki** also provides a JUnit rule called 
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

### Request assertions

**Hiroaki** provides a highly configurable **extension function** working over any `MockWebServer` instance to perform assertions over your requests. Any of its arguments are **optional** so you're free to configure the assertion in a way that matches your needs. 

Here you have some examples:

Here I am asserting about: path where the request was sent to, query parameters, headers, and the HTTP method used.
```kotlin
server.assertRequest(
                sentToPath = "v2/top-headlines",
                queryParams = params(
                        "sources" to "crypto-coins-news",
                        "apiKey" to "a7c816f57c004c49a21bd458e11e2807"),
                headers = headers(
                        "Cache-Control" to "max-age=640000"
                ),
                method = Method.GET)
```
You can also provide a json body to assert over the body sent on your requests (`POST`, `PUT`, `PATCH`). Here you have an inlined body used for the assertion. 

*Note that **It's mandatory to provide the network DTO you are using to map that body from**, since `Hiroaki` parses both bodies to objects and uses `equals` to compare the **expected** vs **sent** bodies. Therefore, it's highly recommended to **use Kotlin `data` classes** for your DTOs (following the standards) or if you don't really want to use them, you'll have to override `equals` on the class and all its nested levels.*
```kotlin
server.assertRequest(
                sentToPath = "v2/top-headlines",
                jsonBody = inlineBody("{\n" +
                        "  \"title\": \"Any Title\",\n" +
                        "  \"description\": \"Any description\",\n" +
                        "  \"source\": {\n" +
                        "    \"link\": \"http://source/123\",\n" +
                        "    \"name\": \"Some source\"\n" +
                        "  }\n" +
                        "}\n", ArticleDto::class.java))
````
You can also provide json body for post requests from a file saved on your `/test/resources` directory.
```kotlin
server.assertRequest(
                sentToPath = "v2/top-headlines",
                jsonBodyResFile = fileBody("PublishHeadline.json", ArticleDto::class.java),
                method = Method.POST)
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
The objects are being compared using the `equals` operator so again, you **better use data classes or redefine `equals`** for your returned classes. 

Do you want to contribute?
--------------------------

I would love to get contributions from anybody. So if you feel that the library is lacking any features 
you consider key, please open an issue asking for it or a pull request providing an implementation for it. 

Any PR's must pass CI and that includes code style. Run the following commands to check code style or 
automatically format it. (You can use the graddle wrapper (`gradlew`) instead)
```groovy
// check code style
gradle app:ktlint
gradle hiroaki:ktlint 

// autoformat
gradle app:ktlintFormat
gradle hiroaki:ktlintFormat
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

