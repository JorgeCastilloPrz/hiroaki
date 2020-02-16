package me.jorgecastillo.hiroaki.dispatcher

import me.jorgecastillo.hiroaki.Either
import me.jorgecastillo.hiroaki.left
import me.jorgecastillo.hiroaki.right
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matcher

private typealias DispatchableBlock = (recordedRequest: RecordedRequest) -> MockResponse

/**
 * This is the MockWebServer dispatcher auto-attached to the server at start. It's required to mock
 * responses that are dependant on the request, instead of plain enqueuing (which just adds them to
 * a sequential queue).
 *
 * Thanks to this Dispatcher, we can program different answers depending on which endpoint is
 * queried and regardless of the execution order. That's very handy for Android end to end UI tests
 * where you don't care when are requests made but just that they return your mocks when they are
 * done.
 *
 * It's open so end users can extend it to create their own dispatchers if they need to.
 */
object HiroakiDispatcher : Dispatcher() {

    private val mockRequestsForever =
        mutableListOf<Pair<Matcher<RecordedRequest>, Either<MockResponse, DispatchableBlock>>>()
    private val mockRequests =
        mutableListOf<Pair<Matcher<RecordedRequest>, Either<MockResponse, DispatchableBlock>>>()
    val dispatchedRequests = mutableListOf<RecordedRequest>()

    fun addMockRequest(
        matcher: Matcher<RecordedRequest>,
        mockResponse: MockResponse
    ) {
        mockRequests.add(Pair(matcher, mockResponse.left()))
    }

    fun addMockRequestForever(
        matcher: Matcher<RecordedRequest>,
        mockResponse: MockResponse
    ) {
        mockRequestsForever.add(Pair(matcher, mockResponse.left()))
    }

    fun addDispatchableBlock(
        matcher: Matcher<RecordedRequest>,
        dispatchableBlock: (recordedRequest: RecordedRequest) -> MockResponse
    ) {
        mockRequests.add(Pair(matcher, dispatchableBlock.right()))
    }

    fun addDispatchableBlockForever(
        matcher: Matcher<RecordedRequest>,
        dispatchableBlock: (recordedRequest: RecordedRequest) -> MockResponse
    ) {
        mockRequestsForever.add(Pair(matcher, dispatchableBlock.right()))
    }

    /**
     * Resets both collections to their initial state. Called after any test.
     */
    fun reset() {
        mockRequestsForever.clear()
        mockRequests.clear()
        dispatchedRequests.clear()
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        dispatchedRequests.add(request)
        val predicate: (Pair<Matcher<RecordedRequest>, Any>) -> Boolean =
            { (matcher, _) -> matcher.matches(request) }

        val mockRequest = mockRequests.pop(predicate)
            ?: mockRequestsForever.findLast(predicate)

        return mockRequest?.second?.fold({ it }, { it(request) }) ?: notMockedResponse()
    }

    private fun <T> MutableList<T>.pop(predicate: (T) -> Boolean): T? {
        return find(predicate).also { if (it != null) remove(it) }
    }

    private fun notMockedResponse() = MockResponse().apply {
        setResponseCode(500)
        setBody("{ \"error\" : \"not mocked response\" }")
    }
}
