package ru.otus.cryptomvisample.features.coins

interface CoinListSideEffect {
    data class ShowError(val message: String) : CoinListSideEffect
}