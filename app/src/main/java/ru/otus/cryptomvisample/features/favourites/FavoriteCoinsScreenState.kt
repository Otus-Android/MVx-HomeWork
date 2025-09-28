package ru.otus.cryptomvisample.features.favourites

data class FavoriteCoinsScreenState(
    val favoriteCoins: List<FavouriteCoinState> = emptyList(),
)

data class FavouriteCoinState(
    val id: String,
    val name: String,
    val image: String,
    val price: String,
    val isPriceGoesUp: Boolean,
    val priceChange: String,
)
