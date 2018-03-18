package com.jorgecastillo.hiroaki.matchers

import com.jorgecastillo.hiroaki.Headers
import com.jorgecastillo.hiroaki.Method
import com.jorgecastillo.hiroaki.QueryParams
import com.jorgecastillo.hiroaki.fileContentAsString
import com.jorgecastillo.hiroaki.fromJson
import com.jorgecastillo.hiroaki.models.JsonBody
import com.jorgecastillo.hiroaki.models.JsonBodyFile
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

/**
 * Creator function that provides highly configurable matchers for MockRequests.
 */
fun <T : Any> T.matches(
    sentToPath: String,
    queryParams: QueryParams? = null,
    jsonBodyResFile: JsonBodyFile? = null,
    jsonBody: JsonBody? = null,
    headers: Headers? = null,
    method: Method? = null
): Matcher<RecordedRequest> {

    throwIfBothBodyParamsArePassed(jsonBodyResFile, jsonBody)

    val matchers = mutableListOf(isSentToPath("/$sentToPath"))

    queryParams?.let {
        matchers.add(hasQueryParams(queryParams))
    }
    jsonBodyResFile?.let {
        val fileStringBody = fileContentAsString(it.jsonBodyResFile)
        matchers.add(
                hasBody(
                        fileStringBody,
                        fileStringBody.fromJson())
        )
    }
    jsonBody?.let {
        matchers.add(
                hasBody(
                        it.jsonBody,
                        it.jsonBody.fromJson())
        )
    }
    headers?.let {
        matchers.add(hasHeaders(it))
    }
    method?.let {
        matchers.add(hasMethod(method))
    }

    return allOf(matchers)
}

fun throwIfBothBodyParamsArePassed(
    jsonBodyResFile: JsonBodyFile? = null,
    jsonBody: JsonBody? = null
) {
    if (jsonBodyResFile != null && jsonBody != null) {
        throw IllegalArgumentException("Please pass jsonBodyFile name or jsonBody, but not both.")
    }
}
