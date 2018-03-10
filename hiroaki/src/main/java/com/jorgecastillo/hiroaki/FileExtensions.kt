package com.jorgecastillo.hiroaki

import java.io.File

@Throws(Exception::class)
fun String.fileContentAsString(): String {
    val file = File(this.javaClass.classLoader.getResource(this).file)
    return file.readText()
}
