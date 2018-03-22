package com.jorgecastillo.hiroaki.models

import com.jorgecastillo.hiroaki.models.Body.JsonBody
import com.jorgecastillo.hiroaki.models.Body.JsonBodyFile
import com.jorgecastillo.hiroaki.models.Body.JsonDSL

fun inlineBody(jsonBody: String): JsonBody = JsonBody(jsonBody)

fun fileBody(jsonBodyResFile: String): JsonBodyFile = JsonBodyFile(jsonBodyResFile)

sealed class Body {
    data class JsonBody(val jsonBody: String) : Body()

    data class JsonBodyFile(val jsonBodyResFile: String) : Body()

    class JsonDSL : Body() {
        private val entries = linkedMapOf<String, Any?>()

        infix operator fun String.div(value: String?): Unit {
            entries[this] = value
        }

        infix operator fun String.div(value: Number?): Unit {
            entries[this] = value
        }

        infix operator fun String.div(value: Double?): Unit {
            entries[this] = value
        }

        infix operator fun String.div(value: Boolean?): Unit {
            entries[this] = value
        }

        infix operator fun String.div(value: Array<*>?): Unit {
            entries[this] = value
        }

        infix operator fun String.div(value: JsonDSL?): Unit {
            entries[this] = value
        }

        override fun toString(): String {
            return toJsonString()
        }

        fun toJsonString(indent: String = " "): String {
            fun toJsonStringNested(
                nested: JsonDSL,
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
                            is Array<*> -> {
                                append("[")
                                v.forEachIndexed { index, item ->
                                    when (item) {
                                        is JsonDSL -> {
                                            append(item.toJsonString())
                                        }
                                        is String -> {
                                            append("\"$item\"")
                                        }
                                        else -> {
                                            append("$item")
                                        }
                                    }
                                    if (index < v.size - 1) {
                                        append(",")
                                    }
                                }
                                append("]")
                            }
                            is JsonDSL -> {
                                append(toJsonStringNested(nested = v, indent = "$indent "))
                            }
                            else -> {
                                append("$v")
                            }
                        }
                        if (index < nested.entries.size - 1){
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

inline fun <reified T> jsonArray(vararg elements: T = arrayOf()): Array<T> = arrayOf(elements).flatten().toTypedArray()

fun json(init: JsonDSL.() -> Unit): JsonDSL {
    val json = JsonDSL()
    json.init()
    return json
}
