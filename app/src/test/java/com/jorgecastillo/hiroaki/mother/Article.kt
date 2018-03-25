package com.jorgecastillo.hiroaki.mother

import me.jorgecastillo.hiroaki.model.Article
import me.jorgecastillo.hiroaki.model.Source

const val anyTitle = "Any Title"
const val anyDescription = "Any description"
const val anyUrl = "http://any.url"
const val anyImageUrl = "http://any.url/any_image.png"
const val anyPublishedAt = "2018-03-10T14:09:00Z"
const val anySourceId = "AnyId"
const val anySourceName = "ANYID"

fun anyArticle() = Article(
        anyTitle,
        anyDescription,
        anyUrl,
        anyImageUrl,
        anyPublishedAt,
        Source(anySourceId, anySourceName)
)
