package me.jorgecastillo.hiroaki.matchers

import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Hamcrest matcher to match against request ordering.
 */
fun order(vararg orders: Int): (requestMatcher: Matcher<RecordedRequest>) -> Matcher<List<RecordedRequest>> {
    if (orders.any { it <= 0 }) throw IllegalArgumentException("Request ordering starts at 1!")
    return { requestMatcher ->
        object : TypeSafeMatcher<List<RecordedRequest>>() {

            override fun describeTo(description: Description) {
                description.appendText("Expected request/s in order/s: ${orders.joinToString { "$it" }}")
            }

            override fun describeMismatchSafely(
                dispatchedRequests: List<RecordedRequest>,
                mismatchDescription: Description
            ) {
                orders.forEach { order ->
                    if (dispatchedRequests.size < order) {
                        mismatchDescription.appendText(
                                "\nexpected order $order is higher than the number of requests done " +
                                        "(${dispatchedRequests.size}).")
                    } else if (!requestMatcher.matches(dispatchedRequests[order - 1])) {
                        mismatchDescription.appendText(
                                "\nRequest ${dispatchedRequests[order]} not dispatched.")
                    }
                }
            }

            override fun matchesSafely(dispatchedRequests: List<RecordedRequest>): Boolean {
                return orders.fold(true) { _, order ->
                    dispatchedRequests.size >= order &&
                            requestMatcher.matches(dispatchedRequests[order - 1])
                }
            }
        }
    }
}
