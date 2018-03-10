package com.jorgecastillo.hiroaki.matchers

import com.jorgecastillo.hiroaki.bodyAsMap
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Custom Hamcrest matcher to assert about the outgoing body of an OkHttp RecordedRequest.
 */
fun hasBody(body: Map<String, String>): Matcher<RecordedRequest> {
    var requestBodySnapshot: Map<String, String> = mapOf()

    return object : TypeSafeMatcher<RecordedRequest>() {

        override fun describeTo(description: Description) {
            description.appendText("The HTTP query should have the following body: ")
            description.appendText("\n$body")
        }

        override fun describeMismatchSafely(request: RecordedRequest,
                                            mismatchDescription: Description) {
            requestBodySnapshot.keys.forEach { key ->
                if (body[key] == null) {
                    mismatchDescription.appendText("\n The key $key is not present.")
                } else {
                    if (requestBodySnapshot[key] != body[key]) {
                        mismatchDescription.appendText(
                                "\n$key = ${requestBodySnapshot[key]} Not matching!")
                    }
                }
            }
        }

        override fun matchesSafely(request: RecordedRequest): Boolean {
            requestBodySnapshot = request.bodyAsMap()
            return requestBodySnapshot.keys.fold(true) { acc, key ->
                acc && if (body[key] == null) {
                    false
                } else {
                    requestBodySnapshot[key] == body[key]
                }
            }
        }
    }
}
