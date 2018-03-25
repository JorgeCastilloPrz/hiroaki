package me.jorgecastillo.hiroaki.matchers

import me.jorgecastillo.hiroaki.Method
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Custom Hamcrest matcher to assert about HTTP method (GET, POST, PUT, DELETE...) on an OkHttp
 * RecordedRequest.
 */
fun hasMethod(expectedMethod: Method): Matcher<RecordedRequest> {
    return object : TypeSafeMatcher<RecordedRequest>() {

        override fun describeTo(description: Description) {
            description.appendText("The HTTP query should have method: $expectedMethod")
        }

        override fun describeMismatchSafely(request: RecordedRequest, mismatchDescription: Description) {
            mismatchDescription.appendText("\nMethod ${request.method} was found instead.")
        }

        override fun matchesSafely(request: RecordedRequest): Boolean =
                request.method == expectedMethod.toString()
    }
}
