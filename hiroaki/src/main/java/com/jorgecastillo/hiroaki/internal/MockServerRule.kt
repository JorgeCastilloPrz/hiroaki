package com.jorgecastillo.hiroaki.internal

import com.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
import org.junit.Before
import org.junit.rules.ExternalResource

class MockServerRule : ExternalResource() {

    lateinit var server: MockWebServer

    @Before
    override fun before() {
        super.before()
        server = MockWebServer()
        DispatcherRetainer.dispatcher.reset()
    }

    override fun after() {
        super.after()
        DispatcherRetainer.dispatcher.reset()
        server.setDispatcher(QueueDispatcher())
    }
}
