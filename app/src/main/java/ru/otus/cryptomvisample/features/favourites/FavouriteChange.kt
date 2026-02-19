package ru.otus.cryptomvisample.features.favourites

internal sealed interface FavouriteChange {

    data class DataUpdated(val coins: List<FavouriteCoinState>): FavouriteChange
}