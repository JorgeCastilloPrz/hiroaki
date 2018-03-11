package com.jorgecastillo.hiroaki

/**
 * Mimics the `to` infix operator to create a tuple. It's just a rename to provide semantics on
 * how it's used on this library.
 *
 * Creates a tuple of type [Pair] from this and [that].
 *
 * This can be useful for creating [Map] literals with less noise, for example:
 * @sample samples.collections.Maps.Instantiation.mapFromPairs
 */
public infix fun <A, B> A.withType(that: B): Pair<A, B> = Pair(this, that)
