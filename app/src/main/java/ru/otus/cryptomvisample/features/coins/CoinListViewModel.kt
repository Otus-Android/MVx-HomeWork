package ru.otus.cryptomvisample.features.coins

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
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase

private typealias Reducer = (CoinsScreenState, CoinChange) -> CoinsScreenState

class CoinListViewModel(
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsScreenState())
    val state: StateFlow<CoinsScreenState> = _state.asStateFlow()

    private val actions = Channel<CoinAction>(Channel.UNLIMITED)
    private var coinsJob: Job? = null

    fun sendAction(action: CoinAction) {
        actions.trySend(action)
    }

    private fun dispatch(action: CoinAction) {
        when (action) {
            is CoinAction.FeatureStarted -> startObservingCoins()

            is CoinAction.HighlightMoversToggled -> {
                applyChange(CoinChange.HighlightMoversChanged(action.isChecked))
            }

            is CoinAction.ToggleFavourite -> {
                val isCurrentlyFavorite = _state.value.categories.any { category ->
                    category.coins.any { it.id == action.coinId && it.isFavourite }
                }
                if (isCurrentlyFavorite) {
                    unsetFavouriteCoinUseCase(action.coinId)
                } else {
                    setFavouriteCoinUseCase(action.coinId)
                }
            }
        }
    }

    private fun startObservingCoins() {
        if (coinsJob?.isActive == true) return

        coinsJob = consumeCoinsUseCase()
            .map { categories -> categories.map(coinsStateFactory::create) }
            .onEach { categories -> applyChange(CoinChange.CoinsLoaded(categories)) }
            .launchIn(viewModelScope)
    }

    private fun applyChange(change: CoinChange) {
        _state.update { current -> reducer(current, change) }
    }

    private val reducer: Reducer = { state, change ->
        when (change) {
            is CoinChange.HighlightMoversChanged -> state.copy(highlightMovers = change.enabled)

            is CoinChange.CoinsLoaded -> state.copy(categories = change.categories)
        }
    }

    init {
        viewModelScope.launch {
            for (action in actions) dispatch(action)
        }
    }
}
