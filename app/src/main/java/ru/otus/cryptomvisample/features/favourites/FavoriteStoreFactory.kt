package ru.otus.cryptomvisample.features.favourites

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import javax.inject.Inject

@FeatureScope
class FavoriteStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val mapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) {

    fun create(): FavoriteStore =
        object : FavoriteStore, Store<FavoriteStore.Intent, FavoriteCoinsScreenState, Nothing> by storeFactory.create(
            name = "FavoriteStore",
            autoInit = false,
            initialState = FavoriteCoinsScreenState(),
            bootstrapper = SimpleBootstrapper(Action.LoadFavorites),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    private sealed interface Action {
        data object LoadFavorites : Action
    }

    private sealed interface Message {
        data class FavoritesLoaded(val coins: List<FavouriteCoinState>) : Message
    }

    private inner class ExecutorImpl : CoroutineExecutor<
        FavoriteStore.Intent,
        Action,
        FavoriteCoinsScreenState,
        Message,
        Nothing,
    >() {
        override fun executeAction(action: Action) {
            when (action) {
                Action.LoadFavorites -> loadFavorites()
            }
        }

        override fun executeIntent(intent: FavoriteStore.Intent) {
            when (intent) {
                is FavoriteStore.Intent.FavouriteRemoved -> unsetFavouriteCoinUseCase(intent.coinId)
            }
        }

        private fun loadFavorites() {
            scope.launch {
                consumeFavoriteCoinsUseCase()
                    .map { coins -> coins.map(mapper::mapToState) }
                    .collect { coins -> dispatch(Message.FavoritesLoaded(coins)) }
            }
        }
    }

    private object ReducerImpl : Reducer<FavoriteCoinsScreenState, Message> {
        override fun FavoriteCoinsScreenState.reduce(message: Message): FavoriteCoinsScreenState =
            when (message) {
                is Message.FavoritesLoaded -> copy(favoriteCoins = message.coins)
            }
    }
}
