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
import java.io.IOException

class HiroakiServer {

    companion object {
        private const val SUCCESS_RESPONSE_CODE = 200
        private const val UNAUTHORIZED_RESPONSE_CODE = 401
    }

    private lateinit var converterFactory: Converter.Factory
    private lateinit var server: MockWebServer

    private fun okHttpClient(
        loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
    ): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(loggingLevel))
                    .build()

    fun <T> retrofitService(
        serviceClass: Class<T>,
        converterFactory: Converter.Factory
    ): T {
        this.converterFactory = converterFactory
        this.server = MockWebServer()
        return Retrofit.Builder().baseUrl(server.url("/").toString())
                .client(okHttpClient())
                .addConverterFactory(converterFactory).build()
                .create(serviceClass)
    }

    @Throws(IOException::class)
    fun shutdown() {
        server.shutdown()
    }

    fun enqueueSuccessResponse() {
        val response = MockResponse()
        response.setResponseCode(SUCCESS_RESPONSE_CODE)
        response.setBody("")
        server.enqueue(response)
    }

    fun enqueueErrorResponse() {
        val response = MockResponse()
        response.setResponseCode(UNAUTHORIZED_RESPONSE_CODE)
        server.enqueue(response)
    }

    fun enqueueSuccessResponse(jsonFileName: String) {
        val body = fileContentAsString(jsonFileName)
        val response = MockResponse()
        response.setResponseCode(SUCCESS_RESPONSE_CODE)
        response.setBody(body)
        server.enqueue(response)
    }

    fun enqueueErrorResponse(statusCode: Int) {
        val response = MockResponse()
        response.setResponseCode(statusCode)
        server.enqueue(response)
    }

    fun enqueueErrorResponse(statusCode: Int, reason: String) {
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
        server.enqueue(response)
    }

    fun assertRequest(
        sentToPath: String,
        queryParams: Map<String, String>? = null,
        jsonBodyResFile: Pair<String, Class<*>>? = null,
        jsonBody: Pair<String, Class<*>>? = null,
        headers: Map<String, String>? = null
    ) {
        throwIfBothBodyParamsArePassed(jsonBodyResFile, jsonBody)

        val request = server.takeRequest()
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
}
