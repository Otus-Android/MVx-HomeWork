package ru.otus.cryptomvisample.features.coins

import com.arkivanov.mvikotlin.core.store.Store

interface CoinListStore : Store<CoinListStore.Intent, CoinsScreenState, Nothing> {

    sealed interface Intent {
        data class HighlightMoversToggled(val isChecked: Boolean) : Intent
        data class FavouriteToggled(val coinId: String) : Intent
    }
}
