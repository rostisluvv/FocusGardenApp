package com.example.focusgarden

import android.app.Application
import com.example.focusgarden.di.AppContainer

class FocusGardenApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
