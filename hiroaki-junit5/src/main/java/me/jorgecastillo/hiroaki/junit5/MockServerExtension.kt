package me.jorgecastillo.hiroaki.junit5

import me.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.net.InetAddress

class MockServerExtension @JvmOverloads constructor(
    val inetAddress: InetAddress = InetAddress.getByName("localhost"),
    val port: Int = 0
) : BeforeEachCallback, AfterEachCallback {

    lateinit var server: MockWebServer

    override fun beforeEach(context: ExtensionContext) {
        server = MockWebServer()
        DispatcherRetainer.registerRetainer()
        DispatcherRetainer.resetDispatchers()
        server.start(inetAddress, port)
    }

    override fun afterEach(context: ExtensionContext) {
        server.shutdown()
        DispatcherRetainer.resetDispatchers()
        server.setDispatcher(DispatcherRetainer.queueDispatcher)
    }
}
