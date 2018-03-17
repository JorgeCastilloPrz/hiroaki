package com.jorgecastillo.hiroaki

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.jorgecastillo.hiroaki.Method.GET
import com.jorgecastillo.hiroaki.internal.MockServerSuite
import com.jorgecastillo.hiroaki.models.success
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations

@LargeTest
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest : MockServerSuite() {

    @get:Rule val testRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    override fun setup() {
        super.setup()
        MockitoAnnotations.initMocks(this)
    }

    private fun startActivity(): MainActivity {
        return testRule.launchActivity(Intent())
    }

    @Test
    fun showsEmptyCaseIfThereAreNoSuperHeroes() {
        server.whenever(GET, "v2/top-headlines")
                .thenRespond(success(jsonFileName = "GetNews.json"))

        startActivity()
    }
}
