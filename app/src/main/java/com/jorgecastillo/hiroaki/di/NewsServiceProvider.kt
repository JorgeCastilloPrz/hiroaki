package com.jorgecastillo.hiroaki.di

import com.jorgecastillo.hiroaki.data.service.MoshiNewsApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

fun provideNewsService(client: OkHttpClient = provideOkHttpClient()): MoshiNewsApiService =
        Retrofit.Builder().baseUrl("https://newsapi.org")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create()).build()
                .create(MoshiNewsApiService::class.java)
