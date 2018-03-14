package com.jorgecastillo.hiroaki.models

import com.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matcher

/**
 * Models a bunch of chained Requests that still require to be validated by the dispatcher once
 * they're fired by the production code.
 */
class PotentialRequestChain(private val matcher: Matcher<RecordedRequest>) {

    fun thenRespond(mockResponse: MockResponse): PotentialRequestChain {
        DispatcherRetainer.dispatcher.addMockRequest(matcher, mockResponse)
        return this
    }
}
