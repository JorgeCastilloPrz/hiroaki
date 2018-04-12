package me.jorgecastillo.hiroaki

import android.content.Intent
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import me.jorgecastillo.hiroaki.data.service.MoshiNewsApiService
import me.jorgecastillo.hiroaki.internal.AndroidMockServerSuite
import me.jorgecastillo.hiroaki.models.fileBody
import me.jorgecastillo.hiroaki.models.success
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.converter.moshi.MoshiConverterFactory

@LargeTest
@RunWith(AndroidJUnit4::class)
class AndroidVerificationTests :AndroidMockServerSuite() {

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
        return testRule.launchActivity(Intent())
    }

    @Test
    fun showsEmptyCaseIfThereAreNoSuperHeroes() {
        server.whenever(Method.GET, "v2/top-headlines")
                .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        startActivity()

        server.verify("v2/top-headlines").called(times = once())
    }
/*
    @Test
    fun matchesQueryParamsList() {
        server.whenever(Method.POST, "my-fake-service/edit-tag")
                .thenRespond(success())

        service.addNewsTags(listOf("1", "2", "3"), listOf("some-tag-1", "some-tag-2", "some-tag-3"))
                .execute()

        server.verify("my-fake-service/edit-tag").called(
                times = once(),
                queryParams = params(
                        "a" to "some-tag-1",
                        "a" to "some-tag-2",
                        "a" to "some-tag-3"))
    }*/
}
