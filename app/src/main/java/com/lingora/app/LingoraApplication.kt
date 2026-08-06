package com.lingora.app

import android.app.Application
import com.lingora.app.di.AppContainer

class LingoraApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
