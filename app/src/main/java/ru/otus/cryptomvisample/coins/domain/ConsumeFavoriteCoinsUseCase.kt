package ru.otus.cryptomvisample.coins.domain

import kotlinx.coroutines.flow.Flow

interface ConsumeFavoriteCoinsUseCase {
    operator fun invoke(): Flow<List<Coin>>
}
