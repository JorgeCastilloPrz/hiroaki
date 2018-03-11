package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.data.datasource.JacksonNewsNetworkDataSource
import com.jorgecastillo.hiroaki.data.networkdto.MoshiArticleDto
import com.jorgecastillo.hiroaki.data.service.JacksonNewsApiService
import com.jorgecastillo.hiroaki.model.Article
import com.jorgecastillo.hiroaki.mother.anyArticle
import kotlinx.coroutines.experimental.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class JacksonNewsNetworkDataSourceTest {

    private lateinit var dataSource: JacksonNewsNetworkDataSource
    private lateinit var server: HiroakiServer

    @Before
    fun setup() {
        server = HiroakiServer()
        dataSource = JacksonNewsNetworkDataSource(server.retrofitService(
                JacksonNewsApiService::class.java,
                JacksonConverterFactory.create()))
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
                queryParams = mapOf(
                        "sources" to "crypto-coins-news",
                        "apiKey" to "a7c816f57c004c49a21bd458e11e2807"),
                headers = mapOf(
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
                jsonBodyResFile = "PublishHeadline.json" withType MoshiArticleDto::class.java)
    }

    @Test
    fun sendsPublishHeadlineUsingInlineBody() {
        server.enqueueSuccessResponse()
        val article = anyArticle()

        runBlocking { dataSource.publishHeadline(article) }

        server.assertRequest(
                sentToPath = "v2/top-headlines",
                jsonBody = "{\n" +
                        "  \"title\": \"Any Title\",\n" +
                        "  \"description\": \"Any description\",\n" +
                        "  \"url\": \"http://any.url\",\n" +
                        "  \"urlToImage\": \"http://any.url/any_image.png\",\n" +
                        "  \"publishedAt\": \"2018-03-10T14:09:00Z\",\n" +
                        "  \"source\": {\n" +
                        "    \"id\": \"AnyId\",\n" +
                        "    \"name\": \"ANYID\"\n" +
                        "  }\n" +
                        "}\n" withType MoshiArticleDto::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwsWhenYouPassBothBodyParams() {
        server.enqueueSuccessResponse()
        val article = anyArticle()

        runBlocking { dataSource.publishHeadline(article) }

        server.assertRequest(
                sentToPath = "v2/top-headlines",
                jsonBodyResFile = "PublishHeadline.json" withType MoshiArticleDto::class.java,
                jsonBody = "{\"title\" = \"Any title\" }" withType MoshiArticleDto::class.java)
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
