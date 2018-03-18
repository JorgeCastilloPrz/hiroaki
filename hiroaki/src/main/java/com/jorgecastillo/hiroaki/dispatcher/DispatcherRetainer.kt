package com.jorgecastillo.hiroaki.dispatcher

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object DispatcherRetainer {
    val queueDispatcher = HiroakiQueueDispatcher()
    val hiroakiDispatcher = HiroakiDispatcher()
    var androidContext: Context? = null

    fun resetDispatchers() {
        queueDispatcher.reset()
        hiroakiDispatcher.reset()
    }

    fun dispatchedRequests() =
            queueDispatcher.dispatchedRequests + hiroakiDispatcher.dispatchedRequests
}
