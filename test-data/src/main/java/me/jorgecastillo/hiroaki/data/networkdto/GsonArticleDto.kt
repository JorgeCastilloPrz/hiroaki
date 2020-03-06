package me.jorgecastillo.hiroaki.data.networkdto

import com.google.gson.annotations.SerializedName
import me.jorgecastillo.hiroaki.model.Article
import me.jorgecastillo.hiroaki.model.Source

data class GsonNewsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResults: Long,
    @SerializedName("articles") val articles: List<GsonArticleDto>
)

data class GsonArticleDto(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("url") val url: String,
    @SerializedName("urlToImage") val urlToImage: String,
    @SerializedName("publishedAt") val publishedAt: String,
    @SerializedName("source") val source: GsonSourceDto
)

data class GsonSourceDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String
)

fun GsonArticleDto.toArticle() =
    Article(title, description, url, urlToImage, publishedAt, source.toSource())

fun Article.toGsonDto() = GsonArticleDto(
        title, description, url, urlToImage, publishedAt, source.toGsonDto()
)

fun GsonSourceDto.toSource() = Source(id, name)

fun Source.toGsonDto() = GsonSourceDto(id, name)
