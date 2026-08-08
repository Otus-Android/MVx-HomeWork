package ru.otus.cryptomvisample.features.coins.mvi

import ru.otus.cryptomvisample.features.coins.CoinCategoryState

sealed interface CoinsChange {

    data class DataLoaded(val categories: List<CoinCategoryState>) : CoinsChange
    data class FavoriteClicked(val coinId: String, val isFavorite: Boolean) : CoinsChange
    data class HighlightMoversClicked(val isClicked: Boolean) : CoinsChange
}