package com.jorgecastillo.hiroaki

import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import okhttp3.mockwebserver.RecordedRequest
import java.io.File
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType.Object
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap

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

private fun convertStreamToString(inputStream: java.io.InputStream): String {
    val s = java.util.Scanner(inputStream)
            .useDelimiter("\\A")
    return if (s.hasNext()) s.next() else ""
}

@Throws(Exception::class)
fun String.fromJson(): LinkedTreeMap<String, Object> {
    val gson = Gson()
    return gson.fromJson<LinkedTreeMap<String, Object>>(this, LinkedTreeMap::class.java)
}

@Throws(JsonParseException::class)
fun RecordedRequest.parse(): Pair<LinkedTreeMap<String, Object>, String> {
    val bodyString = this.body.snapshot().utf8()
    return Pair(bodyString.fromJson(), bodyString)
}

