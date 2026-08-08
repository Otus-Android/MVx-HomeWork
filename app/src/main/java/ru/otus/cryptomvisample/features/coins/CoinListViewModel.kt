package ru.otus.cryptomvisample.features.coins

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
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import ru.otus.cryptomvisample.features.coins.mvi.CoinsAction
import ru.otus.cryptomvisample.features.coins.mvi.CoinsChange

class CoinListViewModel(
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsScreenState())
    val state: StateFlow<CoinsScreenState> = _state.asStateFlow()

    private val actionFlow = MutableSharedFlow<CoinsAction>()

    init {
        processActions()
        dispatch(CoinsAction.LoadData)
    }

    fun dispatch(action: CoinsAction) {
        viewModelScope.launch {
            actionFlow.emit(action)
        }
    }

    fun onHighlightMoversToggled(isChecked: Boolean) {
        dispatch(CoinsAction.ClickHighlightMovers(isChecked))
    }

    fun onToggleFavourite(coinId: String) {
        dispatch(CoinsAction.ClickFavourite(coinId))
    }

    private fun processActions() {
        actionFlow
            .onEach { action ->
                when (action) {
                    is CoinsAction.LoadData -> loadCoins()
                    is CoinsAction.ClickHighlightMovers -> handleHighlightMovers(action.isEnabled)
                    is CoinsAction.ClickFavourite -> handleFavouriteClick(action.coinId)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun handleHighlightMovers(isEnabled: Boolean) {
        applyChange(CoinsChange.HighlightMoversClicked(isEnabled))
    }

    private fun handleFavouriteClick(coinId: String) {
        val isCurrentlyFavorite = _state.value.categories.any { category ->
            category.coins.any { coin -> coin.id == coinId && coin.isFavourite }
        }

        if (isCurrentlyFavorite) {
            unsetFavouriteCoinUseCase(coinId)
        } else {
            setFavouriteCoinUseCase(coinId)
        }

        applyChange(CoinsChange.FavoriteClicked(coinId, !isCurrentlyFavorite))
    }

    private fun loadCoins() {
        consumeCoinsUseCase()
            .map { categories ->
                categories.map { category -> coinsStateFactory.create(category) }
            }
            .onEach { categoryListState ->
                applyChange(CoinsChange.DataLoaded(categoryListState))
            }
            .catch {
                applyChange(CoinsChange.DataLoaded(emptyList()))
            }
            .launchIn(viewModelScope)
    }

    private fun applyChange(change: CoinsChange) {
        _state.update { currentState ->
            reduce(currentState, change)
        }
    }

    private fun reduce(currentState: CoinsScreenState, change: CoinsChange): CoinsScreenState {
        return when (change) {
            is CoinsChange.DataLoaded -> {
                currentState.copy(
                    categories = change.categories,
                    highlightMovers = currentState.highlightMovers
                )
            }
            is CoinsChange.FavoriteClicked -> {
                val updatedCategories = currentState.categories.map { category ->
                    category.copy(
                        coins = category.coins.map { coin ->
                            if (coin.id == change.coinId) {
                                coin.copy(isFavourite = change.isFavorite)
                            } else {
                                coin
                            }
                        }
                    )
                }
                currentState.copy(categories = updatedCategories)
            }
            is CoinsChange.HighlightMoversClicked -> {
                val highlightMovers = change.isClicked
                val updatedCategories = currentState.categories.map { category ->
                    category.copy(
                        coins = category.coins.map { coin ->
                            coin.copy(
                                highlight = highlightMovers && coin.isHotMover
                            )
                        }
                    )
                }
                currentState.copy(
                    categories = updatedCategories,
                    highlightMovers = highlightMovers
                )
            }
        }
    }
}
