package me.jorgecastillo.hiroaki

import me.jorgecastillo.hiroaki.dispatcher.DispatcherAdapter
import me.jorgecastillo.hiroaki.matchers.anyOrder
import me.jorgecastillo.hiroaki.matchers.matches
import me.jorgecastillo.hiroaki.matchers.never
import me.jorgecastillo.hiroaki.matchers.times
import me.jorgecastillo.hiroaki.models.Body
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat

fun MockWebServer.verify(path: String): VerifiableRequest =
    VerifiableRequest(path)

fun once(): (requestMatcher: Matcher<RecordedRequest>) -> Matcher<List<RecordedRequest>> =
    times(1)

fun twice(): (requestMatcher: Matcher<RecordedRequest>) -> Matcher<List<RecordedRequest>> =
    times(2)

class VerifiableRequest(private val path: String) {

    fun called(
        times: ((requestMatcher: Matcher<RecordedRequest>) -> Matcher<List<RecordedRequest>>)? = null,
        order: ((requestMatcher: Matcher<RecordedRequest>) -> Matcher<List<RecordedRequest>>)? = null,
        queryParams: QueryParams? = null,
        jsonBody: Body? = null,
        headers: Headers? = null,
        method: Method? = null
    ) {

        val safeTimes = times ?: once()
        val safeOrder = order ?: anyOrder()

        val requestMatcher = matches(sentToPath = path,
                queryParams = queryParams,
                jsonBody = jsonBody,
                headers = headers,
                method = method)

        val dispatchedRequests = DispatcherAdapter.dispatchedRequests()
        assertThat(dispatchedRequests, safeTimes(requestMatcher))
        if (times != never()) {
            assertThat(dispatchedRequests, safeOrder(requestMatcher))
        }
    }
}
