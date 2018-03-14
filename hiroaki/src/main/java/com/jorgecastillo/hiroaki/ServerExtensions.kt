package com.jorgecastillo.hiroaki

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Converter
import retrofit2.Retrofit

enum class Method {
    DELETE, GET, HEAD, POST, OPTIONS, PATCH, PUT, CONNECT, TRACE
}

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
    converterFactory: Converter.Factory,
    okHttpClient: OkHttpClient = okHttpClient()
): T {
    return Retrofit.Builder().baseUrl(this.url("/").toString())
            .client(okHttpClient)
            .addConverterFactory(converterFactory).build()
            .create(serviceClass)
}

fun MockWebServer.whenever() {

}

fun MockWebServer.enqueueSuccessResponse() {
    val response = MockResponse()
    response.setResponseCode(SUCCESS_RESPONSE_CODE)
    response.setBody("")
    this.enqueue(response)
}

fun MockWebServer.enqueueSuccessResponse(jsonFileName: String) {
    val body = fileContentAsString(jsonFileName)
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
