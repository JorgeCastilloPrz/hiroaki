package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.model.Article
import com.jorgecastillo.hiroaki.mother.anyArticle
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class NewsNetworkDataSourceTest {

    private lateinit var dataSource: NewsNetworkDataSource
    private lateinit var server: MockWebServer

    @Before
    fun setup() {
        server = MockWebServer()
        val service = server.retrofitService(
                NewsApiService::class.java,
                MoshiConverterFactory.create())

        dataSource = NewsNetworkDataSource(service)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun sendsGetNews() {
        server.enqueueSuccessResponse("GetNews.json")

        runBlocking { dataSource.getNews() }

        server.assertRequest(
                sentToPath = "v2/top-headlines",
                queryParams = listOf(
                        "sources" to "crypto-coins-news",
                        "apiKey" to "a7c816f57c004c49a21bd458e11e2807"),
                headers = listOf(
                        "Cache-Control" to "max-age=640000"
                ))
    }

    @Test
    fun sendsPublishHeadline() {
        server.enqueueSuccessResponse()
        val article = anyArticle()

        runBlocking { dataSource.publishHeadline(article) }

        server.assertRequest(
                sentToPath = "v2/top-headlines",
                jsonBodyFileName = "PublishHeadline.json")
    }

    @Test
    fun parsesNewsProperly() {
        server.enqueueSuccessResponse("GetNews.json")

        val news = runBlocking { dataSource.getNews() }

        thenNewsAreParsed(news)
    }

    @Test(expected = IOException::class)
    fun throwsIOExceptionOnGetNewsErrorResponse() {
        server.enqueueErrorResponse()

        runBlocking { dataSource.getNews() }
    }

    private fun thenNewsAreParsed(news: List<Article>) {
        assertThat(news.size, `is`(3))
    }
}
