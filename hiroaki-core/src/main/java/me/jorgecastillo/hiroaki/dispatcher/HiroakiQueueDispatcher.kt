package me.jorgecastillo.hiroaki.dispatcher

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest

/**
 * This is a basic QueueDispatcher to enqueue sequential calls on MockWebServer, but also retaining
 * some info about dispatched calls for further verify() calls.
 */
class HiroakiQueueDispatcher : QueueDispatcher() {

    val dispatchedRequests: MutableList<RecordedRequest> = mutableListOf()

    /**
     * Resets both collections to their initial state. Called after any test.
     */
    fun reset() {
        dispatchedRequests.clear()
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        dispatchedRequests.add(request)
        return super.dispatch(request)
    }
}
