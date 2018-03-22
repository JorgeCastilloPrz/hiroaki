package com.jorgecastillo.hiroaki.matchers

import com.jorgecastillo.hiroaki.Headers
import com.jorgecastillo.hiroaki.Method
import com.jorgecastillo.hiroaki.QueryParams
import com.jorgecastillo.hiroaki.json.fileContentAsString
import com.jorgecastillo.hiroaki.json.fromJson
import com.jorgecastillo.hiroaki.models.Body
import com.jorgecastillo.hiroaki.models.Body.JsonBody
import com.jorgecastillo.hiroaki.models.Body.JsonBodyFile
import com.jorgecastillo.hiroaki.models.Body.JsonDSL
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

/**
 * Creator function that provides highly configurable matchers for MockRequests.
 */
fun <T : Any> T.matches(
    sentToPath: String,
    queryParams: QueryParams? = null,
    jsonBody: Body? = null,
    headers: Headers? = null,
    method: Method? = null
): Matcher<RecordedRequest> {
    val matchers = mutableListOf(isSentToPath("/$sentToPath"))

    queryParams?.let {
        matchers.add(hasQueryParams(queryParams))
    }
    jsonBody?.let { body ->
        when (body) {
            is JsonBody -> matchers.add(
                    hasBody(
                            body.jsonBody,
                            body.jsonBody.fromJson())
            )
            is JsonBodyFile -> {
                val fileStringBody = fileContentAsString(body.jsonBodyResFile)
                matchers.add(
                        hasBody(
                                fileStringBody,
                                fileStringBody.fromJson())
                )
            }
            is JsonDSL -> matchers.add(
                    hasBody(
                            body.toJsonString(),
                            body.toJsonString().fromJson())
            )
        }
    }
    headers?.let {
        matchers.add(hasHeaders(it))
    }
    method?.let {
        matchers.add(hasMethod(method))
    }

    return allOf(matchers)
}
