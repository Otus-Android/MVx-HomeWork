package ru.otus.cryptomvisample.coins.feature

data class CoinsScreenState(
    val categories: List<CoinCategoryState> = emptyList(),
    val highlightMovers: Boolean = false,
)

data class CoinCategoryState(
    val id: String,
    val name: String,
    val coins: List<CoinState>,
)

data class CoinState(
    val id: String,
    val name: String,
    val image: String,
    val price: String,
    val goesUp: Boolean,
    val discount: String,
    val isHotMover: Boolean,
    val isFavourite: Boolean,
    val highlight: Boolean = false,
)
