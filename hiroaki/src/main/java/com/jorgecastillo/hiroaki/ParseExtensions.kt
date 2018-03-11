package com.jorgecastillo.hiroaki

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import okhttp3.mockwebserver.RecordedRequest
import java.io.File

@Throws(Exception::class)
fun <T : Any> T.fileContentAsString(fileName: String): String {
    val classLoader = this::class.java.classLoader
    val file = File(classLoader.getResource(fileName).file)
    return file.readText(Charsets.UTF_8)
}

@Throws(Exception::class)
fun <T> String.fromJson(clazz: Class<T>): T =
        GsonBuilder().create().fromJson(this, clazz)

@Throws(JsonParseException::class)
fun <T> RecordedRequest.parse(clazz: Class<T>): Pair<T, String> {
    val bodyString = this.body.readUtf8()
    return Pair(GsonBuilder().create().fromJson(bodyString, clazz), bodyString)
}
