package ru.otus.cryptomvisample.features.favourites.mvi

sealed interface FavouritesAction {

    object FeatureStarted : FavouritesAction
    data class RemoveFromFavourites (val coinId: String): FavouritesAction
}