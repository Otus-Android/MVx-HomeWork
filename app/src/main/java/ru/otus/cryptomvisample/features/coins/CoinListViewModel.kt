package ru.otus.cryptomvisample.features.coins

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.observer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CoinListViewModel(
    private val storeFactory: CoinListStoreFactory,
) : ViewModel() {

    private val store = storeFactory.create()
    private val _state = MutableStateFlow(store.state)
    val state: StateFlow<CoinsScreenState> = _state.asStateFlow()
    private val stateSubscription: Disposable = store.states(
        observer(onNext = { newState -> _state.value = newState })
    )

    init {
        store.init()
    }

    fun accept(intent: CoinListStore.Intent) {
        store.accept(intent)
    }

    override fun onCleared() {
        stateSubscription.dispose()
        store.dispose()
        super.onCleared()
    }
}
