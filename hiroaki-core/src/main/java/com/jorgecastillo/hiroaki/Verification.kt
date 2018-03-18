package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import com.jorgecastillo.hiroaki.matchers.anyOrder
import com.jorgecastillo.hiroaki.matchers.matches
import com.jorgecastillo.hiroaki.matchers.times
import com.jorgecastillo.hiroaki.models.JsonBody
import com.jorgecastillo.hiroaki.models.JsonBodyFile
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat

fun verify(path: String): VerifiableRequest =
        VerifiableRequest(path)

fun once(): (requestMatcher: Matcher<RecordedRequest>) -> Matcher<List<RecordedRequest>> = times(1)

fun twice(): (requestMatcher: Matcher<RecordedRequest>) -> Matcher<List<RecordedRequest>> = times(2)

class VerifiableRequest(private val path: String) {

    fun called(
        times: ((requestMatcher: Matcher<RecordedRequest>) -> Matcher<List<RecordedRequest>>)? = null,
        order: ((requestMatcher: Matcher<RecordedRequest>) -> Matcher<List<RecordedRequest>>)? = null,
        queryParams: QueryParams? = null,
        jsonBodyResFile: JsonBodyFile? = null,
        jsonBody: JsonBody? = null,
        headers: Headers? = null,
        method: Method? = null
    ) {

        val safeTimes = times ?: once()
        val safeOrder = order ?: anyOrder()

        val requestMatcher = matches(sentToPath = path,
                queryParams = queryParams,
                jsonBodyResFile = jsonBodyResFile,
                jsonBody = jsonBody,
                headers = headers,
                method = method)

        val dispatchedRequests = DispatcherRetainer.dispatchedRequests()
        assertThat(dispatchedRequests, safeTimes(requestMatcher))
        assertThat(dispatchedRequests, safeOrder(requestMatcher))
    }
}
