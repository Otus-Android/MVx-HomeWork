package ru.otus.cryptomvisample.features.coins

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
    val isPriceGoesUp: Boolean,
    val priceChange: String,
    val isHotMover: Boolean,
    val isFavourite: Boolean,
    val highlight: Boolean = false,
)
