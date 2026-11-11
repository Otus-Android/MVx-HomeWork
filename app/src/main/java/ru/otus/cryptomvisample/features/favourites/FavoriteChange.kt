package ru.otus.cryptomvisample.features.favourites

sealed interface FavoriteChange {
    data class FavoriteCoinsLoaded(val favoriteCoins: List<FavouriteCoinState>) : FavoriteChange
}