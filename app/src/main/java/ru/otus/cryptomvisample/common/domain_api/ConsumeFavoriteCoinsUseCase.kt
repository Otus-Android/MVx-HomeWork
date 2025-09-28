package ru.otus.cryptomvisample.common.domain_api

import kotlinx.coroutines.flow.Flow

interface ConsumeFavoriteCoinsUseCase {
    operator fun invoke(): Flow<List<Coin>>
}
