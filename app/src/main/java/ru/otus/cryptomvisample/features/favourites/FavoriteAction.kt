package ru.otus.cryptomvisample.features.favourites

sealed interface FavoriteAction {
    object FeatureStarted : FavoriteAction
    data class RemoveFavourite(val coinId: String) : FavoriteAction
}