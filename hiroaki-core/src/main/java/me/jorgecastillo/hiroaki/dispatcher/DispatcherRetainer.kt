package me.jorgecastillo.hiroaki.dispatcher

import java.io.File

internal object DispatcherRetainer : Retainer {
    val queueDispatcher = HiroakiQueueDispatcher()
    val hiroakiDispatcher = HiroakiDispatcher()

    fun registerRetainer() {
        DispatcherAdapter.register(this)
    }

    fun resetDispatchers() {
        queueDispatcher.reset()
        hiroakiDispatcher.reset()
    }

    override fun dispatchedRequests() =
        queueDispatcher.dispatchedRequests + hiroakiDispatcher.dispatchedRequests

    override fun <T : Any> fileContentAsString(
        fileName: String,
        receiver: T
    ): String {
        val classLoader = receiver::class.java.classLoader
        val file = File(classLoader.getResource(fileName).file)
        return file.readText(Charsets.UTF_8)
    }
}
