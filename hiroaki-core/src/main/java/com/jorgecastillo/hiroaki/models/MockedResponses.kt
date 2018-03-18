package com.jorgecastillo.hiroaki.models

import com.jorgecastillo.hiroaki.Headers
import com.jorgecastillo.hiroaki.fileContentAsString
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.util.concurrent.TimeUnit

private const val SUCCESS_RESPONSE_CODE = 200
private const val UNAUTHORIZED_RESPONSE_CODE = 401

/**
 * Utility function to mock success responses easily.
 */
fun success(
    code: Int = SUCCESS_RESPONSE_CODE,
    jsonFileName: String? = null,
    jsonBody: String? = null,
    headers: Headers? = null
): MockResponse =
        response(code, jsonFileName, jsonBody, headers)

/**
 * Utility function to mock success responses easily.
 */
fun error(
    code: Int = UNAUTHORIZED_RESPONSE_CODE,
    jsonFileName: String? = null,
    jsonBody: String? = null,
    headers: Headers? = null
): MockResponse =
        response(code, jsonFileName, jsonBody, headers)

/**
 * Utility function to mock responses easily.
 */
fun response(
    code: Int = UNAUTHORIZED_RESPONSE_CODE,
    jsonFileName: String? = null,
    jsonBody: String? = null,
    headers: Headers? = null
): MockResponse {
    throwIfBothBodyParamsArePassed(jsonFileName, jsonBody)

    return MockResponse().apply {
        setResponseCode(code)
        when {
            jsonFileName != null -> setBody(fileContentAsString(jsonFileName))
            jsonBody != null -> setBody(jsonBody)
            else -> setBody("")
        }
        headers?.forEach { header -> addHeader(header.key, header.value) }
    }
}

fun throwIfBothBodyParamsArePassed(jsonBodyResFile: String? = null, jsonBody: String? = null) {
    if (jsonBodyResFile != null && jsonBody != null) {
        throw IllegalArgumentException("Please pass jsonBodyFile name or jsonBody, but not both.")
    }
}

fun MockWebServer.enqueueSuccess(
    code: Int = SUCCESS_RESPONSE_CODE,
    jsonFileName: String? = null,
    jsonBody: String? = null,
    headers: Headers? = null
) {
    this.enqueue(success(code, jsonFileName, jsonBody, headers))
}

fun MockWebServer.enqueueError(
    code: Int = SUCCESS_RESPONSE_CODE,
    jsonFileName: String? = null,
    jsonBody: String? = null,
    headers: Headers? = null
) {
    this.enqueue(error(code, jsonFileName, jsonBody, headers))
}

fun MockResponse.delay(millis: Long): MockResponse = this.apply {
    setBodyDelay(millis, TimeUnit.MILLISECONDS)
}
