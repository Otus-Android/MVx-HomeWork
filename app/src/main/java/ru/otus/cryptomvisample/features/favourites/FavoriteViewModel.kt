package ru.otus.cryptomvisample.features.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import ru.otus.cryptomvisample.features.favourites.Change.DataLoaded
import kotlin.collections.map


class FavoriteViewModel(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val mapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteCoinsScreenState())
    val state: StateFlow<FavoriteCoinsScreenState> = _state.asStateFlow()

    private val _actions = MutableSharedFlow<Action>(extraBufferCapacity = 1)
    private val _changes = MutableSharedFlow<Change>(
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private fun bind() {
        _actions.onEach { action -> dispatcher(state.value, action) }.launchIn(viewModelScope)
        _changes.onEach { change -> _state.value = reducer(state.value, change) }
            .launchIn(viewModelScope)
    }

    init {
        bind()
        sendAction(Action.FeatureStarted)
    }

    fun removeFavourite(coinId: String) {
        unsetFavouriteCoinUseCase(coinId)
    }

    private fun dispatcher(state: FavoriteCoinsScreenState, action: Action) {
        when (action) {
            Action.FeatureStarted -> {
                consumeFavoriteCoinsUseCase()
                    .map { favoriteCoins ->
                        favoriteCoins.map { coin ->
                            mapper.mapToState(coin)
                        }
                    }
                    .onEach { favoriteCoinsState ->
                        _changes.tryEmit(DataLoaded(favoriteCoinsState))
                    }
                    .launchIn(viewModelScope)
            }

            is Action.RemoveFromFavourites -> {
                unsetFavouriteCoinUseCase(action.coinId)
                _changes.tryEmit(Change.RemoveFromFavourites(action.coinId))
        }
    }
}

    private fun reducer(state: FavoriteCoinsScreenState, change: Change): FavoriteCoinsScreenState =
        when (change) {
            is DataLoaded -> state.copy(
                favoriteCoins = change.favoriteCoins
            )

            is Change.RemoveFromFavourites -> {
                state.copy(
                    favoriteCoins = state.favoriteCoins.filter { it.id == change.coinId }
                )

            }

            else -> state
        }

    fun sendAction(action: Action) {
        _actions.tryEmit(action)
    }

}
