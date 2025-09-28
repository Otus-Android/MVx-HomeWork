package ru.otus.cryptomvisample.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.otus.cryptomvisample.features.coins.di.CoinListDependencies
import ru.otus.cryptomvisample.features.favourites.di.FavouritesDependencies
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DataModule::class,
        DomainModule::class,
    ]
)
interface AppComponent :
    CoinListDependencies,
    FavouritesDependencies {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }
}