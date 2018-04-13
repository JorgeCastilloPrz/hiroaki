package me.jorgecastillo.hiroaki.dispatcher

import okhttp3.mockwebserver.RecordedRequest

object DispatcherAdapter {
    private val registeredDispatchers: MutableMap<String, Retainer> = mutableMapOf()

    fun register(retainer: Retainer) {
        registeredDispatchers[retainer::class.java.simpleName] = retainer
    }

    fun dispatchedRequests(): List<RecordedRequest> {
        val requests = registeredDispatchers["DispatcherRetainer"]?.dispatchedRequests() ?: listOf()
        val androidRequests = registeredDispatchers["AndroidDispatcherRetainer"]?.dispatchedRequests() ?: listOf()
        val allRequests = requests.union(androidRequests).toList()
        return allRequests
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
