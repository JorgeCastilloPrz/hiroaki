package com.jorgecastillo.hiroaki.dispatcher

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matcher

/**
 * This is the MockWebServer dispatcher auto-attached to the server at start. It's required to mock
 * responses that are dependant on the request, instead of plain enqueuing (which just adds them to
 * a sequential queue).
 *
 * Thanks to this Dispatcher, we can program different answers depending on which endpoint is
 * queried and regardless of the execution order. That's very handy for Android end to end UI tests
 * where you don't care when are requests made but just that they return your mocks when they are
 * done.
 */
class HiroakiDispatcher : Dispatcher() {

    private val mockRequests: MutableList<Pair<Matcher<RecordedRequest>, MockResponse>> =
            mutableListOf()

    fun addMockRequest(matcher: Matcher<RecordedRequest>, mockResponse: MockResponse) {
        mockRequests.add(Pair(matcher, mockResponse))
    }

    fun reset(): Unit {
        mockRequests.clear()
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        val mockRequest = mockRequests.find { (matcher, _) -> matcher.matches(request) }
        return mockRequest?.second ?: notMockedResponse()
    }

    private fun notMockedResponse(): MockResponse {
        val mockResponse = MockResponse().setResponseCode(500)
        mockResponse.setBody("{ \"error\" : \"not mocked response\" }")
        return mockResponse
    }
}
