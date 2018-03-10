package com.jorgecastillo.hiroaki.matchers

import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.net.URLEncoder

/**
 * Custom Hamcrest matcher to assert about query parameters on an OkHttp RecordedRequest.
 */
fun hasQueryParams(params: List<Pair<String, String>>): Matcher<RecordedRequest> {
    return object : TypeSafeMatcher<RecordedRequest>() {

        override fun describeTo(description: Description) {
            description.appendText("The HTTP query should contain params: ")
            params.forEach {
                description.appendText("\n${it.first} = ${it.second}")
            }
        }

        override fun describeMismatchSafely(request: RecordedRequest, mismatchDescription: Description) {
            for ((key, value) in params) {
                val parameter = request.requestUrl.queryParameter(key)
                if (parameter == null) {
                    mismatchDescription.appendText("\nparameter $key is not present.")
                } else {
                    if (parameter != URLEncoder.encode(value, "UTF-8")) {
                        mismatchDescription.appendText("\n$key = $parameter (Not matching!)")
                    }
                }
            }
        }

        override fun matchesSafely(request: RecordedRequest): Boolean {
            var failed = false
            for ((key, value) in params) {
                val parameter = request.requestUrl.queryParameter(key)
                if (parameter == null) {
                    failed = true
                } else {
                    if (parameter != URLEncoder.encode(value, "UTF-8")) {
                        failed = true
                    }
                }
            }
            return !failed
        }
    }
}
