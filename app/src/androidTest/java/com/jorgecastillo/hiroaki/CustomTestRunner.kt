package com.jorgecastillo.hiroaki

import android.app.Application
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnitRunner

class CustomTestRunner : AndroidJUnitRunner() {

    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(
        cl: ClassLoader,
        className: String,
        context: Context
    ): Application = super.newApplication(cl, TestSampleApp::class.java.name, context)
}
