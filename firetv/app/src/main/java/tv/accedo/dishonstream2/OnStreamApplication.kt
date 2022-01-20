package tv.accedo.dishonstream2

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import tv.accedo.dishonstream2.data.di.helperModule
import tv.accedo.dishonstream2.data.di.networkingModule
import tv.accedo.dishonstream2.data.di.repositoryModule
import tv.accedo.dishonstream2.data.di.storageModule
import tv.accedo.dishonstream2.di.*
import tv.accedo.dishonstream2.domain.di.interactionModule

class OnStreamApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@OnStreamApplication)
            if (BuildConfig.DEBUG) androidLogger(Level.ERROR)
            modules(
                modules = listOf(networkingModule, repositoryModule, helperModule, storageModule) +
                    listOf(interactionModule) + listOf(presentationModule)
            )
        }
    }
}

