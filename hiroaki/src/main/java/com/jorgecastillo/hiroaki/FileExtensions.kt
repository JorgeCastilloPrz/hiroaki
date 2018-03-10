package com.jorgecastillo.hiroaki

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

@Throws(Exception::class)
fun <T : Any> T.fileContentAsString(fileName: String): String {
    val classLoader = this::class.java.classLoader
    val file = File(classLoader.getResource(fileName).file)
    return file.readText(Charsets.UTF_8)
}

@Throws(Exception::class)
fun <T : Any> T.fileContentAsMap(fileName: String): Map<String, String> {
    val fileString = fileContentAsString(fileName)
    val mapType = object : TypeToken<Map<String, String>>() {}.type
    return GsonBuilder().create().fromJson(fileString, mapType)
}
