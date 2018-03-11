package com.jorgecastillo.hiroaki.matchers

import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Custom Hamcrest matcher to assert about the outgoing body of an OkHttp RecordedRequest.
 */
fun <T> hasBody(stringBody: String, parsedExpectedBody: T, parsedRequestBody: Pair<T, String>):
        Matcher<RecordedRequest> = object : TypeSafeMatcher<RecordedRequest>() {

    override fun describeTo(description: Description) {
        description.appendText("The HTTP query should have the following body: ")
        description.appendText("\n$stringBody")
    }

    override fun describeMismatchSafely(
        request: RecordedRequest,
        mismatchDescription: Description
    ) {
        mismatchDescription.appendText("\n${parsedRequestBody.second}")
    }

    override fun matchesSafely(request: RecordedRequest): Boolean {
        return parsedRequestBody.first == parsedExpectedBody
    }
}
