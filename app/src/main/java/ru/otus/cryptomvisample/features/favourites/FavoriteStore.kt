package ru.otus.cryptomvisample.features.favourites

import com.arkivanov.mvikotlin.core.store.Store

interface FavoriteStore : Store<FavoriteStore.Intent, FavoriteCoinsScreenState, Nothing> {

    sealed interface Intent {
        data class FavouriteRemoved(val coinId: String) : Intent
    }
}
