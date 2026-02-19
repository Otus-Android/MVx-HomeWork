package ru.otus.cryptomvisample.features.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.util.Dispatcher
import ru.otus.cryptomvisample.common.util.Reducer


class FavoriteViewModel(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val mapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _actions = MutableSharedFlow<FavouriteAction>(extraBufferCapacity = 1)
    private val _changes = MutableSharedFlow<FavouriteChange>(
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val _state = MutableStateFlow(FavoriteCoinsScreenState())
    val state: StateFlow<FavoriteCoinsScreenState> = _state.asStateFlow()

    private val dispatcher: Dispatcher<FavoriteCoinsScreenState, FavouriteAction> = { state, action ->
        when (action) {
            FavouriteAction.LoadData -> {
                consumeFavoriteCoinsUseCase()
                    .map { favoriteCoins ->
                        favoriteCoins.map { coin ->
                            mapper.mapToState(coin)
                        }
                    }
                    .onEach { favoriteCoinsState ->
                        _changes.tryEmit(FavouriteChange.DataUpdated(coins = favoriteCoinsState))
                    }
                    .catch {
                        _changes.tryEmit(FavouriteChange.DataUpdated(coins = emptyList()))
                    }
                    .launchIn(viewModelScope)
            }
            is FavouriteAction.RemoveFavourite -> unsetFavouriteCoinUseCase(coinId = action.coinId)
        }
    }

    private val reducer: Reducer<FavoriteCoinsScreenState, FavouriteChange> = { state, change ->
        when (change) {
            is FavouriteChange.DataUpdated -> state.copy(favoriteCoins = change.coins)
        }
    }

    init {
        bind()
        sendAction(FavouriteAction.LoadData)
    }

    private fun bind() {
        _actions.onEach { action -> dispatcher(state.value, action) }.launchIn(viewModelScope)
        _changes.onEach { change -> _state.value = reducer(state.value, change) }.launchIn(viewModelScope)
    }

    fun sendAction(action: FavouriteAction) {
        _actions.tryEmit(action)
    }

    fun removeFavourite(coinId: String) {
        unsetFavouriteCoinUseCase(coinId)
    }
}
