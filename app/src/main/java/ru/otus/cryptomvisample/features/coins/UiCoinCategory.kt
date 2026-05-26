package ru.otus.cryptomvisample.features.coins

data class UiCoinCategory(
    val id: String,
    val name: String,
    val coins: List<UiCoinState>,
)

data class UiCoinState(
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