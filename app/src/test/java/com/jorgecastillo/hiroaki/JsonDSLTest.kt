package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.data.datasource.GsonNewsNetworkDataSource
import com.jorgecastillo.hiroaki.data.service.GsonNewsApiService
import com.jorgecastillo.hiroaki.internal.MockServerSuite
import com.jorgecastillo.hiroaki.model.Article
import com.jorgecastillo.hiroaki.model.Source
import com.jorgecastillo.hiroaki.models.inlineBody
import com.jorgecastillo.hiroaki.models.success
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(MockitoJUnitRunner::class)
class JsonDSLTest : MockServerSuite() {

    private lateinit var dataSource: GsonNewsNetworkDataSource

    @Before
    override fun setup() {
        super.setup()
        dataSource = GsonNewsNetworkDataSource(server.retrofitService(
                GsonNewsApiService::class.java,
                GsonConverterFactory.create()))
    }

    @Test
    fun enqueuesSuccess() {
        server.whenever(Method.GET, "v2/top-headlines")
                .thenDispatch { request ->
                    success(jsonBody = inlineBody("{\n" +
                            "  \"status\": \"ok\",\n" +
                            "  \"totalResults\": 2342,\n" +
                            "  \"articles\": [\n" +
                            "    {\n" +
                            "      \"source\": {\n" +
                            "        \"id\": ${request.path.length},\n" +
                            "        \"name\": \"Lifehacker.com\"\n" +
                            "      },\n" +
                            "      \"author\": \"Jacob Kleinman\",\n" +
                            "      \"title\": \"How to Get Android P's Screenshot Editing Tool on Any Android Phone\",\n" +
                            "      \"description\": \"Last year, Apple brought advanced screenshot editing tools to the iPhone with iOS 11, and, this week, Google fired back with a similar Android feature called Markup. The only catch is that this new tool is limited to Android P, which launches later this year …\",\n" +
                            "      \"url\": \"https://lifehacker.com/how-to-get-android-ps-screenshot-editing-tool-on-any-an-1823646122\",\n" +
                            "      \"urlToImage\": \"https://i.kinja-img.com/gawker-media/image/upload/s--Y-5X_NcT--/c_fill,fl_progressive,g_center,h_450,q_80,w_800/nxmwbkwzoc1z1tmak7s4.jpg\",\n" +
                            "      \"publishedAt\": \"2018-03-09T20:30:00Z\"\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}"))
                }

        val singleNew = runBlocking { dataSource.getNews() }

        singleNew eq expectedSingleNew(83)
    }

    private fun expectedSingleNew(requestPathLengthAsSourceId: Int? = null): List<Article> = listOf(
            Article("How to Get Android P's Screenshot Editing Tool on Any Android Phone",
                    "Last year, Apple brought advanced screenshot editing tools to the iPhone with iOS 11, and, this week, Google fired back with a similar Android feature called Markup. The only catch is that this new tool is limited to Android P, which launches later this year …",
                    "https://lifehacker.com/how-to-get-android-ps-screenshot-editing-tool-on-any-an-1823646122",
                    "https://i.kinja-img.com/gawker-media/image/upload/s--Y-5X_NcT--/c_fill,fl_progressive,g_center,h_450,q_80,w_800/nxmwbkwzoc1z1tmak7s4.jpg",
                    "2018-03-09T20:30:00Z",
                    Source(if (requestPathLengthAsSourceId != null) "$requestPathLengthAsSourceId" else null, "Lifehacker.com")))
}
