package me.jorgecastillo.hiroaki.internal

import androidx.test.platform.app.InstrumentationRegistry
import me.jorgecastillo.hiroaki.dispatcher.AndroidDispatcherRetainer
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.rules.ExternalResource

/**
 * JUnit4 Rule to provide the mock server before and after the test execution. This is the Android version which also
 * sets the android Context up into the library so it can easily reach asset resources for json body files.
 */
class AndroidMockServerRule : ExternalResource() {

  lateinit var server: MockWebServer

  @Before
  override fun before() {
    server = MockWebServer()
    AndroidDispatcherRetainer.androidContext = InstrumentationRegistry.getInstrumentation().context
    AndroidDispatcherRetainer.registerRetainer()
    AndroidDispatcherRetainer.resetDispatchers()
    server.start()
  }

  override fun after() {
    server.shutdown()
    AndroidDispatcherRetainer.resetDispatchers()
    server.setDispatcher(AndroidDispatcherRetainer.queueDispatcher)
  }
}
