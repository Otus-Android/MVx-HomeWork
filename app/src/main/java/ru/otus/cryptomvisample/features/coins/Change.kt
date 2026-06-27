package ru.otus.cryptomvisample.features.coins

interface Change {
    data class DataLoaded (val categories: List<CoinCategoryState>): Change
    data class FavoriteToggled(val coinId: String, val isFavorite: Boolean) : Change
    data class HighlightMoversToggled(val isClicked: Boolean) : Change
}