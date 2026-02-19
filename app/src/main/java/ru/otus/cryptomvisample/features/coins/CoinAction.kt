package ru.otus.cryptomvisample.features.coins

sealed interface CoinAction {

    data object LoadData : CoinAction

    data class ToggleHighlightMovers(val isEnabled: Boolean) : CoinAction

    data class ToggleFavourite(val coinId: String) : CoinAction
}
