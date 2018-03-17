package com.jorgecastillo.hiroaki.matchers

import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun anyOrder(): (requestMatcher: Matcher<RecordedRequest>) -> Matcher<List<RecordedRequest>> {
    return { requestMatcher ->
        object : TypeSafeMatcher<List<RecordedRequest>>() {

            override fun describeTo(description: Description) {
                description.appendText("Expected request: asdf")
            }

            override fun describeMismatchSafely(
                    dispatchedRequests: List<RecordedRequest>,
                    mismatchDescription: Description
            ) {
                mismatchDescription.appendText("But request has not been done.")
            }

            override fun matchesSafely(dispatchedRequests: List<RecordedRequest>): Boolean {
                return dispatchedRequests.any { requestMatcher.matches(it) }
            }
        }
    }
}
