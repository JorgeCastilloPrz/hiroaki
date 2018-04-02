package me.jorgecastillo.hiroaki.matchers

import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.net.URLEncoder

/**
 * Custom Hamcrest matcher to assert about query parameters on an OkHttp RecordedRequest.
 */
fun hasQueryParams(expectedParams: List<Pair<String, String>>): Matcher<RecordedRequest> {
    return object : TypeSafeMatcher<RecordedRequest>() {

        override fun describeTo(description: Description) {
            description.appendText("The HTTP query should contain params: ")
            expectedParams.forEach {
                description.appendText("\n${it.first} = ${it.second}")
            }
        }

        override fun describeMismatchSafely(request: RecordedRequest, mismatchDescription: Description) {
            for ((key, value) in expectedParams) {
                val requestedParamsForExpectedKey = request.requestUrl.queryParameterValues(key)
                if (requestedParamsForExpectedKey == null ||
                        requestedParamsForExpectedKey.isEmpty()) {
                    mismatchDescription.appendText("\nparameter $key is not present.")
                } else {
                    if (requestedParamsForExpectedKey
                                    .find { it == URLEncoder.encode(value, "UTF-8") } == null) {
                        mismatchDescription.appendText("\n$key = $value (Not matching!)")
                    }
                }
            }
        }

        override fun matchesSafely(request: RecordedRequest): Boolean {
            var failed = false
            for ((key, value) in expectedParams) {
                val requestedParamsForExpectedKey = request.requestUrl.queryParameterValues(key)
                if (requestedParamsForExpectedKey == null ||
                        requestedParamsForExpectedKey.isEmpty()) {
                    failed = true
                } else {
                    if (requestedParamsForExpectedKey
                                    .find { it == URLEncoder.encode(value, "UTF-8") } == null) {
                        failed = true
                    }
                }
            }
            return !failed
        }
    }
}
