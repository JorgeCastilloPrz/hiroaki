package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.matchers.hasBody
import com.jorgecastillo.hiroaki.matchers.hasHeaders
import com.jorgecastillo.hiroaki.matchers.hasQueryParams
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import retrofit2.Converter
import retrofit2.Retrofit

private const val SUCCESS_RESPONSE_CODE = 200
private const val UNAUTHORIZED_RESPONSE_CODE = 401

private fun okHttpClient(
    loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
): OkHttpClient =
        OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(loggingLevel))
                .build()

fun <T> MockWebServer.retrofitService(
    serviceClass: Class<T>,
    converterFactory: Converter.Factory
): T {
    return Retrofit.Builder().baseUrl(this.url("/").toString())
            .client(okHttpClient())
            .addConverterFactory(converterFactory).build()
            .create(serviceClass)
}

fun MockWebServer.enqueueSuccessResponse() {
    val response = MockResponse()
    response.setResponseCode(SUCCESS_RESPONSE_CODE)
    response.setBody("")
    this.enqueue(response)
}

fun MockWebServer.enqueueErrorResponse() {
    val response = MockResponse()
    response.setResponseCode(UNAUTHORIZED_RESPONSE_CODE)
    this.enqueue(response)
}

fun MockWebServer.enqueueSuccessResponse(jsonFileName: String) {
    val body = fileContentAsString(jsonFileName)
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

fun MockWebServer.assertRequest(
    sentToPath: String,
    queryParams: Map<String, String>? = null,
    jsonBodyResFile: Pair<String, Class<*>>? = null,
    jsonBody: Pair<String, Class<*>>? = null,
    headers: Map<String, String>? = null
) {
    throwIfBothBodyParamsArePassed(jsonBodyResFile, jsonBody)

    val request = this.takeRequest()
    MatcherAssert.assertThat(request.path, CoreMatchers.startsWith("/$sentToPath"))

    queryParams?.let {
        MatcherAssert.assertThat(request, hasQueryParams(it))
    }

    jsonBodyResFile?.let {
        val fileStringBody = fileContentAsString(it.first)
        MatcherAssert.assertThat(request, hasBody(
                fileStringBody,
                fileStringBody.fromJson(it.second),
                request.parse(it.second)))
    }

    jsonBody?.let {
        MatcherAssert.assertThat(request, hasBody(
                it.first,
                it.first.fromJson(it.second),
                request.parse(it.second)))
    }

    headers?.let {
        MatcherAssert.assertThat(request, hasHeaders(it))
    }
}

private fun throwIfBothBodyParamsArePassed(
    jsonBodyResFile: Pair<String, Class<*>>? = null,
    jsonBody: Pair<String, Class<*>>? = null
) {
    if (jsonBodyResFile != null && jsonBody != null) {
        throw IllegalArgumentException("Please pass jsonBodyFile name or jsonBody, but not both.")
    }
}
