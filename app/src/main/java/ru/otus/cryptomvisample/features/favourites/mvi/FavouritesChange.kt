package ru.otus.cryptomvisample.features.favourites.mvi

import ru.otus.cryptomvisample.features.favourites.FavouriteCoinState

sealed interface FavouritesChange {

    data class DataLoaded (val favoriteCoins: List<FavouriteCoinState>): FavouritesChange
    data class RemoveFromFavourites(val coinId: String) : FavouritesChange
}