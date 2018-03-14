package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.data.datasource.JacksonNewsNetworkDataSource
import com.jorgecastillo.hiroaki.data.networkdto.MoshiArticleDto
import com.jorgecastillo.hiroaki.data.service.JacksonNewsApiService
import com.jorgecastillo.hiroaki.model.Article
import com.jorgecastillo.hiroaki.model.Source
import com.jorgecastillo.hiroaki.models.fileBody
import com.jorgecastillo.hiroaki.models.inlineBody
import com.jorgecastillo.hiroaki.mother.anyArticle
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException
import com.jorgecastillo.hiroaki.Method.GET
import com.jorgecastillo.hiroaki.Method.POST

@RunWith(MockitoJUnitRunner::class)
class JacksonNewsNetworkDataSourceTest : MockServerSuite() {

    private lateinit var dataSource: JacksonNewsNetworkDataSource

    @Before
    override fun setup() {
        super.setup()
        dataSource = JacksonNewsNetworkDataSource(server.retrofitService(
                JacksonNewsApiService::class.java,
                JacksonConverterFactory.create()))
    }

    @Test
    fun sendsGetNews() {
        server.enqueueSuccessResponse("GetNews.json")

        runBlocking { dataSource.getNews() }

        server.assertRequest(
                sentToPath = "v2/top-headlines",
                queryParams = params(
                        "sources" to "crypto-coins-news",
                        "apiKey" to "a7c816f57c004c49a21bd458e11e2807"),
                headers = headers(
                        "Cache-Control" to "max-age=640000"
                ),
                method = GET)
    }

    @Test
    fun sendsPublishHeadline() {
        server.enqueueSuccessResponse()
        val article = anyArticle()

        runBlocking { dataSource.publishHeadline(article) }

        server.assertRequest(
                sentToPath = "v2/top-headlines",
                jsonBodyResFile = fileBody("PublishHeadline.json", MoshiArticleDto::class.java),
                method = POST)
    }

    @Test
    fun sendsPublishHeadlineUsingInlineBody() {
        server.enqueueSuccessResponse()
        val article = anyArticle()

        runBlocking { dataSource.publishHeadline(article) }

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
                        "}\n", MoshiArticleDto::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwsWhenYouPassBothBodyParams() {
        server.enqueueSuccessResponse()
        val article = anyArticle()

        runBlocking { dataSource.publishHeadline(article) }

        server.assertRequest(
                sentToPath = "v2/top-headlines",
                jsonBodyResFile = fileBody("PublishHeadline.json", MoshiArticleDto::class.java),
                jsonBody = inlineBody("{\"title\" = \"Any title\" }", MoshiArticleDto::class.java))
    }

    @Test
    fun parsesNewsProperly() {
        server.enqueueSuccessResponse("GetNews.json")

        val news = runBlocking { dataSource.getNews() }

        news eq expectedNews()
    }

    @Test(expected = IOException::class)
    fun throwsIOExceptionOnGetNewsErrorResponse() {
        server.enqueueErrorResponse()

        runBlocking { dataSource.getNews() }
    }

    private fun expectedNews(): List<Article> {
        return listOf(
                Article("How to Get Android P's Screenshot Editing Tool on Any Android Phone",
                        "Last year, Apple brought advanced screenshot editing tools to the iPhone with iOS 11, and, this week, Google fired back with a similar Android feature called Markup. The only catch is that this new tool is limited to Android P, which launches later this year …",
                        "https://lifehacker.com/how-to-get-android-ps-screenshot-editing-tool-on-any-an-1823646122",
                        "https://i.kinja-img.com/gawker-media/image/upload/s--Y-5X_NcT--/c_fill,fl_progressive,g_center,h_450,q_80,w_800/nxmwbkwzoc1z1tmak7s4.jpg",
                        "2018-03-09T20:30:00Z",
                        Source(null, "Lifehacker.com")),
                Article("Capital One's virtual credit cards could help you avoid fraud",
                        "Capital One is no stranger to trying new things -- especially when it comes to technology. Its Eno texting chatbot, for example, is a quick and conversational way for its customers to check their balances and perform simple tasks, like checking on recent tran…",
                        "https://www.engadget.com/2018/03/09/capital-one-virtual-credit-cards/",
                        "https://o.aolcdn.com/images/dims?thumbnail=1200%2C630&quality=80&image_uri=https%3A%2F%2Fo.aolcdn.com%2Fimages%2Fdims%3Fcrop%3D3861%252C2574%252C0%252C0%26quality%3D85%26format%3Djpg%26resize%3D1600%252C1067%26image_uri%3Dhttp%253A%252F%252Fo.aolcdn.com%252Fhss%252Fstorage%252Fmidas%252Fca9d74d8d57677d4f3291ae4347b26da%252F206197048%252Fcapital-one-financial-corp-signage-is-displayed-at-a-bank-branch-in-picture-id120933061%26client%3Da1acac3e1b3290917d92%26signature%3Db51d9958f8487452f6a8c6d354650fcb339f0520&client=cbc79c14efcebee57402&signature=44e59feb505c1b9c4e5867d453d3ed345010acac",
                        "2018-03-09T13:00:00Z",
                        Source("engadget", "Engadget")),
                Article("Magic Leap gets \$461M more, Travis goes VC, and HQ Trivia scales up",
                        "Hello and welcome back to Equity, TechCrunch’s venture capital-focused podcast where we unpack the numbers behind the headlines. This week we had a corking set of news to get through, so we rounded up the usual gang (Matthew Lynley, Katie Roof, and myself), a…",
                        "http://techcrunch.com/2018/03/09/magic-leap-gets-461m-more-travis-goes-vc-and-hq-trivia-scales-up/",
                        "https://tctechcrunch2011.files.wordpress.com/2017/03/tc-equity-podcast-ios.jpg",
                        "2018-03-09T14:10:01Z",
                        Source("techcrunch", "TechCrunch"))
        )
    }
}
