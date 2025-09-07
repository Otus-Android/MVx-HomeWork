package ru.otus.cryptosample.coins.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CoinsRepository @Inject constructor(
    private val coinsLocalDataSource: CoinsLocalDataSource,
) {
    fun consumeCoins(): Flow<CoinsEntity> {
        return coinsLocalDataSource.consumeCoins()
    }
}
