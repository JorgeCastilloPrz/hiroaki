package com.jorgecastillo.hiroaki.matchers

import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Hamcrest matcher to match against the number of times a request is made.
 */
fun times(count: Int): (requestMatcher: Matcher<RecordedRequest>) -> Matcher<List<RecordedRequest>> {
    if (count <= 0) throw IllegalArgumentException("Times count must be >= 1!")
    return { requestMatcher ->
        object : TypeSafeMatcher<List<RecordedRequest>>() {

            override fun describeTo(description: Description) {
                description.appendText("Expected: $count times")
            }

            override fun describeMismatchSafely(
                    dispatchedRequests: List<RecordedRequest>,
                    mismatchDescription: Description
            ) {
                val timesFound = dispatchedRequests
                        .fold(0, { acc, recordedRequest ->
                            if (requestMatcher.matches(recordedRequest)) acc + 1 else acc
                        })
                mismatchDescription.appendText("\nBut request found it $timesFound times instead.")
            }

            override fun matchesSafely(dispatchedRequests: List<RecordedRequest>): Boolean {
                val timesFound = dispatchedRequests
                        .fold(0, { acc, recordedRequest ->
                            if (requestMatcher.matches(recordedRequest)) acc + 1 else acc
                        })
                return timesFound == count
            }
        }
    }
}
