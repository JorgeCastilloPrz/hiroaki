package com.jorgecastillo.hiroaki.internal

import com.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
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
        DispatcherRetainer.dispatcher.reset()
    }

    @After
    open fun tearDown() {
        server.shutdown()
        DispatcherRetainer.dispatcher.reset()
        server.setDispatcher(QueueDispatcher())
    }
}
