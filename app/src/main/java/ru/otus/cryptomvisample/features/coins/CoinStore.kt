package ru.otus.cryptomvisample.features.coins

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.common.domain_api.CoinGroup
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import ru.otus.cryptomvisample.features.coins.CoinStore.Intent
import ru.otus.cryptomvisample.features.coins.CoinStore.State
import javax.inject.Inject

interface CoinStore : Store<Intent, State, Nothing> {

    sealed interface Intent {
        data class HighlightMoversToggled(val isChecked: Boolean) : Intent
        data class ToggleFavourite(val coinId: String) : Intent
    }

    data class State(
        val categories: List<CategoryItem> = emptyList(),
        val highlightMovers: Boolean = false,
    ) {
        data class CategoryItem(
            val id: String,
            val name: String,
            val coins: List<CoinItem>
        )

        data class CoinItem(
            val id: String,
            val name: String,
            val image: String,
            val price: String,
            val isPriceGoesUp: Boolean,
            val priceChange: String,
            val isHotMover: Boolean,
            val isFavourite: Boolean,
            val highlight: Boolean = false
        )
    }
}

@FeatureScope
class CoinStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) {

    fun create(): CoinStore =
        object : CoinStore,
            Store<Intent, State, Nothing> by storeFactory.create(
                name = "CoinStore",
                initialState = State(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl,
            ) {}

    private sealed interface Action {
        data class CoinsChanged(val coinGroups: List<CoinGroup>) : Action
        data object CoinsLoadingFailed : Action
    }

    private sealed interface Message {
        data class CoinsChanged(val categories: List<State.CategoryItem>) : Message
        data class HighlightMoversChanged(val isChecked: Boolean) : Message
        data object CoinsLoadingFailed : Message
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                consumeCoinsUseCase()
                    .catch { dispatch(Action.CoinsLoadingFailed) }
                    .collect { coinGroups ->
                        dispatch(Action.CoinsChanged(coinGroups))
                    }
            }
        }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<Intent, Action, State, Message, Nothing>() {

        override fun executeAction(action: Action) {
            when (action) {
                is Action.CoinsChanged -> {
                    dispatch(
                        Message.CoinsChanged(
                            action.coinGroups.map(coinsStateFactory::create)
                        )
                    )
                }

                Action.CoinsLoadingFailed -> dispatch(Message.CoinsLoadingFailed)
            }
        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.HighlightMoversToggled -> {
                    dispatch(Message.HighlightMoversChanged(intent.isChecked))
                }

                is Intent.ToggleFavourite -> toggleFavourite(intent.coinId)
            }
        }

        private fun toggleFavourite(coinId: String) {
            val isCurrentlyFavourite = state().categories.any { category ->
                category.coins.any { coin -> coin.id == coinId && coin.isFavourite }
            }

            if (isCurrentlyFavourite) {
                unsetFavouriteCoinUseCase(coinId)
            } else {
                setFavouriteCoinUseCase(coinId)
            }
        }
    }

    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            is Message.CoinsChanged -> copy(
                categories = msg.categories.withHighlight(highlightMovers)
            )

            is Message.HighlightMoversChanged -> copy(
                categories = categories.withHighlight(msg.isChecked),
                highlightMovers = msg.isChecked
            )

            Message.CoinsLoadingFailed -> copy(categories = emptyList())
        }

        private fun List<State.CategoryItem>.withHighlight(
            isEnabled: Boolean,
        ): List<State.CategoryItem> = map { category ->
            category.copy(
                coins = category.coins.map { coin ->
                    coin.copy(highlight = isEnabled && coin.isHotMover)
                }
            )
        }
    }
}
