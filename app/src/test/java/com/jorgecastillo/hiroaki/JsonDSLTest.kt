package com.jorgecastillo.hiroaki

import me.jorgecastillo.hiroaki.data.networkdto.MoshiArticleDto
import me.jorgecastillo.hiroaki.data.networkdto.MoshiSourceDto
import com.jorgecastillo.hiroaki.internal.MockServerSuite
import com.jorgecastillo.hiroaki.models.json
import com.jorgecastillo.hiroaki.models.jsonArray
import com.jorgecastillo.hiroaki.models.success
import com.jorgecastillo.hiroaki.services.SomeService
import com.jorgecastillo.hiroaki.services.dto.NonNestedData
import com.jorgecastillo.hiroaki.services.dto.NonNestedDataNumericArray
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(MockitoJUnitRunner::class)
class JsonDSLTest : MockServerSuite() {

    lateinit var service: SomeService

    @Before
    override fun setup() {
        super.setup()
        service = server.retrofitService(
                SomeService::class.java,
                GsonConverterFactory.create()
        )
    }

    @Test
    fun respondsJsonDSLNonNested() {
        server.whenever(Method.GET, "my-fake-service/1")
                .thenRespond(success(jsonBody = json {
                    "first" / "ok"
                    "second" / 2342
                    "third" / jsonArray<String>()
                }))

        val parsedData = service.getSomeNonNestedData()
                .execute()
                .body()

        parsedData eq NonNestedData("ok", 2342, listOf())
    }

    @Test
    fun failsJsonDSLNonNested() {
        server.whenever(Method.GET, "my-fake-service/1")
                .thenRespond(success(jsonBody = json {
                    "first" / "ok"
                    "second" / 2341
                    "third" / jsonArray<String>()
                }))

        val parsedData = service.getSomeNonNestedData()
                .execute()
                .body()

        assertNotEquals(parsedData, NonNestedData("ok", 2342, listOf()))
    }

    @Test
    fun respondsDSLArrayWithPlainStringValue() {
        server.whenever(Method.GET, "my-fake-service/1")
                .thenRespond(success(jsonBody = json {
                    "first" / "ok"
                    "second" / 2341
                    "third" / jsonArray("Something", "More something")
                }))

        val parsedData = service.getSomeNonNestedData()
                .execute()
                .body()

        assertNotEquals(parsedData, NonNestedData("ok", 2342, listOf()))
    }

    @Test
    fun respondsDSLArrayWithPlainNumeric() {
        server.whenever(Method.GET, "my-fake-service/1")
                .thenRespond(success(jsonBody = json {
                    "first" / "ok"
                    "second" / 2342
                    "third" / jsonArray(123, 456)
                }))

        val parsedData = service.getSomeNonNestedDataNumericArray()
                .execute()
                .body()

        parsedData eq NonNestedDataNumericArray("ok", 2342, listOf(123, 456))
    }

    @Test
    fun respondsDSLNestedJson() {
        server.whenever(Method.GET, "my-fake-service/1")
                .thenDispatch { request ->
                    success(jsonBody = json {
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
                    })
                }

        val parsedData = service.getNestedJson()
                .execute()
                .body()
                ?.articles

        parsedData eq expectedSingleNew(18)
    }

    @Test
    fun respondsDSLArrayAtRootLevel() {
        server.whenever(Method.GET, "my-fake-service/1")
                .thenRespond(success(jsonBody = jsonArray(1, 2, 3)))

        val parsedData = service.getSomeJsonWithArrayOnRootLevel()
                .execute()
                .body()

        parsedData eq arrayListOf(1, 2, 3)
    }

    @Test
    fun respondsDSLStringArrayAtRootLevel() {
        server.whenever(Method.GET, "my-fake-service/1")
                .thenRespond(success(jsonBody = jsonArray("Something", "More stuff", "Something more")))

        val parsedData = service.getSomeJsonWithStringArrayOnRootLevel()
                .execute()
                .body()

        parsedData eq arrayListOf("Something", "More stuff", "Something more")
    }

