package me.jorgecastillo.hiroaki.internal

import androidx.test.platform.app.InstrumentationRegistry
import me.jorgecastillo.hiroaki.dispatcher.AndroidDispatcherRetainer
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
    AndroidDispatcherRetainer.androidContext = InstrumentationRegistry.getInstrumentation().context
    AndroidDispatcherRetainer.registerRetainer()
    AndroidDispatcherRetainer.resetDispatchers()
    server.start()
  }

  @After
  open fun tearDown() {
    server.shutdown()
    AndroidDispatcherRetainer.resetDispatchers()
    server.setDispatcher(AndroidDispatcherRetainer.queueDispatcher)
  }
}
