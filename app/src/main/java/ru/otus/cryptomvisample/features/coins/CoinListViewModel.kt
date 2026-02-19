package ru.otus.cryptomvisample.features.coins

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
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.util.Dispatcher
import ru.otus.cryptomvisample.common.util.Reducer

class CoinListViewModel(
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _actions = MutableSharedFlow<CoinAction>(extraBufferCapacity = 1)
    private val _changes = MutableSharedFlow<CoinChange>(
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val _state = MutableStateFlow(CoinsScreenState())
    val state: StateFlow<CoinsScreenState> = _state.asStateFlow()

    private val dispatcher: Dispatcher<CoinsScreenState, CoinAction> = { state, action ->
        when (action) {
            CoinAction.LoadData -> {
                consumeCoinsUseCase()
                    .map { categories ->
                        categories.map { category -> coinsStateFactory.create(category) }
                    }
                    .onEach { categoryListState ->
                        _changes.tryEmit(CoinChange.DataUpdated(categoryListState))
                    }
                    .catch {
                        _changes.tryEmit(CoinChange.DataUpdated(emptyList()))
                    }
                    .launchIn(viewModelScope)
            }

            is CoinAction.ToggleHighlightMovers -> {
                _changes.tryEmit(CoinChange.HighlightToggled(action.isEnabled))
            }

            is CoinAction.ToggleFavourite -> {
                val isCurrentlyFavourite = state.categories.any { category ->
                    category.coins.any { coin -> coin.id == action.coinId && coin.isFavourite }
                }
                if (isCurrentlyFavourite) {
                    unsetFavouriteCoinUseCase(coinId = action.coinId)
                } else {
                    setFavouriteCoinUseCase(coinId = action.coinId)
                }
            }
        }
    }

    private val reducer: Reducer<CoinsScreenState, CoinChange> = { state, change ->
        when (change) {
            is CoinChange.DataUpdated -> {
                state.copy(categories = applyHighlight(change.categories, state.highlightMovers))
            }
            is CoinChange.HighlightToggled -> {
                state.copy(
                    highlightMovers = change.isEnabled,
                    categories = applyHighlight(state.categories, change.isEnabled)
                )
            }
        }
    }

    init {
        bind()
        sendAction(CoinAction.LoadData)
    }

    private fun bind() {
        _actions.onEach { action -> dispatcher(state.value, action) }.launchIn(viewModelScope)
        _changes.onEach { change -> _state.value = reducer(state.value, change) }.launchIn(viewModelScope)
    }

    fun sendAction(action: CoinAction) {
        _actions.tryEmit(action)
    }

    private fun applyHighlight(
        categories: List<CoinCategoryState>,
        highlightEnabled: Boolean
    ): List<CoinCategoryState> {
        return categories.map { category ->
            category.copy(coins = category.coins.map { coin ->
                coin.copy(highlight = highlightEnabled && coin.isHotMover)
            })
        }
    }
}
