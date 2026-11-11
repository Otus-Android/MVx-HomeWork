package ru.otus.cryptomvisample.features.coins

sealed interface CoinAction {
    object FeatureStarted : CoinAction
    data class HighlightMoversToggled(val isChecked: Boolean) : CoinAction
    data class ToggleFavourite(val coinId: String) : CoinAction
}