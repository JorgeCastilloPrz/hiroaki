package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.matchers.hasHeaders
import com.jorgecastillo.hiroaki.matchers.hasQueryParams
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val SUCCESS_RESPONSE_CODE = 200
private const val UNAUTHORIZED_RESPONSE_CODE = 401

fun okHttpClient(
        loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY): OkHttpClient =
        OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(loggingLevel))
                .build()

fun <T> MockWebServer.mockService(serviceClass: Class<T>): T {
    return Retrofit.Builder().baseUrl(this.url("/").toString())
            .client(okHttpClient())
            .addConverterFactory(MoshiConverterFactory.create()).build()
            .create(serviceClass)
}

fun MockWebServer.enqueueSuccessfulResponse() {
    val body = fileContentAsString("api/EmptyResponse.json")
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
    val body = fileContentAsString(filePath)
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

fun MockWebServer.enqueueErrorResponse(statusCode: Int, reason: String) {
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

fun MockWebServer.assertRequest(sentToPath: String,
                                queryParams: List<Pair<String, String>> = listOf(),
                                headers: List<Pair<String, String>> = listOf()) {
    val request = this.takeRequest()
    assertThat(request.path, startsWith("/$sentToPath"))

    if (queryParams.isNotEmpty()) {
        assertThat(request, hasQueryParams(queryParams))
    }

    if (headers.isNotEmpty()) {
        assertThat(request, hasHeaders(headers))
    }
}

