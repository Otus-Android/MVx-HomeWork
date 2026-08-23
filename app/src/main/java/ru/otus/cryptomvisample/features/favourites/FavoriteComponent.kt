package ru.otus.cryptomvisample.features.favourites

import kotlinx.coroutines.flow.StateFlow

interface FavoriteComponent {

    val model: StateFlow<FavoriteStore.State>

    fun onRemoveFavourite(coinId: String)
}
