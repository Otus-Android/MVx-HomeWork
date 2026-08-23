package ru.otus.cryptomvisample.features.coins

import kotlinx.coroutines.flow.StateFlow

interface CoinComponent {

    val model: StateFlow<CoinStore.State>

    fun onHighlightMoversToggled(isChecked: Boolean)

    fun onToggleFavourite(coinId: String)
}
