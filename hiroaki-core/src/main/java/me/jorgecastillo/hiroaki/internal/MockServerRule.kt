package me.jorgecastillo.hiroaki.internal

import me.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.rules.ExternalResource

class MockServerRule : ExternalResource() {

    lateinit var server: MockWebServer

    @Before
    override fun before() {
        server = MockWebServer()
        DispatcherRetainer.registerRetainer()
        DispatcherRetainer.resetDispatchers()
        server.start()
    }

    override fun after() {
        server.shutdown()
        DispatcherRetainer.resetDispatchers()
        server.setDispatcher(DispatcherRetainer.queueDispatcher)
    }
}
