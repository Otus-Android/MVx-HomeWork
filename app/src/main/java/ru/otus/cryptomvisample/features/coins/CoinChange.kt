package ru.otus.cryptomvisample.features.coins

sealed interface CoinChange {
    data class CoinsLoaded(val categories: List<CoinCategoryState>) : CoinChange
    data class HighlightMoversChanged(val enabled: Boolean) : CoinChange
}