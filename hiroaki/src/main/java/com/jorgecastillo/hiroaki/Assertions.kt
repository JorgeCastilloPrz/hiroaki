package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.matchers.hasBody
import com.jorgecastillo.hiroaki.matchers.hasHeaders
import com.jorgecastillo.hiroaki.matchers.hasMethod
import com.jorgecastillo.hiroaki.matchers.hasQueryParams
import com.jorgecastillo.hiroaki.matchers.isSentToPath
import com.jorgecastillo.hiroaki.models.JsonBody
import com.jorgecastillo.hiroaki.models.JsonBodyFile
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

fun MockWebServer.assertRequest(
    sentToPath: String,
    queryParams: QueryParams? = null,
    jsonBodyResFile: JsonBodyFile? = null,
    jsonBody: JsonBody? = null,
    headers: Headers? = null,
    method: Method? = null
) {
    throwIfBothBodyParamsArePassed(jsonBodyResFile, jsonBody)

    val request = this.takeRequest()
    assertThat(request, isSentToPath("/$sentToPath"))

    queryParams?.let {
        assertThat(request, hasQueryParams(it))
    }

    jsonBodyResFile?.let {
        val fileStringBody = fileContentAsString(it.jsonBodyResFile)
        assertThat(
                request, hasBody(
                fileStringBody,
                fileStringBody.fromJson(it.type),
                request.parse(it.type)
        )
        )
    }

    jsonBody?.let {
        assertThat(
                request, hasBody(
                it.jsonBody,
                it.jsonBody.fromJson(it.type),
                request.parse(it.type)
        )
        )
    }

    headers?.let {
        assertThat(request, hasHeaders(it))
    }

    method?.let {
        assertThat(request, hasMethod(method))
    }
}

fun throwIfBothBodyParamsArePassed(
    jsonBodyResFile: JsonBodyFile? = null,
    jsonBody: JsonBody? = null
) {
    if (jsonBodyResFile != null && jsonBody != null) {
        throw IllegalArgumentException("Please pass jsonBodyFile name or jsonBody, but not both.")
    }
}

infix fun <T> T.eq(other: T) {
    assertThat(this, `is`(other))
}
