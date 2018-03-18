package com.jorgecastillo.hiroaki.matchers

import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Custom Hamcrest matcher to assert about PATH where a RecordedRequest is sent to.
 */
fun isSentToPath(expectedPath: String): Matcher<RecordedRequest> {
    return object : TypeSafeMatcher<RecordedRequest>() {

        override fun describeTo(description: Description) {
            description.appendText("The HTTP query should be sent to: $expectedPath")
        }

        override fun describeMismatchSafely(
            request: RecordedRequest,
            mismatchDescription: Description
        ) {
            mismatchDescription.appendText("\nRequest sent to ${request.method} instead.")
        }

        override fun matchesSafely(request: RecordedRequest): Boolean =
                request.path.startsWith(expectedPath)
    }
}
