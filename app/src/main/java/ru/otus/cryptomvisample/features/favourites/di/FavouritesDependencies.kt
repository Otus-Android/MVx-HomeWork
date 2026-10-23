package ru.otus.cryptomvisample.features.favourites.di

import ru.otus.common.di.Dependencies
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase

interface FavouritesDependencies: Dependencies {
    fun favoriteStoreFactory(): StoreFactory
    fun consumeFavoriteCoinsUseCase(): ConsumeFavoriteCoinsUseCase
    fun unsetFavouriteCoinUseCase(): UnsetFavouriteCoinUseCase
}
