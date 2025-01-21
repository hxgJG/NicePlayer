package com.hxg.player

import android.app.Application
import android.content.Context
import android.content.res.Configuration

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
//        onConfigurationChanged(applicationContext)
    }

    companion object {
        @JvmStatic
        lateinit var context: App
            private set

    }
}