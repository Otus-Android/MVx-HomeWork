package ru.otus.cryptomvisample.features.coins

import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import ru.otus.common.di.FeatureScope
import javax.inject.Inject

@FeatureScope
class DefaultCoinComponent @Inject constructor(
    coinStoreFactory: CoinStoreFactory
) : CoinComponent {

    private val store = coinStoreFactory.create()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<CoinStore.State> = store.stateFlow

    override fun onHighlightMoversToggled(isChecked: Boolean) {
        store.accept(CoinStore.Intent.HighlightMoversToggled(isChecked))
    }

    override fun onToggleFavourite(coinId: String) {
        store.accept(CoinStore.Intent.ToggleFavourite(coinId))
    }

    fun dispose() {
        store.dispose()
    }
}
