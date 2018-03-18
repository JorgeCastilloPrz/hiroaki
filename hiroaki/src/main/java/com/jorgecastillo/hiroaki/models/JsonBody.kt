package com.jorgecastillo.hiroaki.models

fun inlineBody(jsonBody: String): JsonBody = JsonBody(jsonBody)

fun fileBody(jsonBodyResFile: String): JsonBodyFile = JsonBodyFile(jsonBodyResFile)

data class JsonBody(val jsonBody: String)
data class JsonBodyFile(val jsonBodyResFile: String)
