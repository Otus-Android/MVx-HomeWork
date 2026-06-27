package ru.otus.cryptomvisample.features.favourites

sealed interface Action {
    object FeatureStarted : Action
    data class RemoveFromFavourites (val coinId: String): Action
}