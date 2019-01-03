package me.jorgecastillo.hiroaki.data.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import me.jorgecastillo.hiroaki.data.networkdto.toArticle
import me.jorgecastillo.hiroaki.data.networkdto.toGsonDto
import me.jorgecastillo.hiroaki.data.service.GsonNewsApiService
import me.jorgecastillo.hiroaki.model.Article
import java.io.IOException

/**
 * Basic sample class to request data from network.
 */
class GsonNewsNetworkDataSource(private val service: GsonNewsApiService) {

    @Throws(IOException::class)
    suspend fun getNews(): List<Article> {
        val query = GlobalScope.async(Dispatchers.IO) {
            val response = service.getNews().execute()
            if (response.isSuccessful) {
                response.body()!!.articles.map { it.toArticle() }
            } else throw IOException("News not found.")
        }

        return query.await()
    }

    /**
     * This is a no-op method on the real news API, just created for testing purposes.
     */
    @Throws(IOException::class)
    suspend fun publishHeadline(article: Article) {
        val query = GlobalScope.async(Dispatchers.IO) {
            val response = service.publishHeadline(article.toGsonDto()).execute()
            if (!response.isSuccessful) {
                throw IOException("Coins not found.")
            }
        }

        return query.await()
    }
}
