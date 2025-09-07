package ru.otus.cryptosample

import android.app.Application
import ru.otus.cryptosample.di.AppComponent
import ru.otus.common.di.Dependencies
import ru.otus.common.di.DependenciesProvider
import ru.otus.cryptosample.di.DaggerAppComponent

class CoinsSampleApp: Application(), DependenciesProvider {
    val appComponent: AppComponent = DaggerAppComponent.factory().create(this)

    override fun getDependencies(): Dependencies {
        return appComponent
    }
}
