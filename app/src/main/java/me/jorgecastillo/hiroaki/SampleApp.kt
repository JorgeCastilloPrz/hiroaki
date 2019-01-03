package me.jorgecastillo.hiroaki

import android.app.Activity
import androidx.multidex.MultiDexApplication
import me.jorgecastillo.hiroaki.data.service.MoshiNewsApiService
import me.jorgecastillo.hiroaki.di.provideNewsService

open class SampleApp : MultiDexApplication() {

    open fun newsService(): MoshiNewsApiService =
            provideNewsService()
}

fun Activity.getApp() = application as SampleApp
