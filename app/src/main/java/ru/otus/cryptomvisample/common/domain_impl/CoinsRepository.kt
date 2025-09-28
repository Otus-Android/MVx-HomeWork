package ru.otus.cryptomvisample.common.domain_impl

import kotlinx.coroutines.flow.Flow
import ru.otus.cryptomvisample.common.domain_api.Coins

interface CoinsRepository {
    fun consumeCoins(): Flow<Coins>
}