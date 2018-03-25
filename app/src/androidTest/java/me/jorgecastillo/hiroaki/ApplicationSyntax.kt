package me.jorgecastillo.hiroaki

import android.support.test.InstrumentationRegistry

fun getApp() =
    (InstrumentationRegistry.getInstrumentation()
            .targetContext
            .applicationContext as TestSampleApp)
