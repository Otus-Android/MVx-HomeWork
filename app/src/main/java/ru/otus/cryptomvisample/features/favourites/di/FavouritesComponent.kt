package ru.otus.cryptomvisample.features.favourites.di

import dagger.Component
import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.features.favourites.FavoriteViewModelFactory

@FeatureScope
@Component(dependencies = [FavouritesDependencies::class])
interface FavouritesComponent {

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: FavouritesDependencies,
        ): FavouritesComponent
    }

    fun viewModelFactory(): FavoriteViewModelFactory
}
