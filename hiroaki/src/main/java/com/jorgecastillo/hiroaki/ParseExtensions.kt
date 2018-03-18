package com.jorgecastillo.hiroaki

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import okhttp3.mockwebserver.RecordedRequest
import java.io.File

@Throws(Exception::class)
fun <T : Any> T.fileContentAsString(fileName: String): String {
    val context = DispatcherRetainer.androidContext
    return if (context != null) {
        val inputStream = context.resources.assets.open(fileName)
        convertStreamToString(inputStream)
    } else {
        val classLoader = this::class.java.classLoader
        val file = File(classLoader.getResource(fileName).file)
        file.readText(Charsets.UTF_8)
    }
}

fun convertStreamToString(inputStream: java.io.InputStream): String {
    val s = java.util.Scanner(inputStream)
            .useDelimiter("\\A")
    return if (s.hasNext()) s.next() else ""
}

@Throws(Exception::class)
fun <T> String.fromJson(clazz: Class<T>): T =
        GsonBuilder().create().fromJson(this, clazz)

@Throws(JsonParseException::class)
fun <T> RecordedRequest.parse(clazz: Class<T>): Pair<T, String> {
    val bodyString = this.body.readUtf8()
    return Pair(GsonBuilder().create().fromJson(bodyString, clazz), bodyString)
}

@Throws(JsonParseException::class)
fun <T> String.parse(clazz: Class<T>): Pair<T, String> {
    return Pair(fromJson(clazz), this)
}
