package ru.otus.cryptomvisample.features.coins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase

private typealias Dispatcher = suspend (CoinAction) -> Unit
private typealias Reducer = (CoinsScreenState, CoinChange) -> CoinsScreenState

class CoinListViewModel(
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsScreenState())
    val state: StateFlow<CoinsScreenState> = _state.asStateFlow()

    private val _actions = MutableSharedFlow<CoinAction>(
        extraBufferCapacity = 1
    )
    private val _changes = MutableSharedFlow<CoinChange>()

    private var fullCategories: List<CoinCategoryState> = emptyList()

    init {
        bind()
    }

    fun sendAction(action: CoinAction) {
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
            is CoinAction.FeatureStarted -> {
                consumeCoinsUseCase()
                    .map { categories ->
                        categories.map { category -> coinsStateFactory.create(category) }
                    }
                    .onEach { categories ->
                        fullCategories = categories
                        _changes.emit(CoinChange.CoinsLoaded(categories))
                    }
                    .launchIn(viewModelScope)
            }

            is CoinAction.HighlightMoversToggled -> {
                _changes.emit(
                    CoinChange.HighlightMoversChanged(action.isChecked)
                )
            }

            is CoinAction.ToggleFavourite -> {
                val isCurrentlyFavorite = fullCategories.any { category ->
                    category.coins.any { coin -> coin.id == action.coinId && coin.isFavourite }
                }

                if (isCurrentlyFavorite) {
                    unsetFavouriteCoinUseCase(action.coinId)
                } else {
                    setFavouriteCoinUseCase(action.coinId)
                }
            }
        }
    }

    private val reducer: Reducer = { state, change ->
        when (change) {
            is CoinChange.HighlightMoversChanged -> state.copy(
                highlightMovers = change.enabled,
                categories = fullCategories.map { category ->
                    category.copy(coins = category.coins.map { coin ->
                        coin.copy(
                            highlight = change.enabled && coin.isHotMover
                        )
                    })
                }
            )

            is CoinChange.CoinsLoaded -> state.copy(
                categories = fullCategories.map { category ->
                    category.copy(coins = category.coins.map { coin ->
                        coin.copy(
                            highlight = state.highlightMovers && coin.isHotMover
                        )
                    })
                }
            )
        }
    }
}
