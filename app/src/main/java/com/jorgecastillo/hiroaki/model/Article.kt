package com.jorgecastillo.hiroaki.model

data class Article(
        val title: String,
        val description: String,
        val url: String,
        val urlToImage: String,
        val publishedAt: String,
        val source: Source
)

data class Source(val id: String?, val name: String)
