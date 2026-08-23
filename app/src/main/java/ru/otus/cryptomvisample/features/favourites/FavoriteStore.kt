package ru.otus.cryptomvisample.features.favourites

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.common.domain_api.Coin
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import ru.otus.cryptomvisample.features.favourites.FavoriteStore.Intent
import ru.otus.cryptomvisample.features.favourites.FavoriteStore.State
import javax.inject.Inject

interface FavoriteStore : Store<Intent, State, Nothing> {

    sealed interface Intent {
        data class RemoveFavourite(val coinId: String) : Intent
    }

    data class State(
        val favoriteCoins: List<CoinItem> = emptyList(),
    ) {
        data class CoinItem(
            val id: String,
            val name: String,
            val image: String,
            val price: String,
            val isPriceGoesUp: Boolean,
            val priceChange: String,
        )
    }
}

@FeatureScope
class FavoriteStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val favoriteStateMapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) {

    fun create(): FavoriteStore =
        object : FavoriteStore,
            Store<Intent, State, Nothing> by storeFactory.create(
                name = "FavoriteStore",
                initialState = State(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl,
            ) {}

    private sealed interface Action {
        data class FavoriteCoinsChanged(val coins: List<Coin>) : Action
    }

    private sealed interface Message {
        data class FavoriteCoinsChanged(val coins: List<State.CoinItem>) : Message
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                consumeFavoriteCoinsUseCase().collect { coins ->
                    dispatch(Action.FavoriteCoinsChanged(coins))
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Nothing>() {

        override fun executeAction(action: Action) {
            when (action) {
                is Action.FavoriteCoinsChanged -> {
                    dispatch(
                        Message.FavoriteCoinsChanged(
                            action.coins.map(favoriteStateMapper::mapToState)
                        )
                    )
                }
            }
        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.RemoveFavourite -> {
                    unsetFavouriteCoinUseCase(intent.coinId)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            is Message.FavoriteCoinsChanged -> copy(favoriteCoins = msg.coins)
        }
    }
}
