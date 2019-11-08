package me.jorgecastillo.hiroaki

import kotlinx.coroutines.runBlocking
import me.jorgecastillo.hiroaki.data.datasource.JacksonNewsNetworkDataSource
import me.jorgecastillo.hiroaki.internal.MockServerRule
import me.jorgecastillo.hiroaki.models.fileBody
import me.jorgecastillo.hiroaki.models.success
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import retrofit2.converter.jackson.JacksonConverterFactory
import java.net.InetAddress

@RunWith(Parameterized::class)
class RuleParametersTest(inetAddress: InetAddress, port: Int) {

    private lateinit var dataSource: JacksonNewsNetworkDataSource
    @get:Rule
    val rule = MockServerRule(inetAddress, port)

    @Before
    fun setup() {
        dataSource = JacksonNewsNetworkDataSource(
            rule.server.retrofitService(JacksonConverterFactory.create())
        )
    }

    @Test
    fun sendsGetNews() {
        rule.server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        runBlocking { dataSource.getNews() }

        rule.server.verify("v2/top-headlines").called(
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

    companion object {
        @JvmStatic
        @Parameters(name = "inetAddress={0} port={1}")
        fun parameters() = listOf(
            arrayOf(InetAddress.getByName("localhost"), 0),
            arrayOf(InetAddress.getByName("localhost"), 8888),
            arrayOf(InetAddress.getByName("127.0.0.1"), 0),
            arrayOf(InetAddress.getByName("127.0.0.1"), 9999)
        )
    }
}
