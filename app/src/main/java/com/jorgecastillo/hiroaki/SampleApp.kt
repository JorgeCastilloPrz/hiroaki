package com.jorgecastillo.hiroaki

import android.app.Activity
import android.app.Application
import com.jorgecastillo.hiroaki.data.service.MoshiNewsApiService
import com.jorgecastillo.hiroaki.di.provideNewsService

open class SampleApp : Application() {

    open fun newsService(): MoshiNewsApiService = provideNewsService()
}

fun Activity.getApp() = application as SampleApp
