package com.jorgecastillo.hiroaki.di

import com.jorgecastillo.hiroaki.NewsApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

fun provideNewsService(client: OkHttpClient = provideOkHttpClient()): NewsApiService =
        Retrofit.Builder().baseUrl("https://newsapi.org")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create()).build()
                .create(NewsApiService::class.java)
