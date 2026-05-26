package ru.otus.cryptomvisample.features.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<S : BaseViewModelConfiguration.State, I : BaseViewModelConfiguration.Intent, E : BaseViewModelConfiguration.Effect>(
    initialState: S
): ViewModel() {
    protected val _state = MutableStateFlow(initialState)

    val state = _state.asStateFlow()

    protected val _effects =
        MutableSharedFlow<E>(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

    val effects = _effects.asSharedFlow()

    abstract fun reduce(intent: I)

    protected fun postEffect(effect: E){
        _effects.tryEmit(effect)
    }

}