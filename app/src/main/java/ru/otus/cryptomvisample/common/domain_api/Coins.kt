package ru.otus.cryptomvisample.common.domain_api

data class Coins(
    val coins: List<Coin>,
    val categories: List<Category>,
)

data class Coin (
    val id: String,
    val name: String,
    val ticker: String,
    val price: Double,
    val change24h: Double,
    val iconPath: String,
    val category: String,
    val isFavourite: Boolean = false
)

data class Category(
    val id: String,
    val name: String,
    val order: Int,
)
