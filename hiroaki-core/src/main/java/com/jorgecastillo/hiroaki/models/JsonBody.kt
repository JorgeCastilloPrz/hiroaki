package com.jorgecastillo.hiroaki.models

import com.jorgecastillo.hiroaki.models.Body.JsonBody
import com.jorgecastillo.hiroaki.models.Body.JsonBodyFile
import com.jorgecastillo.hiroaki.models.Body.Json
import com.jorgecastillo.hiroaki.models.Body.JsonArray

fun inlineBody(jsonBody: String): JsonBody = JsonBody(jsonBody)

fun fileBody(jsonBodyResFile: String): JsonBodyFile = JsonBodyFile(jsonBodyResFile)

sealed class Body {
    data class JsonBody(val jsonBody: String) : Body()

    data class JsonBodyFile(val jsonBodyResFile: String) : Body()

    class JsonArray<T>(val array: Array<T>) : Body() {
        override fun toString(): String {
            return toJsonString()
        }

        fun toJsonString(indent: String = " "): String {
            fun toJsonStringNested(
                nested: JsonArray<T>,
                indent: String
            ): String {
                val sb = StringBuilder().append("[")
                        .append("\n")

                var index = 0
                for (v in nested.array) {
                    with(sb) {
                        when (v) {
                            is String -> {
                                append("\"$v\"")
                            }
                            is JsonArray<*> -> {
                                append(toJsonString(indent = "$indent "))
                            }
                            is Json -> {
                                append(v.toJsonString(indent = "$indent "))
                            }
                            else -> {
                                append("$v")
                            }
                        }
                        if (index < nested.array.size - 1) {
                            append(",")
                        }
                        append("\n")
                    }
                    index += 1
                }
                return sb.append("$indent]")
                        .toString()
            }
            return toJsonStringNested(this, indent = indent)
        }
    }

    class Json : Body() {
        private val entries = linkedMapOf<String, Any?>()

        infix operator fun String.div(value: String?) {
            entries[this] = value
        }

        infix operator fun String.div(value: Number?) {
            entries[this] = value
        }

        infix operator fun String.div(value: Double?) {
            entries[this] = value
        }

        infix operator fun String.div(value: Boolean?) {
            entries[this] = value
        }

        infix operator fun String.div(value: JsonArray<*>?) {
            entries[this] = value
        }

        infix operator fun String.div(value: Json?) {
            entries[this] = value
        }

        override fun toString(): String {
            return toJsonString()
        }

        fun toJsonString(indent: String = " "): String {
            fun toJsonStringNested(
                nested: Json,
                indent: String
            ): String {
                val sb = StringBuilder().append("{")
                        .append("\n")

                var index = 0
                for ((k, v) in nested.entries) {
                    with(sb) {
                        append("""$indent"$k" : """)
                        when (v) {
                            is String -> {
                                append("\"$v\"")
                            }
                            is JsonArray<*> -> {
                                append("[")
                                v.array.forEachIndexed { index, item ->
                                    when (item) {
                                        is Json -> {
                                            append(item.toJsonString())
                                        }
                                        is String -> {
                                            append("\"$item\"")
                                        }
                                        else -> {
                                            append("$item")
                                        }
                                    }
                                    if (index < v.array.size - 1) {
                                        append(",")
                                    }
                                }
                                append("]")
                            }
                            is Json -> {
                                append(toJsonStringNested(nested = v, indent = "$indent "))
                            }
                            else -> {
                                append("$v")
                            }
                        }
                        if (index < nested.entries.size - 1) {
                            append(",")
                        }
                        append("\n")
                    }
                    index += 1
                }
                return sb.append("$indent}")
                        .toString()
            }
            return toJsonStringNested(this, indent = indent)
        }

        fun getValue(key: String) = entries[key]
    }
}

inline fun <reified T> jsonArray(vararg elements: T = arrayOf()): JsonArray<T> =
    JsonArray(arrayOf(elements).flatten().toTypedArray())

fun json(init: Json.() -> Unit): Json {
    val json = Json()
    json.init()
    return json
}
