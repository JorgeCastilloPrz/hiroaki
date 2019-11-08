package me.jorgecastillo.hiroaki

import android.content.Intent
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import me.jorgecastillo.hiroaki.internal.AndroidMockServerSuite
import me.jorgecastillo.hiroaki.matchers.never
import me.jorgecastillo.hiroaki.models.fileBody
import me.jorgecastillo.hiroaki.models.success
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.converter.moshi.MoshiConverterFactory

@LargeTest
@RunWith(AndroidJUnit4::class)
class AndroidVerificationTests : AndroidMockServerSuite() {

    @get:Rule
    val testRule: ActivityTestRule<MainActivity> = ActivityTestRule(
        MainActivity::class.java, true, false)

    @Before
    override fun setup() {
        super.setup()
        getApp().service = server.retrofitService(MoshiConverterFactory.create())
    }

    private fun startActivity(): MainActivity {
        return testRule.launchActivity(Intent())
    }

    @Test
    fun verifiesEndpointCalled() {
        server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        startActivity()

        server.verify("v2/top-headlines").called(times = once())
    }

    @Test
    fun verifiesHeadersOnEndpointCalled() {
        server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        startActivity()

        server.verify("v2/top-headlines").called(
            times = once(),
            headers = headers("Cache-Control" to "max-age=640000"))
    }

    @Test
    fun verifiesQueryParamsOnEndpointCalled() {
        server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        startActivity()

        server.verify("v2/top-headlines").called(
            times = once(),
            queryParams = params(
                "sources" to "crypto-coins-news",
                "apiKey" to "21a12ef352b649caa97499bed2e77350"))
    }

    @Test
    fun verifiesEndpointNotCalled() {
        server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        startActivity()

        server.verify("v2/news").called(times = never())
    }
}
