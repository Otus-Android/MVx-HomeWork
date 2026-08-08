package ru.otus.cryptomvisample.features.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import ru.otus.cryptomvisample.features.favourites.mvi.FavouritesAction
import ru.otus.cryptomvisample.features.favourites.mvi.FavouritesChange


class FavoriteViewModel(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val mapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteCoinsScreenState())
    val state: StateFlow<FavoriteCoinsScreenState> = _state.asStateFlow()

    private val actionFlow = MutableSharedFlow<FavouritesAction>()

    init {
        processActions()
        dispatch(FavouritesAction.FeatureStarted)
    }

    fun dispatch(action: FavouritesAction) {
        viewModelScope.launch {
            actionFlow.emit(action)
        }
    }

    fun removeFavourite(coinId: String) {
        dispatch(FavouritesAction.RemoveFromFavourites(coinId))
    }

    private fun processActions() {
        actionFlow
            .onEach { action ->
                when (action) {
                    is FavouritesAction.FeatureStarted -> loadFavoriteCoins()
                    is FavouritesAction.RemoveFromFavourites -> handleRemoveFromFavourites(action.coinId)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun handleRemoveFromFavourites(coinId: String) {
        unsetFavouriteCoinUseCase(coinId)
        applyChange(FavouritesChange.RemoveFromFavourites(coinId))
    }

    private fun loadFavoriteCoins() {
        consumeFavoriteCoinsUseCase()
            .map { favoriteCoins ->
                favoriteCoins.map { coin ->
                    mapper.mapToState(coin)
                }
            }
            .onEach { favoriteCoinsState ->
                applyChange(FavouritesChange.DataLoaded(favoriteCoinsState))
            }
            .catch {
                applyChange(FavouritesChange.DataLoaded(emptyList()))
            }
            .launchIn(viewModelScope)
    }

    private fun applyChange(change: FavouritesChange) {
        _state.update { currentState ->
            reduce(currentState, change)
        }
    }

    private fun reduce(
        currentState: FavoriteCoinsScreenState,
        change: FavouritesChange
    ): FavoriteCoinsScreenState {
        return when (change) {
            is FavouritesChange.DataLoaded -> {
                currentState.copy(
                    favoriteCoins = change.favoriteCoins
                )
            }
            is FavouritesChange.RemoveFromFavourites -> {
                val updatedCoins = currentState.favoriteCoins.filter { coin ->
                    coin.id != change.coinId
                }
                currentState.copy(
                    favoriteCoins = updatedCoins
                )
            }
        }
    }
}
