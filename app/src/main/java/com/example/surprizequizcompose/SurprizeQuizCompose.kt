package com.example.surprizequizcompose

import android.app.Application
import com.example.surprizequizcompose.di.module.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SurprizeQuizCompose: Application() {

    override fun onCreate() {
        super.onCreate()
        config()
    }

    private fun config() {
        startKoin {
            androidContext(this@SurprizeQuizCompose)
            modules(Test.module())
        }
    }
}