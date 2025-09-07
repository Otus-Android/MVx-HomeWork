package ru.otus.cryptomvisample.coins.domain

import kotlinx.coroutines.flow.Flow

interface ConsumeCoinsUseCase {
    operator fun invoke(): Flow<List<CoinCategory>>
}