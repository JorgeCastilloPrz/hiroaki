package com.jorgecastillo.hiroaki.dispatcher

object DispatcherRetainer {
    val queueDispatcher = HiroakiQueueDispatcher()
    val hiroakiDispatcher = HiroakiDispatcher()

    fun resetDispatchers() {
        queueDispatcher.reset()
        hiroakiDispatcher.reset()
    }

    fun dispatchedRequests() =
            queueDispatcher.dispatchedRequests + hiroakiDispatcher.dispatchedRequests
}
