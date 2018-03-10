package com.jorgecastillo.hiroaki

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URLEncoder

private const val SUCCESS_RESPONSE_CODE = 200
private const val UNAUTHORIZED_RESPONSE_CODE = 401

fun okHttpClient(loggingLevel: HttpLoggingInterceptor.Level =
                         HttpLoggingInterceptor.Level.BODY): OkHttpClient =
        OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(loggingLevel))
                .build()

fun <T> MockWebServer.mockService(serviceClass: Class<T>): T {
    val server = MockWebServer()
    return Retrofit.Builder().baseUrl(server.url("/").toString())
            .client(okHttpClient())
            .addConverterFactory(MoshiConverterFactory.create()).build()
            .create(serviceClass)
}

fun MockWebServer.enqueueSuccessfulResponse() {
    val body = "api/EmptyResponse.json".fileContentAsString()
    val response = MockResponse()
    response.setResponseCode(SUCCESS_RESPONSE_CODE)
    response.setBody(body)
    this.enqueue(response)
}

fun MockWebServer.enqueueErrorResponse() {
    val response = MockResponse()
    response.setResponseCode(UNAUTHORIZED_RESPONSE_CODE)
    this.enqueue(response)
}

fun MockWebServer.enqueueSuccessfulResponse(filePath: String) {
    val body = filePath.fileContentAsString()
    val response = MockResponse()
    response.setResponseCode(SUCCESS_RESPONSE_CODE)
    response.setBody(body)
    this.enqueue(response)
}

fun MockWebServer.enqueueErrorResponse(statusCode: Int) {
    val response = MockResponse()
    response.setResponseCode(statusCode)
    this.enqueue(response)
}

fun MockWebServer.enqueueErrorResponse(
        statusCode: Int,
        reason: String
) {
    val response = MockResponse()
    response.setResponseCode(statusCode)
    response.setBody(
            """
          {
            "status": "failure",
            "data": {
              "reason": "$reason"
            }
          }
        """
    )
    this.enqueue(response)
}

fun MockWebServer.assertRequestSendToPath(path: String) {
    val request = this.takeRequest()
    assertEquals("/$path", request.path)
}

fun MockWebServer.assertRequestSendToPath(
        path: String,
        args: List<Pair<String, String>> = listOf()) {

    val pathWithArgs = StringBuilder(path)
    var first = true
    for ((key, value) in args) {
        if (first) {
            pathWithArgs.append("?")
            first = false
        } else {
            pathWithArgs.append("&")
        }
        pathWithArgs.append(key)
                .append("=")
                .append(URLEncoder.encode(value, "UTF-8"))
    }

    val request = this.takeRequest()
    assertEquals("/$pathWithArgs", request.path)
}
