package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.model.Article
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class NewsNetworkDataSourceTest {

    private lateinit var dataSource: NewsNetworkDataSource
    private lateinit var server: MockWebServer

    @Before
    fun setup() {
        server = MockWebServer()
        val service = server.mockService(NewsApiService::class.java)
        dataSource = NewsNetworkDataSource(service)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    suspend fun sendsGetNewsToRequiredEndpoint() {
        server.enqueueSuccessfulResponse("GetNews.json")

        dataSource.getNews()

        server.assertRequestSendToPath("/v2/top-headlines")
    }

    @Test
    suspend fun parsesNewsProperly() {
        server.enqueueSuccessfulResponse("api/GetRidesSummary.json")

        val news = dataSource.getNews()

        thenNewsAreParsed(news)
    }

    @Test(expected = IOException::class)
    suspend fun throwsIOExceptionOnGetNewsErrorResponse() {
        server.enqueueErrorResponse()

        dataSource.getNews()
    }

    private fun thenNewsAreParsed(news: List<Article>) {
        assertThat(news.size, `is`(4))
    }
}
