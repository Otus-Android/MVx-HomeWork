package ru.otus.cryptomvisample.features.coins

sealed interface Action {
    object FeatureStarted : Action
    data class AddToFavorite (val coinId: String): Action
    data class HighlightMoversClick(val isClicked: Boolean): Action
}