package ru.otus.cryptomvisample.features.favourites

import ru.otus.cryptomvisample.features.mvi.BaseViewModelConfiguration

interface FavoriteContract {
    data class State(val coins: List<UiFavouriteCoin> = emptyList()) :
        BaseViewModelConfiguration.State

    sealed interface Intent : BaseViewModelConfiguration.Intent {
        data class RemoveFavoriteCoin(val id: String) : Intent
    }

    sealed interface Effect : BaseViewModelConfiguration.Effect
}