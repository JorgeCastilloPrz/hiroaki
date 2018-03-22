package com.jorgecastillo.hiroaki.models

import com.jorgecastillo.hiroaki.models.Body.JsonBody
import com.jorgecastillo.hiroaki.models.Body.JsonBodyFile
import com.jorgecastillo.hiroaki.models.Body.JsonDSL

/*
fun test() {
    json {
        "language" / "Kotlin"
        "description" / "Statically typed JVM language"
        "version" / "1.1.4"
        "someArray" / arrayOf("First", "Second", "Third")
        "nestedJson" / json {
            "key" / "12356some01ko12os01key234908="
            "id" / 147L
        }
    }
}*/

/*class Json {
    private lateinit var jsonObject: Obj
    fun render(): String = jsonObject.toJsonString()
}*/

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

        infix operator fun String.div(value: Int?): Unit {
            entries[this] = value
        }

        infix operator fun String.div(value: Long?): Unit {
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

        fun toJsonString(indent: String = " "): String {
            val sb = StringBuilder().append("{")
                    .append("\n")
            for ((k, v) in entries) {
                with(sb) {
                    append("""$indent"$k" : """)
                    when (v) {
                        is String -> append(""""$v"""")
                        is Array<*> -> append("[${v.joinToString()}]")
                        else -> append("$v")
                    }
                    append("\n")
                }
            }
            return sb.append("$indent}")
                    .toString()
        }
    }
}

fun json(init: JsonDSL.() -> Unit): JsonDSL {
    val json = JsonDSL()
    json.init()
    return json
}
