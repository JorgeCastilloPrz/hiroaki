package me.jorgecastillo.hiroaki.matchers

import com.google.gson.internal.LinkedTreeMap
import me.jorgecastillo.hiroaki.json.parse
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType.Object

/**
 * Custom Hamcrest matcher to assert about the outgoing body of an OkHttp RecordedRequest.
 */
fun hasBody(
    stringBody: String,
    parsedExpectedBody: LinkedTreeMap<String, Object>
):
        Matcher<RecordedRequest> = object : TypeSafeMatcher<RecordedRequest>() {

    override fun describeTo(description: Description) {
        description.appendText("The HTTP query should have the following body: ")
        description.appendText("\n$stringBody")
    }

    override fun describeMismatchSafely(
        request: RecordedRequest,
        mismatchDescription: Description
    ) {
        mismatchDescription.appendText("\n${request.parse().second}")
    }

    override fun matchesSafely(request: RecordedRequest): Boolean {
        return request.parse().first == parsedExpectedBody
    }
}