    @Test
    fun respondsDSLArrayWithJsonObjectsAtRootLevel() {
        server.whenever(Method.GET, "my-fake-service/1")
                .thenRespond(
                        success(
                                jsonBody = jsonArray(
                                        json {
                                            "status" / "ok"
                                            "totalResults" / 2342
                                            "articles" / jsonArray(json {
                                                "source" / json {
                                                    "id" / 18
                                                    "name" / "Lifehacker.com"
                                                }
                                                "author" / "Jacob Kleinman"
                                                "title" / "How to Get Android P's Screenshot Editing Tool on Any Android Phone"
                                                "description" / "Last year, Apple brought advanced screenshot editing tools to the iPhone with iOS 11, and, this week, Google fired back with a similar Android feature called Markup. The only catch is that this new tool is limited to Android P, which launches later this year …"
                                                "url" / "https://lifehacker.com/how-to-get-android-ps-screenshot-editing-tool-on-any-an-1823646122"
                                                "urlToImage" / "https://i.kinja-img.com/gawker-media/image/upload/s--Y-5X_NcT--/c_fill,fl_progressive,g_center,h_450,q_80,w_800/nxmwbkwzoc1z1tmak7s4.jpg"
                                                "publishedAt" / "2018-03-09T20:30:00Z"
                                            })
                                        },
                                        json {
                                            "status" / "ok"
                                            "totalResults" / 2342
                                            "articles" / jsonArray(json {
                                                "source" / json {
                                                    "id" / 18
                                                    "name" / "Lifehacker.com"
                                                }
                                                "author" / "Jacob Kleinman"
                                                "title" / "How to Get Android P's Screenshot Editing Tool on Any Android Phone"
                                                "description" / "Last year, Apple brought advanced screenshot editing tools to the iPhone with iOS 11, and, this week, Google fired back with a similar Android feature called Markup. The only catch is that this new tool is limited to Android P, which launches later this year …"
                                                "url" / "https://lifehacker.com/how-to-get-android-ps-screenshot-editing-tool-on-any-an-1823646122"
                                                "urlToImage" / "https://i.kinja-img.com/gawker-media/image/upload/s--Y-5X_NcT--/c_fill,fl_progressive,g_center,h_450,q_80,w_800/nxmwbkwzoc1z1tmak7s4.jpg"
                                                "publishedAt" / "2018-03-09T20:30:00Z"
                                            })
                                        },
                                        json {
                                            "status" / "ok"
                                            "totalResults" / 2342
                                            "articles" / jsonArray(json {
                                                "source" / json {
                                                    "id" / 18
                                                    "name" / "Lifehacker.com"
                                                }
                                                "author" / "Jacob Kleinman"
                                                "title" / "How to Get Android P's Screenshot Editing Tool on Any Android Phone"
                                                "description" / "Last year, Apple brought advanced screenshot editing tools to the iPhone with iOS 11, and, this week, Google fired back with a similar Android feature called Markup. The only catch is that this new tool is limited to Android P, which launches later this year …"
                                                "url" / "https://lifehacker.com/how-to-get-android-ps-screenshot-editing-tool-on-any-an-1823646122"
                                                "urlToImage" / "https://i.kinja-img.com/gawker-media/image/upload/s--Y-5X_NcT--/c_fill,fl_progressive,g_center,h_450,q_80,w_800/nxmwbkwzoc1z1tmak7s4.jpg"
                                                "publishedAt" / "2018-03-09T20:30:00Z"
                                            })
                                        })
                        )
                )

        val parsedData = service.getSomeJsonArrayWithJsonObjectsOnRootLevel()
                .execute()
                .body()
                ?.flatMap { it.articles }

        parsedData eq arrayListOf(expectedSingleNew(18)[0], expectedSingleNew(18)[0], expectedSingleNew(18)[0])
    }

    private fun expectedSingleNew(requestPathLengthAsSourceId: Int? = null): List<MoshiArticleDto> = listOf(
            MoshiArticleDto(
                    "How to Get Android P's Screenshot Editing Tool on Any Android Phone",
                    "Last year, Apple brought advanced screenshot editing tools to the iPhone with iOS 11, and, this week, Google fired back with a similar Android feature called Markup. The only catch is that this new tool is limited to Android P, which launches later this year …",
                    "https://lifehacker.com/how-to-get-android-ps-screenshot-editing-tool-on-any-an-1823646122",
                    "https://i.kinja-img.com/gawker-media/image/upload/s--Y-5X_NcT--/c_fill,fl_progressive,g_center,h_450,q_80,w_800/nxmwbkwzoc1z1tmak7s4.jpg",
                    "2018-03-09T20:30:00Z",
                    MoshiSourceDto(
                            if (requestPathLengthAsSourceId != null) "$requestPathLengthAsSourceId" else null,
                            "Lifehacker.com"
                    )
            )
    )
}
