package ru.otus.cryptomvisample.features.favourites

sealed interface FavouriteAction {

    object LoadData: FavouriteAction

    data class RemoveFavourite(val coinId: String): FavouriteAction
}