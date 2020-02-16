package me.jorgecastillo.hiroaki.models

import me.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matcher

/**
 * Models a bunch of chained Requests that still require to be validated by the dispatcher once
 * they're fired by the production code.
 */
class PotentialRequestChain(private val matcher: Matcher<RecordedRequest>) {

    /**
     * Enqueue a mocked response for the conditions given on the "whenever" statement.
     */
    fun thenRespond(mockResponse: MockResponse): PotentialRequestChain {
        DispatcherRetainer.hiroakiDispatcher.addMockRequest(matcher, mockResponse)
        return this
    }

    /**
     * Setup a mocked response that will be used forever until reset for the conditions given on the
     * "whenever" statement.
     */
    fun thenRespondForever(mockResponse: MockResponse): PotentialRequestChain {
        DispatcherRetainer.hiroakiDispatcher.addMockRequestForever(matcher, mockResponse)
        return this
    }

    /**
     * Enqueue a dispatched response for the conditions given on the "whenever" statement. A
     * dispatched response is just a function that will receive the request and return the required
     * mocked response. This allows the user to configure returned responses depending on the
     * request.
     */
    fun thenDispatch(dispatchableBlock: (recordedRequest: RecordedRequest) -> MockResponse): PotentialRequestChain {
        DispatcherRetainer.hiroakiDispatcher.addDispatchableBlock(matcher, dispatchableBlock)
        return this
    }

    /**
     * Setup a dispatched response that will be used forever until reset for the conditions given on
     * the "whenever" statement. A dispatched response is just a function that will receive the
     * request and return the required mocked response. This allows the user to configure returned
     * responses depending on the request.
     */
    fun thenDispatchForever(dispatchableBlock: (recordedRequest: RecordedRequest) -> MockResponse): PotentialRequestChain {
        DispatcherRetainer.hiroakiDispatcher.addDispatchableBlockForever(matcher, dispatchableBlock)
        return this
    }
}
