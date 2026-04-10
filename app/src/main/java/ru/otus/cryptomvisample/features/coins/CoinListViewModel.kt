package ru.otus.cryptomvisample.features.coins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.states
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase

internal interface CoinListStore : Store<CoinListStore.Intent, CoinsScreenState, Nothing> {
    sealed interface Intent {
        data class ToggleHighlightMovers(val isChecked: Boolean) : Intent
        data class ToggleFavourite(val coinId: String) : Intent
    }
}

class CoinListViewModel(
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val store = CoinListStoreFactory(
        DefaultStoreFactory(),
        consumeCoinsUseCase,
        coinsStateFactory,
        setFavouriteCoinUseCase,
        unsetFavouriteCoinUseCase
    ).create()

    private val _state = MutableStateFlow(store.state)
    val state: StateFlow<CoinsScreenState> = _state.asStateFlow()

    init {
        store.states
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }

    fun onHighlightMoversToggled(isChecked: Boolean) {
        store.accept(CoinListStore.Intent.ToggleHighlightMovers(isChecked))
    }

    fun onToggleFavourite(coinId: String) {
        store.accept(CoinListStore.Intent.ToggleFavourite(coinId))
    }
}

private class CoinListStoreFactory(
    private val storeFactory: StoreFactory,
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) {
    fun create(): CoinListStore =
        object : CoinListStore, Store<CoinListStore.Intent, CoinsScreenState, Nothing> by storeFactory.create(
            name = "CoinListStore",
            initialState = CoinsScreenState(),
            bootstrapper = SimpleBootstrapper(Action.LoadCoins),
            executorFactory = ::ExecutorImpl,
            reducer = { msg ->
                when (msg) {
                    is Message.CoinsLoaded -> {
                        val processed = processCategories(msg.categories, highlightMovers)
                        copy(
                            fullCategories = msg.categories,
                            categories = processed
                        )
                    }
                    is Message.HighlightMoversToggled -> {
                        val processed = processCategories(fullCategories, msg.isChecked)
                        copy(
                            highlightMovers = msg.isChecked,
                            categories = processed
                        )
                    }
                }
            }
        ) {}

    private sealed interface Action {
        object LoadCoins : Action
    }

    private sealed interface Message {
        data class CoinsLoaded(val categories: List<CoinCategoryState>) : Message
        data class HighlightMoversToggled(val isChecked: Boolean) : Message
    }

    private fun processCategories(categories: List<CoinCategoryState>, highlightMovers: Boolean): List<CoinCategoryState> {
        return categories.map { category ->
            category.copy(coins = category.coins.map { coin ->
                coin.copy(
                    highlight = highlightMovers && coin.isHotMover
                )
            })
        }
    }

    private inner class ExecutorImpl : Executor<CoinListStore.Intent, Action, CoinsScreenState, Message, Nothing> {
        private lateinit var callbacks: Executor.Callbacks<CoinsScreenState, Message, Nothing>
        private val scope = CoroutineScope(Dispatchers.Main)

        override fun init(callbacks: Executor.Callbacks<CoinsScreenState, Message, Nothing>) {
            this.callbacks = callbacks
        }

        override fun executeAction(action: Action) {
            when (action) {
                is Action.LoadCoins -> {
                    scope.launch {
                        try {
                            consumeCoinsUseCase().collect { categories ->
                                val categoryListState = categories.map { category -> coinsStateFactory.create(category) }
                                callbacks.onMessage(Message.CoinsLoaded(categoryListState))
                            }
                        } catch (_: Exception) {
                            callbacks.onMessage(Message.CoinsLoaded(emptyList()))
                        }
                    }
                }
            }
        }

        override fun executeIntent(intent: CoinListStore.Intent) {
            when (intent) {
                is CoinListStore.Intent.ToggleHighlightMovers -> {
                    callbacks.onMessage(Message.HighlightMoversToggled(intent.isChecked))
                }
                is CoinListStore.Intent.ToggleFavourite -> {
                    val isCurrentlyFavorite = callbacks.state.fullCategories.any { category ->
                        category.coins.any { coin -> coin.id == intent.coinId && coin.isFavourite }
                    }
                    
                    if (isCurrentlyFavorite) {
                        unsetFavouriteCoinUseCase(intent.coinId)
                    } else {
                        setFavouriteCoinUseCase(intent.coinId)
                    }
                }
            }
        }

        override fun dispose() {
            scope.cancel()
        }
    }
}
