package me.jorgecastillo.hiroaki.data.networkdto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import me.jorgecastillo.hiroaki.model.Article
import me.jorgecastillo.hiroaki.model.Source

@JsonIgnoreProperties(ignoreUnknown = true)
data class JacksonNewsResponse(
    @JsonProperty("status") val status: String,
    @JsonProperty("totalResults") val totalResults: Long,
    @JsonProperty("articles") val articles: List<JacksonArticleDto>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class JacksonArticleDto(
    @JsonProperty("title") val title: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("url") val url: String,
    @JsonProperty("urlToImage") val urlToImage: String,
    @JsonProperty("publishedAt") val publishedAt: String,
    @JsonProperty("source") val source: JacksonSourceDto
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class JacksonSourceDto(
    @JsonProperty("id") val id: String?,
    @JsonProperty("name") val name: String
)

fun JacksonArticleDto.toArticle() =
    Article(title, description, url, urlToImage, publishedAt, source.toSource())

fun Article.toJacksonDto() = JacksonArticleDto(
        title, description, url, urlToImage, publishedAt, source.toJacksonDto()
)

fun JacksonSourceDto.toSource() =
    Source(id, name)

fun Source.toJacksonDto() =
    JacksonSourceDto(id, name)
