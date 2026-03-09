package com.mudita.nav3sample

import android.app.Application
import com.mudita.core.data.di.dataModule
import com.mudita.features.auth.di.authFeatureModule
import com.mudita.features.order.di.orderFeatureModule
import com.mudita.nav3sample.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                appModule,            // App: config, global singletons
                dataModule,           // Data: repositories
                authFeatureModule,    // Auth: AuthManager + ViewModels
                orderFeatureModule    // Order: Use Cases + ViewModels
            )
        }
    }
}
