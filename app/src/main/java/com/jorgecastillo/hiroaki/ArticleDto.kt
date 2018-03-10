package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.model.Article
import com.jorgecastillo.hiroaki.model.Source

data class NewsResponse(
    val status: String,
    val totalResults: Long,
    val articles: List<ArticleDto>
)

data class ArticleDto(
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val source: SourceDto
)

data class SourceDto(val id: String?, val name: String)

fun ArticleDto.toArticle() = Article(title, description, url, urlToImage, publishedAt, source.toSource())

fun Article.toDto() = ArticleDto(title, description, url, urlToImage, publishedAt, source.toSourceDto())

fun SourceDto.toSource() = Source(id, name)

fun Source.toSourceDto() = SourceDto(id, name)
