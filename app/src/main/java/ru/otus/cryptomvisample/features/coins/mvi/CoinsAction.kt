package ru.otus.cryptomvisample.features.coins.mvi

sealed interface CoinsAction {

    data object LoadData : CoinsAction

    data class ClickHighlightMovers(val isEnabled: Boolean) : CoinsAction

    data class ClickFavourite(val coinId: String) : CoinsAction
}