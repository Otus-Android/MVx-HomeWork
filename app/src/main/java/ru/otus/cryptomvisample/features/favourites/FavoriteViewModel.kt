package ru.otus.cryptomvisample.features.favourites

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
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase

internal interface FavoriteStore : Store<FavoriteStore.Intent, FavoriteCoinsScreenState, Nothing> {
    sealed interface Intent {
        data class RemoveFavourite(val coinId: String) : Intent
    }
}

class FavoriteViewModel(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val mapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val store = FavoriteStoreFactory(
        DefaultStoreFactory(),
        consumeFavoriteCoinsUseCase,
        mapper,
        unsetFavouriteCoinUseCase
    ).create()

    private val _state = MutableStateFlow(store.state)
    val state: StateFlow<FavoriteCoinsScreenState> = _state.asStateFlow()

    init {
        store.states
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        store.dispose()
        super.onCleared()
    }

    fun removeFavourite(coinId: String) {
        store.accept(FavoriteStore.Intent.RemoveFavourite(coinId))
    }
}

private class FavoriteStoreFactory(
    private val storeFactory: StoreFactory,
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val mapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) {
    fun create(): FavoriteStore =
        object : FavoriteStore, Store<FavoriteStore.Intent, FavoriteCoinsScreenState, Nothing> by storeFactory.create(
            name = "FavoriteStore",
            initialState = FavoriteCoinsScreenState(),
            bootstrapper = SimpleBootstrapper(Action.LoadFavorites),
            executorFactory = ::ExecutorImpl,
            reducer = { msg ->
                when (msg) {
                    is Message.FavoritesLoaded -> copy(favoriteCoins = msg.coins)
                }
            }
        ) {}

    private sealed interface Action {
        object LoadFavorites : Action
    }

    private sealed interface Message {
        data class FavoritesLoaded(val coins: List<FavouriteCoinState>) : Message
    }

    private inner class ExecutorImpl : Executor<FavoriteStore.Intent, Action, FavoriteCoinsScreenState, Message, Nothing> {
        private lateinit var callbacks: Executor.Callbacks<FavoriteCoinsScreenState, Message, Nothing>
        private val scope = CoroutineScope(Dispatchers.Main)

        override fun init(callbacks: Executor.Callbacks<FavoriteCoinsScreenState, Message, Nothing>) {
            this.callbacks = callbacks
        }

        override fun executeAction(action: Action) {
            when (action) {
                is Action.LoadFavorites -> {
                    scope.launch {
                        consumeFavoriteCoinsUseCase().collect { favoriteCoins ->
                            val favoriteCoinsState = favoriteCoins.map { coin -> mapper.mapToState(coin) }
                            callbacks.onMessage(Message.FavoritesLoaded(favoriteCoinsState))
                        }
                    }
                }
            }
        }

        override fun executeIntent(intent: FavoriteStore.Intent) {
            when (intent) {
                is FavoriteStore.Intent.RemoveFavourite -> {
                    unsetFavouriteCoinUseCase(intent.coinId)
                }
            }
        }

        override fun dispose() {
            scope.cancel()
        }
    }
}
