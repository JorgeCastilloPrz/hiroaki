package com.jorgecastillo.hiroaki.models

import com.jorgecastillo.hiroaki.Headers
import com.jorgecastillo.hiroaki.json.fileContentAsString
import com.jorgecastillo.hiroaki.models.Body.JsonBody
import com.jorgecastillo.hiroaki.models.Body.JsonBodyFile
import com.jorgecastillo.hiroaki.models.Body.JsonDSL
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
    jsonBody: Body? = null,
    headers: Headers? = null
): MockResponse =
    response(code, jsonBody, headers)

/**
 * Utility function to mock success responses easily.
 */
fun error(
    code: Int = UNAUTHORIZED_RESPONSE_CODE,
    jsonBody: Body? = null,
    headers: Headers? = null
): MockResponse =
    response(code, jsonBody, headers)

/**
 * Utility function to mock responses easily.
 */
fun response(
    code: Int = UNAUTHORIZED_RESPONSE_CODE,
    jsonBody: Body? = null,
    headers: Headers? = null
): MockResponse = MockResponse().apply {
    setResponseCode(code)
    jsonBody?.let { body ->
        when (body) {
            is JsonBody -> setBody(body.jsonBody)
            is JsonBodyFile -> setBody(fileContentAsString(body.jsonBodyResFile))
            is JsonDSL -> setBody(body.toJsonString())
        }

    } ?: setBody("")
    headers?.forEach { header -> addHeader(header.key, header.value) }
}

fun MockWebServer.enqueueSuccess(
    code: Int = SUCCESS_RESPONSE_CODE,
    jsonBody: Body? = null,
    headers: Headers? = null
) {
    this.enqueue(success(code, jsonBody, headers))
}

fun MockWebServer.enqueueError(
    code: Int = SUCCESS_RESPONSE_CODE,
    jsonBody: Body? = null,
    headers: Headers? = null
) {
    this.enqueue(error(code, jsonBody, headers))
}

fun MockResponse.delay(millis: Long): MockResponse = this.apply {
    setBodyDelay(millis, TimeUnit.MILLISECONDS)
}

/**
 * Throttles the request reader and response writer to sleep for the given periodToSleepMillis after each
 * series of {@code bytesPerPeriod} bytes are transferred. Use this to simulate network behavior.
 *
 * @param bytesPerPeriod how many bytes are sent before waiting for periodToSleepMillis
 * @periodToSleepMillis how long the server sleeps after the previous bytesPerPeriod chunk.
 */
fun MockResponse.throttle(
    bytesPerPeriod: Long,
    periodToSleepMillis: Long
): MockResponse = this.apply {
    throttleBody(bytesPerPeriod, periodToSleepMillis, TimeUnit.MILLISECONDS)
}
