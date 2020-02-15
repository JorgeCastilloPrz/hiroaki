package me.jorgecastillo.hiroaki.junit5

import me.jorgecastillo.hiroaki.Method
import me.jorgecastillo.hiroaki.data.datasource.JacksonNewsNetworkDataSource
import me.jorgecastillo.hiroaki.headers
import me.jorgecastillo.hiroaki.models.fileBody
import me.jorgecastillo.hiroaki.models.success
import me.jorgecastillo.hiroaki.once
import me.jorgecastillo.hiroaki.params
import me.jorgecastillo.hiroaki.retrofitService
import me.jorgecastillo.hiroaki.verify
import me.jorgecastillo.hiroaki.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import retrofit2.converter.jackson.JacksonConverterFactory

class MockServerExtensionTest {

    @RegisterExtension
    @JvmField
    val serverExtension = MockServerExtension()

    private lateinit var dataSource: JacksonNewsNetworkDataSource

    @BeforeEach
    fun setup() {
        dataSource = JacksonNewsNetworkDataSource(
            serverExtension.server.retrofitService(JacksonConverterFactory.create())
        )
    }

    @Test
    fun sendsGetNews() {
        serverExtension.server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        dataSource.getNews()

        serverExtension.server.verify("v2/top-headlines").called(
            times = once(),
            queryParams = params(
                "sources" to "crypto-coins-news",
                "apiKey" to "21a12ef352b649caa97499bed2e77350"
            ),
            headers = headers(
                "Cache-Control" to "max-age=640000"
            ),
            method = Method.GET
        )
    }
}
