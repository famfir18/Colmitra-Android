package com.softnesia.colmitra.config

import android.app.Application
import android.content.Context
import com.softnesia.colmitra.util.security.ObscuredSharedPreferences

class MyApplication : Application() {
    val SETTINGS_NAME: String = "default_settings"

    @get:Synchronized
    lateinit var sp: ObscuredSharedPreferences
        private set

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        sp = ObscuredSharedPreferences(
            applicationContext,
            getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)
        )
    }

    companion object {
        @get:Synchronized
        var instance: MyApplication? = null
            private set

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}