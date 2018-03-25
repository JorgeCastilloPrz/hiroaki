package me.jorgecastillo.hiroaki

import me.jorgecastillo.hiroaki.Either.Left
import me.jorgecastillo.hiroaki.Either.Right

typealias NetworkDto = Class<*>
typealias QueryParams = Map<String, String>
typealias Headers = Map<String, String>

fun params(vararg pairs: Pair<String, String>): QueryParams =
        if (pairs.isNotEmpty()) pairs.toMap() else emptyMap()

fun headers(vararg pairs: Pair<String, String>): Headers =
        if (pairs.isNotEmpty()) pairs.toMap() else emptyMap()

sealed class Either<out L, out R> {
    class Left<L, R>(val value: L) : Either<L, R>()
    class Right<L, R>(val value: R) : Either<L, R>()

    fun <C> fold(fa: (L) -> C, fb: (R) -> C): C = when (this) {
        is Right<L, R> -> fb(value)
        is Left<L, R> -> fa(value)
    }
}

fun <L, R> L.left() = Left<L, R>(this)
fun <L, R> R.right() = Right<L, R>(this)
