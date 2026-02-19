package ru.otus.cryptomvisample.common.util

internal typealias Dispatcher<STATE, ACTION> = suspend (STATE, ACTION) -> Unit

internal typealias Reducer<STATE, CHANGE> = (STATE, CHANGE) -> STATE
