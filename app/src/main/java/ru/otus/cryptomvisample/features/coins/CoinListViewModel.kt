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
import ru.otus.cryptomvisample.features.coins.Change.*

class CoinListViewModel(
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsScreenState())
    val state: StateFlow<CoinsScreenState> = _state.asStateFlow()

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

    private fun dispatcher(state: CoinsScreenState, action: Action) {
        when (action) {
            Action.FeatureStarted -> {
                consumeCoinsUseCase()
                    .map { categories ->
                        categories.map { category -> coinsStateFactory.create(category) }
                    }
                    .onEach { categoryListState ->
                        _changes.tryEmit(DataLoaded(categoryListState))
                    }
                    .catch {} //игнорю ошибку
                    .launchIn(viewModelScope)
            }

            is Action.AddToFavorite -> {
                val isFavorite = state.categories.any { coin ->
                    coin.coins.any { it.id == action.coinId && it.isFavourite }
                }
                _changes.tryEmit(FavoriteToggled(action.coinId, isFavorite))
                if (!isFavorite) {
                    setFavouriteCoinUseCase.invoke(action.coinId)
                } else {
                    unsetFavouriteCoinUseCase.invoke(action.coinId)
                }
            }

            is Action.HighlightMoversClick -> {
                _changes.tryEmit(HighlightMoversToggled(action.isClicked))
            }
        }
    }

    private fun reducer(state: CoinsScreenState, change: Change): CoinsScreenState =
        when (change) {
            is DataLoaded -> state.copy(
                categories = change.categories.map { category ->
                    category.copy(
                        coins = category.coins.map { coin ->
                            coin.copy(highlight = state.highlightMovers && coin.isHotMover)
                        }
                    )
                }
            )


            is FavoriteToggled -> {
                state.copy(
                    categories = state.categories.map { category ->
                        category.copy(
                            coins = category.coins.map { coin ->
                                if (coin.id == change.coinId) {
                                    coin.copy(isFavourite = !change.isFavorite)
                                } else {
                                    coin
                                }
                            }
                        )
                    }
                )
            }

            is HighlightMoversToggled ->
            {
                state.copy(
                    categories = state.categories.map { category ->
                        category.copy(
                            coins = category.coins.map { coin ->
                                coin.copy(highlight = change.isClicked && coin.isHotMover)

                            }
                        )

                    },
                    highlightMovers = change.isClicked
                )
            }

            else -> state
        }

    fun sendAction(action: Action) {
        _actions.tryEmit(action)
    }
}
