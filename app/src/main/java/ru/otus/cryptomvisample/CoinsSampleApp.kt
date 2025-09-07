package ru.otus.cryptomvisample

import android.app.Application
import ru.otus.cryptomvisample.di.AppComponent
import ru.otus.common.di.Dependencies
import ru.otus.common.di.DependenciesProvider
import ru.otus.cryptomvisample.di.DaggerAppComponent

class CoinsSampleApp: Application(), DependenciesProvider {
    val appComponent: AppComponent = DaggerAppComponent.factory().create(this)

    override fun getDependencies(): Dependencies {
        return appComponent
    }
}
