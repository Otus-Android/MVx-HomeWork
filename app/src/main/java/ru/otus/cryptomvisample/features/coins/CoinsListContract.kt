package ru.otus.cryptomvisample.features.coins

import ru.otus.cryptomvisample.features.mvi.BaseViewModelConfiguration

interface CoinsListContract {
    data class State(
        val categories: List<UiCoinCategory> = emptyList(),
        val highlightMovers: Boolean = false,
    ): BaseViewModelConfiguration.State

    sealed interface Intent: BaseViewModelConfiguration.Intent{
        data class OnHighlightMoversToggled(val toggled: Boolean): Intent

        data class OnToggleFavourite(val id: String): Intent
    }

    sealed interface Effect: BaseViewModelConfiguration.Effect
}