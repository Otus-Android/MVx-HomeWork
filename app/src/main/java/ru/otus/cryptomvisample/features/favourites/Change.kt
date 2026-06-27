package ru.otus.cryptomvisample.features.favourites


interface Change {
    data class DataLoaded (val favoriteCoins: List<FavouriteCoinState>): Change
    data class RemoveFromFavourites(val coinId: String) : Change
}