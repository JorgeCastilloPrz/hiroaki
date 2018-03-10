package com.jorgecastillo.hiroaki

import java.io.File

@Throws(Exception::class)
fun <T : Any> T.fileContentAsString(fileName: String): String {
    val classLoader = this::class.java.classLoader
    val file = File(classLoader.getResource(fileName).file)
    return file.readText()
}
