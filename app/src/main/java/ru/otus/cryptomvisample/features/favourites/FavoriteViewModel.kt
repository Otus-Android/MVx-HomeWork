package ru.otus.cryptomvisample.features.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase

private typealias Reducer = (FavoriteCoinsScreenState, FavoriteChange) -> FavoriteCoinsScreenState

class FavoriteViewModel(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val mapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteCoinsScreenState())
    val state: StateFlow<FavoriteCoinsScreenState> = _state.asStateFlow()

    private val actions = Channel<FavoriteAction>(Channel.UNLIMITED)

    private var favoritesJob: Job? = null

    fun sendAction(action: FavoriteAction) {
        actions.trySend(action)
    }

    private fun dispatch(action: FavoriteAction) {
        when (action) {
            is FavoriteAction.FeatureStarted -> startObservingFavorites()
            is FavoriteAction.RemoveFavourite -> unsetFavouriteCoinUseCase(action.coinId)
        }
    }

    private fun startObservingFavorites() {
        if (favoritesJob?.isActive == true) return

        favoritesJob = consumeFavoriteCoinsUseCase()
            .map { coins -> coins.map(mapper::mapToState) }
            .onEach { coins -> applyChange(FavoriteChange.FavoriteCoinsLoaded(coins)) }
            .launchIn(viewModelScope)
    }

    private fun applyChange(change: FavoriteChange) {
        _state.update { current -> reducer(current, change) }
    }

    private val reducer: Reducer = { state, change ->
        when (change) {
            is FavoriteChange.FavoriteCoinsLoaded -> state.copy(
                favoriteCoins = change.favoriteCoins
            )
        }
    }

    init {
        viewModelScope.launch {
            for (action in actions) dispatch(action)
        }
    }
}
