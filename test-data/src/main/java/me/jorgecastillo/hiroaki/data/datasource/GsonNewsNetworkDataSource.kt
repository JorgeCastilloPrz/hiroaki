package me.jorgecastillo.hiroaki.data.datasource

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
    fun getNews(): List<Article> {
        val response = service.getNews().execute()
        if (response.isSuccessful) {
            return response.body()!!.articles.map { it.toArticle() }
        } else throw IOException("News not found.")
    }

    /**
     * This is a no-op method on the real news API, just created for testing purposes.
     */
    @Throws(IOException::class)
    fun publishHeadline(article: Article) {
        val response = service.publishHeadline(article.toGsonDto()).execute()
        if (!response.isSuccessful) {
            throw IOException("Coins not found.")
        }
    }
}
