package me.jorgecastillo.hiroaki

import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import me.jorgecastillo.hiroaki.data.datasource.JacksonNewsNetworkDataSource
import me.jorgecastillo.hiroaki.internal.MockServerRule
import me.jorgecastillo.hiroaki.models.fileBody
import me.jorgecastillo.hiroaki.models.success
import me.jorgecastillo.hiroaki.models.throttle
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class ThrottlingTest {

    private lateinit var dataSource: JacksonNewsNetworkDataSource
    @get:Rule
    val rule: MockServerRule = MockServerRule()

    @Before
    fun setup() {
        dataSource = JacksonNewsNetworkDataSource(
            rule.server.retrofitService(JacksonConverterFactory.create())
        )
    }

    @Test
    fun throttlesMockResponse() {
        rule.server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(
                success(jsonBody = fileBody("GetNews.json"))
                    .throttle(1024, 1000)
            )

        val beforeTheQuery = System.currentTimeMillis()
        runBlocking { dataSource.getNews() }
        val afterTheQuery = System.currentTimeMillis()

        assertTrue(afterTheQuery - beforeTheQuery > 1000)
    }

    @Test
    fun throttlesMockResponseBody() {
        rule.server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(
                success(jsonBody = fileBody("GetNews.json"))
                    .throttleBody(1024, 1, TimeUnit.SECONDS)
            )

        val beforeTheQuery = System.currentTimeMillis()
        runBlocking { dataSource.getNews() }
        val afterTheQuery = System.currentTimeMillis()

        assertTrue(afterTheQuery - beforeTheQuery > 1000)
    }

    @Test
    fun respondsInstantlyForNonThrottledResponse() {
        rule.server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        val beforeTheQuery = System.currentTimeMillis()
        runBlocking { dataSource.getNews() }
        val afterTheQuery = System.currentTimeMillis()

        assertTrue(afterTheQuery - beforeTheQuery < 1000)
    }
}
