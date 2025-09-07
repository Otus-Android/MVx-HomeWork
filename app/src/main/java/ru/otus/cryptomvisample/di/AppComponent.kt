package ru.otus.cryptosample.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.otus.cryptosample.coins.feature.di.CoinListComponentDependencies
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