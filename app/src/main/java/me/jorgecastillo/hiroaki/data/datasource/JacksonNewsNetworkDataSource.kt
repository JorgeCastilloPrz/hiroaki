package me.jorgecastillo.hiroaki.data.datasource

import me.jorgecastillo.hiroaki.data.networkdto.toArticle
import me.jorgecastillo.hiroaki.data.networkdto.toJacksonDto
import me.jorgecastillo.hiroaki.data.service.JacksonNewsApiService
import me.jorgecastillo.hiroaki.model.Article
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.IOException

/**
 * Basic sample class to request data from network.
 */
class JacksonNewsNetworkDataSource(private val service: JacksonNewsApiService) {

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
            val response = service.publishHeadline(article.toJacksonDto()).execute()
            if (!response.isSuccessful) {
                throw IOException("Coins not found.")
            }
        }

        return query.await()
    }
}
