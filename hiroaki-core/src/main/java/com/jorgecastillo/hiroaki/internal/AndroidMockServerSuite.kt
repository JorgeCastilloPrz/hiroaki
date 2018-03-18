package com.jorgecastillo.hiroaki.internal

import android.support.test.InstrumentationRegistry
import com.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import com.jorgecastillo.hiroaki.setAndroidContext
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before

/**
 * Base class to provide the mock server before and after the test execution. This is the Android version which also
 * sets the android Context up into the library so it can easily reach asset resources for json body files.
 */
open class AndroidMockServerSuite {
    lateinit var server: MockWebServer

    @Before
    open fun setup() {
        server = MockWebServer()
        server.setAndroidContext(InstrumentationRegistry.getContext())
        DispatcherRetainer.resetDispatchers()
        server.start()
    }

    @After
    open fun tearDown() {
        server.shutdown()
        DispatcherRetainer.resetDispatchers()
        server.setDispatcher(DispatcherRetainer.queueDispatcher)
    }
}
