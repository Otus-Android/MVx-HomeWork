package ru.otus.cryptomvisample.features.coins

internal sealed interface CoinChange {

    data class DataUpdated(val categories: List<CoinCategoryState>) : CoinChange

    data class HighlightToggled(val isEnabled: Boolean) : CoinChange
}
