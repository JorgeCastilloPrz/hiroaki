package com.jorgecastillo.hiroaki

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
    }

    @After
    open fun tearDown() {
        server.shutdown()
    }
}
