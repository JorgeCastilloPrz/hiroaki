package com.jorgecastillo.hiroaki

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface NewsApiService {
    @GET("/v2/top-headlines?sources=crypto-coins-news&apiKey=a7c816f57c004c49a21bd458e11e2807")
    @Headers("Cache-Control: max-age=640000")
    fun getNews(): Call<NewsResponse>

    /**
     * This is a no-op method on the real news API, just created for testing purposes.
     */
    @POST("/v2/top-headlines?apiKey=a7c816f57c004c49a21bd458e11e2807")
    fun publishHeadline(@Body articleDto: ArticleDto): Call<Void>
}
