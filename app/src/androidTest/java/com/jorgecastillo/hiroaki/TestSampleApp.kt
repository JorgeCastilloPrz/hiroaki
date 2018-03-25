package com.jorgecastillo.hiroaki

import me.jorgecastillo.hiroaki.SampleApp
import me.jorgecastillo.hiroaki.data.service.MoshiNewsApiService

class TestSampleApp : SampleApp() {

    lateinit var service: MoshiNewsApiService

    override fun newsService(): MoshiNewsApiService = service
}
