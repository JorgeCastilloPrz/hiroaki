package me.jorgecastillo.hiroaki.data.networkdto

import me.jorgecastillo.hiroaki.model.Article
import me.jorgecastillo.hiroaki.model.Source

data class MoshiNewsResponse(
    val status: String,
    val totalResults: Long,
    val articles: List<MoshiArticleDto>
)

data class MoshiArticleDto(
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val source: MoshiSourceDto
)

data class MoshiSourceDto(val id: String?, val name: String)

fun MoshiArticleDto.toArticle() =
    Article(title, description, url, urlToImage, publishedAt, source.toSource())

fun Article.toMoshiDto() = MoshiArticleDto(
        title, description, url, urlToImage, publishedAt, source.toMoshiDto()
)

fun MoshiSourceDto.toSource() = Source(id, name)

fun Source.toMoshiDto() =
    MoshiSourceDto(id, name)
