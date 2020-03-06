package me.jorgecastillo.hiroaki.data.service

import me.jorgecastillo.hiroaki.data.networkdto.MoshiArticleDto
import me.jorgecastillo.hiroaki.data.networkdto.MoshiNewsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface MoshiNewsApiService {
    @GET("/v2/top-headlines?sources=crypto-coins-news&apiKey=21a12ef352b649caa97499bed2e77350")
    @Headers("Cache-Control: max-age=640000")
    fun getNews(): Call<MoshiNewsResponse>

    /**
     * This is a no-op method on the real news API, just created for testing purposes.
     */
    @POST("/v2/top-headlines?apiKey=21a12ef352b649caa97499bed2e77350")
    fun publishHeadline(@Body articleDto: MoshiArticleDto): Call<Void>
}
