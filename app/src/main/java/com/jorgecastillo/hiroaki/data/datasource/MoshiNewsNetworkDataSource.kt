package com.jorgecastillo.hiroaki.data.datasource

import com.jorgecastillo.hiroaki.data.service.MoshiNewsApiService
import com.jorgecastillo.hiroaki.model.Article
import com.jorgecastillo.hiroaki.data.networkdto.toArticle
import com.jorgecastillo.hiroaki.data.networkdto.toMoshiDto
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.IOException

/**
 * Basic sample class to request data from network.
 */
class MoshiNewsNetworkDataSource(private val service: MoshiNewsApiService) {

    @Throws(IOException::class)
    suspend fun getNews(): List<Article> {
        val query = async(CommonPool) {
            val response = service.getNews().execute()
            if (response.isSuccessful) {
                response.body()!!.articles.map { it.toArticle() }
            } else throw IOException("Coins not found.")
        }

        return query.await()
    }

    /**
     * This is a no-op method on the real news API, just created for testing purposes.
     */
    @Throws(IOException::class)
    suspend fun publishHeadline(article: Article) {
        val query = async(CommonPool) {
            val response = service.publishHeadline(article.toMoshiDto()).execute()
            if (!response.isSuccessful) {
                throw IOException("Coins not found.")
            }
        }

        return query.await()
    }
}
