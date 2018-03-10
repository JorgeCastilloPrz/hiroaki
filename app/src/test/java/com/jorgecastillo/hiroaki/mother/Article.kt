package com.jorgecastillo.hiroaki.mother

import com.jorgecastillo.hiroaki.model.Article

const val anyTitle = "Any Title"
const val anyDescription = "Any description"
const val anyUrl = "http://any.url"
const val anyImageUrl = "http://any.url/any_image.png"
const val anyPublishedAt = "2018-03-10T14:09:00Z"

fun anyArticle() = Article(anyTitle, anyDescription, anyUrl, anyImageUrl, anyPublishedAt)
