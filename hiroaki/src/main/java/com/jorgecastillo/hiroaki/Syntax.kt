package com.jorgecastillo.hiroaki

typealias NetworkDto = Class<*>
typealias QueryParams = Map<String, String>
typealias Headers = Map<String, String>

fun params(vararg pairs: Pair<String, String>): QueryParams =
        if (pairs.isNotEmpty()) pairs.toMap() else emptyMap()

fun headers(vararg pairs: Pair<String, String>): Headers =
        if (pairs.isNotEmpty()) pairs.toMap() else emptyMap()
