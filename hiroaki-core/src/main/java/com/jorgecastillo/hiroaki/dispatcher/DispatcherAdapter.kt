package com.jorgecastillo.hiroaki.dispatcher

import okhttp3.mockwebserver.RecordedRequest

object DispatcherAdapter {
    private val registeredDispatchers: MutableMap<String, Retainer> = mutableMapOf()

    fun register(retainer: Retainer): Unit {
        registeredDispatchers[retainer::class.java.simpleName] = retainer
    }

    fun dispatchedRequests(): List<RecordedRequest> {
        val requests = registeredDispatchers["DispatcherRetainer"]?.dispatchedRequests()
        val androidRequests = registeredDispatchers["AndroidDispatcherRetainer"]?.dispatchedRequests()
        val allRequests = requests?.union(androidRequests ?: listOf())?.toList()
        return allRequests ?: listOf()
    }

    @Throws(Exception::class)
    fun <T : Any> fileContentAsString(fileName: String, receiver: T): String {
        return registeredDispatchers.values.fold("", { acc, retainer ->
            try {
                if (acc.isEmpty()) retainer.fileContentAsString(fileName, receiver) else acc
            } catch (e: Exception) {
                acc
            }
        })
    }
}
