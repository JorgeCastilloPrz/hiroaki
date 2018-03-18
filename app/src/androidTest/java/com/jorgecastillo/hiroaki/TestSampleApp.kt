package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.data.service.MoshiNewsApiService

class TestSampleApp : SampleApp() {

    lateinit var service: MoshiNewsApiService

    override fun newsService(): MoshiNewsApiService = service
}
