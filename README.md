Hiroaki [![CircleCI](https://circleci.com/gh/JorgeCastilloPrz/hiroaki/tree/master.svg?style=svg&circle-token=3824cb7754fef5b81f1a67c6e86786df5db242c5)](https://circleci.com/gh/JorgeCastilloPrz/hiroaki/tree/master)
======

<img src="./art/sakura_logo.svg" width="256" height="256" />

    Hiroaki is a Japanese name that literally means 'spreading brightness'. It is derived from the 
    words 'hiro', which means 'large or wide', and 'aki', which means 'bright or clear'.

The intention of Hiroaki is to achieve that on your **API integration tests** by simplifying the way you **prepare 
your test environment prior to test execution and the assertions you perform in the end**. 

It uses Kotlin features like extension functions, type aliases, delegation, package level functions and many other features 
like custom hamcrest matchers to achieve the wanted behavior.

Usage
-----

Add the following code to your ``build.gradle``.

```groovy
dependencies{
    implementation 'com.jorgecastillo:hiroaki:0.0.1'
}
```

### Request assertions

Request assertions are mandatory when testing an API client. **Hiroaki** provides a highly configurable **extension function** working over any `MockWebServer` instance to do that. Any of its arguments are **optional** so you're free to configure the assertion in a way that matches your needs. 

Here you have some examples:

Asserting about the path where the request is sent to, the GET request query parameters, the headers, or even the HTTP method used.
```kotlin
server.assertRequest(
                sentToPath = "v2/top-headlines",
                queryParams = params(
                        "sources" to "crypto-coins-news",
                        "apiKey" to "a7c816f57c004c49a21bd458e11e2807"),
                headers = headers(
                        "Cache-Control" to "max-age=640000"
                ),
                method = "GET")
```
You can also provide a json body to assert over the body sent on your requests (`POST`, `PUT`, `PATCH`). Here you have an inlined body used for the assertion. 

Note that **It's mandatory to provide the network DTO you are using to map that body from**, since `Hiroaki` parses both bodies to objects and uses `equals` to compare the expected and sent bodies. Therefore, it's highly recommended to **use Kotlin `data` classes** for your DTOs (following the standards) or if you don't really want to use them, you'll have to override `equals` on the class and all its nested levels.
```kotlin
server.assertRequest(
                sentToPath = "v2/top-headlines",
                jsonBody = inlineBody("{\n" +
                        "  \"title\": \"Any Title\",\n" +
                        "  \"description\": \"Any description\",\n" +
                        "  \"url\": \"http://any.url\",\n" +
                        "  \"urlToImage\": \"http://any.url/any_image.png\",\n" +
                        "  \"publishedAt\": \"2018-03-10T14:09:00Z\",\n" +
                        "  \"source\": {\n" +
                        "    \"id\": \"AnyId\",\n" +
                        "    \"name\": \"ANYID\"\n" +
                        "  }\n" +
                        "}\n", ArticleDto::class.java))
````
You can also provide json body for post requests from a file saved on your `/test/resources` directory.
```kotlin
server.assertRequest(
                sentToPath = "v2/top-headlines",
                jsonBodyResFile = fileBody("PublishHeadline.json", ArticleDto::class.java),
                method = "POST")
```
Again, any of this parameters are optional so feel free to configure the assertion the way you need.

### Parsed assertions
After any test that requests data from network you'll probably need to **assert over the returned 
data after parsing it** to double check whether the response was received and parsed properly to the 
expected object/s.

To achieve that, **Hiroaki** provides syntax for asserting over equality, so you'd do the following:
```kotlin
@Test
fun parsesNewsProperly() {
    server.enqueueSuccessResponse("GetNews.json")

    val news = runBlocking { dataSource.getNews() }

    news eq expectedNews() // eq is an infix function for assertEquals()
}
``` 
So `eq` Is just an `infix` function to `assertEquals` both objects. That means the objects are being 
compared using the `equals` operator so you better use data classes or redefine `equals` properly 
for your returned classes. 

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

