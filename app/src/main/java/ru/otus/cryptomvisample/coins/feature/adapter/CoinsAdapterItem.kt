package ru.otus.cryptomvisample.coins.feature.adapter

import ru.otus.cryptomvisample.coins.feature.CoinState

sealed class CoinsAdapterItem {
    data class CategoryHeader(val categoryName: String) : CoinsAdapterItem()
    data class CoinItem(val coin: CoinState) : CoinsAdapterItem()
    data class HorizontalCoinList(val coins: List<CoinState>) : CoinsAdapterItem()
}