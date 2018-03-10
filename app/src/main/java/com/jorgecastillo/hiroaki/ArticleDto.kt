package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.model.Article

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
    val publishedAt: String
)

fun ArticleDto.toArticle() = Article(title, description, url, urlToImage, publishedAt)

fun Article.toDto() = ArticleDto(title, description, url, urlToImage, publishedAt)
