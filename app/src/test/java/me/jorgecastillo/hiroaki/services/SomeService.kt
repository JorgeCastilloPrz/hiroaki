package me.jorgecastillo.hiroaki.services

import me.jorgecastillo.hiroaki.data.networkdto.GsonArticleDto
import me.jorgecastillo.hiroaki.data.networkdto.MoshiNewsResponse
import me.jorgecastillo.hiroaki.services.dto.NonNestedData
import me.jorgecastillo.hiroaki.services.dto.NonNestedDataNumericArray
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SomeService {

    @GET("/my-fake-service/1")
    fun getSomeNonNestedData(): Call<NonNestedData>

    @GET("/my-fake-service/1")
    fun getSomeNonNestedDataNumericArray(): Call<NonNestedDataNumericArray>

    @GET("/my-fake-service/1")
    fun getNestedJson(): Call<MoshiNewsResponse>

    @GET("/my-fake-service/1")
    fun getSomeJsonWithArrayOnRootLevel(): Call<ArrayList<Int>>

    @GET("/my-fake-service/1")
    fun getSomeJsonWithStringArrayOnRootLevel(): Call<ArrayList<String>>

    @GET("/my-fake-service/1")
    fun getSomeJsonArrayWithJsonObjectsOnRootLevel(): Call<ArrayList<MoshiNewsResponse>>

    /**
     * This is a no-op method on the real news API, just created for testing purposes.
     */
    @POST("/my-fake-service")
    fun publishHeadline(@Body articleDto: GsonArticleDto): Call<Void>

    /**
     * Added to test query param verification for lists.
     */
    @POST("/my-fake-service/edit-tag")
    fun addNewsTags(
        @Query("i") ids: List<String>,
        @Query("a") tagIds: List<String>
    ): Call<Void>
}
