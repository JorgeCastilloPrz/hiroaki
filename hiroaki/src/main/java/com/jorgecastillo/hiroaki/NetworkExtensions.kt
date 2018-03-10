package com.jorgecastillo.hiroaki

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.jorgecastillo.hiroaki.matchers.hasBody
import com.jorgecastillo.hiroaki.matchers.hasHeaders
import com.jorgecastillo.hiroaki.matchers.hasQueryParams
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import retrofit2.Converter
import retrofit2.Retrofit


private const val SUCCESS_RESPONSE_CODE = 200
private const val UNAUTHORIZED_RESPONSE_CODE = 401

fun okHttpClient(
        loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
): OkHttpClient =
        OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(loggingLevel))
                .build()

fun <T> MockWebServer.retrofitService(serviceClass: Class<T>,
                                      converterFactory: Converter.Factory): T {
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
        jsonBodyFileName: String? = null,
        headers: Map<String, String>? = null
) {
    val request = this.takeRequest()
    assertThat(request.path, startsWith("/$sentToPath"))

    queryParams?.let {
        assertThat(request, hasQueryParams(it))
    }

    jsonBodyFileName?.let {
        assertThat(request, hasBody(fileContentAsMap(it)))
    }

    headers?.let {
        assertThat(request, hasHeaders(it))
    }
}

@Throws(JsonParseException::class)
fun <T> RecordedRequest.bodyAsMap(): Map<String, T> {
    val mapType = object : TypeToken<Map<String, T>>() {}.type
    return GsonBuilder().create().fromJson(this.body.readUtf8(), mapType)
}
