package me.jorgecastillo.hiroaki

import androidx.test.platform.app.InstrumentationRegistry

fun getApp() =
    (InstrumentationRegistry.getInstrumentation()
        .targetContext
        .applicationContext as TestSampleApp)
