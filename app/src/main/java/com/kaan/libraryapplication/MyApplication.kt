package com.kaan.libraryapplication

import android.app.Application
import com.kaan.libraryapplication.di.AppContainer

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize DI container
        AppContainer.init(this)
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }

    init {
        instance = this
    }
}
