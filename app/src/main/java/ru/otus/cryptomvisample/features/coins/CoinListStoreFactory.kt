package ru.otus.cryptomvisample.features.coins

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import javax.inject.Inject

@FeatureScope
class CoinListStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) {

    fun create(): CoinListStore =
        object : CoinListStore, Store<CoinListStore.Intent, CoinsScreenState, Nothing> by storeFactory.create(
            name = "CoinListStore",
            autoInit = false,
            initialState = CoinsScreenState(),
            bootstrapper = SimpleBootstrapper(Action.LoadCoins),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl,
        ) {}

    private sealed interface Action {
        data object LoadCoins : Action
    }

    private sealed interface Message {
        data class CoinsLoaded(val categories: List<CoinCategoryState>) : Message
        data class HighlightMoversChanged(val isChecked: Boolean) : Message
    }

    private inner class ExecutorImpl : CoroutineExecutor<
        CoinListStore.Intent,
        Action,
        CoinsScreenState,
        Message,
        Nothing,
    >() {
        override fun executeAction(action: Action) {
            when (action) {
                Action.LoadCoins -> loadCoins()
            }
        }

        override fun executeIntent(intent: CoinListStore.Intent) {
            when (intent) {
                is CoinListStore.Intent.HighlightMoversToggled ->
                    dispatch(Message.HighlightMoversChanged(intent.isChecked))

                is CoinListStore.Intent.FavouriteToggled -> toggleFavourite(intent.coinId)
            }
        }

        private fun loadCoins() {
            scope.launch {
                consumeCoinsUseCase()
                    .map { groups -> groups.map(coinsStateFactory::create) }
                    .catch { dispatch(Message.CoinsLoaded(emptyList())) }
                    .collect { categories -> dispatch(Message.CoinsLoaded(categories)) }
            }
        }

        private fun toggleFavourite(coinId: String) {
            val isFavourite = state().categories
                .asSequence()
                .flatMap { it.coins.asSequence() }
                .firstOrNull { it.id == coinId }
                ?.isFavourite == true

            if (isFavourite) {
                unsetFavouriteCoinUseCase(coinId)
            } else {
                setFavouriteCoinUseCase(coinId)
            }
        }
    }

    private object ReducerImpl : Reducer<CoinsScreenState, Message> {
        override fun CoinsScreenState.reduce(message: Message): CoinsScreenState =
            when (message) {
                is Message.CoinsLoaded -> copy(
                    categories = message.categories.map { category ->
                        category.copy(
                            coins = category.coins.map { coin ->
                                coin.copy(highlight = highlightMovers && coin.isHotMover)
                            }
                        )
                    }
                )

                is Message.HighlightMoversChanged -> copy(
                    highlightMovers = message.isChecked,
                    categories = categories.map { category ->
                        category.copy(
                            coins = category.coins.map { coin ->
                                coin.copy(highlight = message.isChecked && coin.isHotMover)
                            }
                        )
                    }
                )
            }
    }
}
