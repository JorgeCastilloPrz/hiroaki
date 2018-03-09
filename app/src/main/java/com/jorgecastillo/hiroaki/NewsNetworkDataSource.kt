package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.model.Article
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

/**
 * Basic sample class to request data from network.
 */
class NewsNetworkDataSource {

    private val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(Level.BODY))
            .build()

    private val coinCapApiClient
        get() = Retrofit.Builder().baseUrl("https://newsapi.org")
                .client(httpClient)
                .addConverterFactory(MoshiConverterFactory.create()).build()
                .create(NewsApiService::class.java)

    @Throws(IOException::class)
    suspend fun fetchNews(): List<Article> {
        val a = async(CommonPool) {
            val response = coinCapApiClient.getNews().execute()
            if (response.isSuccessful) {
                response.body()!!.articles.map { it.toArticle() }
            } else throw IOException("Coins not found.")
        }

        return a.await()
    }
}
