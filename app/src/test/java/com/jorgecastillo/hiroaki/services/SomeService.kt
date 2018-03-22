package com.jorgecastillo.hiroaki.services

import com.jorgecastillo.hiroaki.data.networkdto.GsonArticleDto
import com.jorgecastillo.hiroaki.data.networkdto.MoshiNewsResponse
import com.jorgecastillo.hiroaki.services.dto.NonNestedData
import com.jorgecastillo.hiroaki.services.dto.NonNestedDataNumericArray
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SomeService {

    @GET("/my-fake-service/1")
    fun getSomeNonNestedData(): Call<NonNestedData>

    @GET("/my-fake-service/1")
    fun getSomeNonNestedDataNumericArray(): Call<NonNestedDataNumericArray>

    @GET("/my-fake-service/1")
    fun getNestedJson(): Call<MoshiNewsResponse>

    /**
     * This is a no-op method on the real news API, just created for testing purposes.
     */
    @POST("/my-fake-service")
    fun publishHeadline(@Body articleDto: GsonArticleDto): Call<Void>
}
