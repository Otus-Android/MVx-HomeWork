package ru.otus.cryptosample.coins.data

import kotlinx.serialization.Serializable

@Serializable
data class CoinsEntity(
    val coins: List<CoinEntity>,
    val categories: List<CategoryEntity>,
)

@Serializable
data class CoinEntity (
    val id: String,
    val name: String,
    val ticker: String,
    val price: Double,
    val change24h: Double,
    val iconPath: String,
    val category: String
)

@Serializable
data class CategoryEntity (
    val id: String,
    val name: String,
    val order: Int,
)