package me.jorgecastillo.hiroaki

import kotlinx.coroutines.runBlocking
import me.jorgecastillo.hiroaki.data.datasource.GsonNewsNetworkDataSource
import me.jorgecastillo.hiroaki.internal.MockServerSuite
import me.jorgecastillo.hiroaki.matchers.times
import me.jorgecastillo.hiroaki.models.error
import me.jorgecastillo.hiroaki.models.fileBody
import me.jorgecastillo.hiroaki.models.inlineBody
import me.jorgecastillo.hiroaki.models.success
import org.junit.Before
import org.junit.Test
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class MockForeverTests : MockServerSuite() {

    private lateinit var dataSource: GsonNewsNetworkDataSource

    @Before
    override fun setup() {
        super.setup()
        dataSource = GsonNewsNetworkDataSource(
            server.retrofitService(GsonConverterFactory.create())
        )
    }

    @Test
    fun shouldRespondMultipleTimes() {
        server.whenever(Method.GET, "v2/top-headlines")
            .thenRespondForever(success(jsonBody = fileBody("GetNews.json")))

        repeat(3) {
            runBlocking { dataSource.getNews() }
        }

        server.verify("v2/top-headlines").called(times(count = 3))
    }

    @Test
    fun shouldDispatchMultipleTimes() {
        server.whenever(Method.GET, "v2/top-headlines")
            .thenDispatchForever { request ->
                success(
                    jsonBody = inlineBody(
                        """{
                              "status": "ok",
                              "totalResults": 2342,
                              "articles": [
                                {
                                  "source": {
                                    "id": ${request.path.length},
                                    "name": "Lifehacker.com"
                                  },
                                  "author": "Jacob Kleinman",
                                  "title": "How to Get Android P's Screenshot Editing Tool on Any Android Phone",
                                  "description": "Last year, Apple brought advanced screenshot editing tools to the iPhone with iOS 11, and, this week, Google fired back with a similar Android feature called Markup. The only catch is that this new tool is limited to Android P, which launches later this year …",
                                  "url": "https://lifehacker.com/how-to-get-android-ps-screenshot-editing-tool-on-any-an-1823646122",
                                  "urlToImage": "https://i.kinja-img.com/gawker-media/image/upload/s--Y-5X_NcT--/c_fill,fl_progressive,g_center,h_450,q_80,w_800/nxmwbkwzoc1z1tmak7s4.jpg",
                                  "publishedAt": "2018-03-09T20:30:00Z"
                                }
                              ]
                            }
                        """
                    )
                )
            }

        repeat(3) {
            runBlocking { dataSource.getNews() }
        }

        server.verify("v2/top-headlines").called(times(count = 3))
    }

    @Test(expected = IOException::class)
    fun regularResponseShouldOverrideRespondForever() {
        server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(error())
            .thenRespondForever(success(jsonBody = fileBody("GetNews.json")))

        runBlocking {
            dataSource.getNews()
        }

        server.verify("v2/top-headlines").called()
    }

    @Test
    fun secondResponseForeverShouldOverrideTheFirst() {
        server.whenever(Method.GET, "v2/top-headlines")
            .thenRespondForever(error())
            .thenRespondForever(success(jsonBody = fileBody("GetNews.json")))

        runBlocking {
            dataSource.getNews()
        }

        server.verify("v2/top-headlines").called()
    }

    @Test
    fun moreGenericMatcherShouldOverridePrevious() {
        server.whenever(
            method = Method.GET,
            sentToPath = "v2/top-headlines",
            queryParams = params(
                "sources" to "crypto-coins-news",
                "apiKey" to "21a12ef352b649caa97499bed2e77350"
            )
        ).thenRespondForever(error())

        server.whenever(sentToPath = "v2/top-headlines")
            .thenRespondForever(success(jsonBody = fileBody("GetNews.json")))

        runBlocking {
            dataSource.getNews()
        }

        server.verify("v2/top-headlines").called()
    }
}
