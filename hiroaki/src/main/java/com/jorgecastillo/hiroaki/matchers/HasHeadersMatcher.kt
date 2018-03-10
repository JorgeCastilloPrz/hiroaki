package com.jorgecastillo.hiroaki.matchers

import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.net.URLEncoder

/**
 * Custom Hamcrest matcher to assert about HTTP headers on an OkHttp RecordedRequest.
 */
fun hasHeaders(headers: List<Pair<String, String>>): Matcher<RecordedRequest> {
    return object : TypeSafeMatcher<RecordedRequest>() {

        override fun describeTo(description: Description) {
            description.appendText("The HTTP query should contain headers: ")
            headers.forEach {
                description.appendText("\n${it.first} = ${it.second}")
            }
        }

        override fun describeMismatchSafely(request: RecordedRequest, mismatchDescription: Description) {
            for ((key, value) in headers) {
                val header = request.getHeader(key)
                if (header == null) {
                    mismatchDescription.appendText("\nheader $key is not present.")
                } else {
                    if (header != URLEncoder.encode(value, "UTF-8")) {
                        mismatchDescription.appendText("\n$key = $header (Not matching!)")
                    }
                }
            }
        }

        override fun matchesSafely(request: RecordedRequest): Boolean {
            var failed = false
            for ((key, value) in headers) {
                val header = request.getHeader(key)
                if (header == null) {
                    failed = true
                } else {
                    if (header != URLEncoder.encode(value, "UTF-8")) {
                        failed = true
                    }
                }
            }
            return !failed
        }
    }
}
