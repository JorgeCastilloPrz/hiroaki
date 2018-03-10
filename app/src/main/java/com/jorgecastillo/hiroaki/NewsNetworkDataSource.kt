package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.model.Article
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.IOException

/**
 * Basic sample class to request data from network.
 */
class NewsNetworkDataSource(private val service: NewsApiService) {

    @Throws(IOException::class)
    suspend fun getNews(): List<Article> {
        val a = async(CommonPool) {
            val response = service.getNews().execute()
            if (response.isSuccessful) {
                response.body()!!.articles.map { it.toArticle() }
            } else throw IOException("Coins not found.")
        }

        return a.await()
    }
}
