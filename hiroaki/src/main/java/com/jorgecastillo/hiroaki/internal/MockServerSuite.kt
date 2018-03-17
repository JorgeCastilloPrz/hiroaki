package com.jorgecastillo.hiroaki.internal

import com.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before

/**
 * Base class to provide the mock server before and after the test execution. Intentionally avoided
 * using a rule for readability (not requiring tests to access server through the rule like
 * rule.server.enqueue() but directly server.enqueue()).
 */
open class MockServerSuite {
    lateinit var server: MockWebServer

    @Before
    open fun setup() {
        server = MockWebServer()
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
