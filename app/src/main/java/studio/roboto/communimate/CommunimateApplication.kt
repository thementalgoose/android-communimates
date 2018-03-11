package studio.roboto.communimate

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

class CommunimateApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}