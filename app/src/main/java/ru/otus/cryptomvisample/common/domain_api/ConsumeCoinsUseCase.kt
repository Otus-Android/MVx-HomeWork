package ru.otus.cryptomvisample.common.domain_api

import kotlinx.coroutines.flow.Flow

interface ConsumeCoinsUseCase {
    operator fun invoke(): Flow<List<CoinGroup>>
}