package ru.otus.cryptomvisample.features.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase

private typealias Dispatcher = suspend (FavoriteAction) -> Unit
private typealias Reducer = (FavoriteCoinsScreenState, FavoriteChange) -> FavoriteCoinsScreenState

class FavoriteViewModel(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val mapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteCoinsScreenState())
    val state: StateFlow<FavoriteCoinsScreenState> = _state.asStateFlow()

    private val _actions = MutableSharedFlow<FavoriteAction>(
        extraBufferCapacity = 1
    )

    private val _changes = MutableSharedFlow<FavoriteChange>()

    init {
        bind()
    }

    fun sendAction(action: FavoriteAction) {
        _actions.tryEmit(action)
    }

    private fun bind() {
        _actions.onEach { action ->
            dispatcher(action)
        }.launchIn(viewModelScope)

        _changes.onEach { change ->
            _state.value = reducer(state.value, change)
        }.launchIn(viewModelScope)
    }

    private val dispatcher: Dispatcher = { action ->
        when (action) {
            is FavoriteAction.FeatureStarted -> {
                consumeFavoriteCoinsUseCase()
                    .map { favoriteCoins ->
                        favoriteCoins.map { coin ->
                            mapper.mapToState(coin)
                        }
                    }
                    .onEach { favoriteCoinsState ->
                        _changes.emit(
                            FavoriteChange.FavoriteCoinsLoaded(favoriteCoinsState)
                        )
                    }
                    .launchIn(viewModelScope)
            }

            is FavoriteAction.RemoveFavourite -> {
                unsetFavouriteCoinUseCase(action.coinId)
            }
        }
    }

    private val reducer: Reducer = { state, change ->
        when (change) {
            is FavoriteChange.FavoriteCoinsLoaded -> state.copy(
                favoriteCoins = change.favoriteCoins
            )
        }
    }
}
