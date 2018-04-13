package me.jorgecastillo.hiroaki

import android.content.Intent
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import kotlinx.coroutines.experimental.runBlocking
import me.jorgecastillo.hiroaki.data.service.MoshiNewsApiService
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
        val mockService = server.retrofitService(
                MoshiNewsApiService::class.java,
                MoshiConverterFactory.create())
        getApp()
                .service = mockService
    }

    private fun startActivity(): MainActivity {
        return runBlocking { testRule.launchActivity(Intent()) }
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
                        "apiKey" to "a7c816f57c004c49a21bd458e11e2807"))
    }

    @Test
    fun verifiesEndpointNotCalled() {
        server.whenever(Method.GET, "v2/top-headlines")
                .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        startActivity()

        server.verify("v2/news").called(times = never())
    }
}
