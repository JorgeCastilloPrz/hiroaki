package me.jorgecastillo.hiroaki.dispatcher

import android.annotation.SuppressLint
import android.content.Context
import com.jorgecastillo.hiroaki.dispatcher.DispatcherAdapter
import com.jorgecastillo.hiroaki.dispatcher.HiroakiDispatcher
import com.jorgecastillo.hiroaki.dispatcher.HiroakiQueueDispatcher
import com.jorgecastillo.hiroaki.dispatcher.Retainer

@SuppressLint("StaticFieldLeak")
internal object AndroidDispatcherRetainer : Retainer {

    val queueDispatcher = HiroakiQueueDispatcher()
    val hiroakiDispatcher = HiroakiDispatcher()
    var androidContext: Context? = null

    fun registerRetainer() {
        DispatcherAdapter.register(this)
    }

    fun resetDispatchers() {
        queueDispatcher.reset()
        hiroakiDispatcher.reset()
    }

    override fun dispatchedRequests() =
        queueDispatcher.dispatchedRequests + hiroakiDispatcher.dispatchedRequests

    @Throws(Exception::class)
    override fun <T : Any> fileContentAsString(
        fileName: String,
        receiver: T
    ): String {
        val inputStream = androidContext!!.resources.assets.open(fileName)
        return convertStreamToString(inputStream)
    }

    private fun convertStreamToString(inputStream: java.io.InputStream): String {
        val s = java.util.Scanner(inputStream)
                .useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }
}
