package ru.otus.cryptomvisample.features.favourites

data class UiFavouriteCoin(
    val id: String,
    val name: String,
    val image: String,
    val price: String,
    val isPriceGoesUp: Boolean,
    val priceChange: String,
)