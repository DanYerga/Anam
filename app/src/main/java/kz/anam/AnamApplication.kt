package kz.anam

import android.app.Application

class AnamApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AnamApplication
            private set
    }
}
