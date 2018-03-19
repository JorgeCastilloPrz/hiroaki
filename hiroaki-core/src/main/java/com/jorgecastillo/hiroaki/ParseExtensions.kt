package com.jorgecastillo.hiroaki

import com.google.gson.JsonParseException
import com.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import okhttp3.mockwebserver.RecordedRequest
import java.io.File
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType.Object
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.jorgecastillo.hiroaki.dispatcher.DispatcherAdapter

@Throws(Exception::class)
fun <T : Any> T.fileContentAsString(fileName: String): String =
        DispatcherAdapter.fileContentAsString(fileName, this)

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
