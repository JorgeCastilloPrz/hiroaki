package com.jorgecastillo.hiroaki

import retrofit2.Call
import retrofit2.http.GET

interface NewsApiService {
    @GET("/v2/top-headlines?sources=crypto-coins-news&apiKey=a7c816f57c004c49a21bd458e11e2807")
    fun getNews(): Call<NewsResponse>
}

