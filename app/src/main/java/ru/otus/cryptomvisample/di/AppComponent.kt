package ru.otus.cryptomvisample.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.otus.cryptomvisample.coins.feature.di.CoinListComponentDependencies
import ru.otus.common.di.Dependencies
import javax.inject.Singleton

@Singleton
@Component(modules = [])
interface AppComponent:
    Dependencies,
    CoinListComponentDependencies
{
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }
}