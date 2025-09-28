package ru.otus.cryptomvisample.common.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import ru.otus.cryptomvisample.common.domain_api.Coins
import ru.otus.cryptomvisample.common.domain_impl.CoinsRepository
import javax.inject.Inject
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class CoinsRepositoryImpl @Inject constructor(
    private val coinsLocalDataSource: CoinsLocalDataSource,
    private val coinDomainMapper: CoinDomainMapper,
): CoinsRepository {
    override fun consumeCoins(): Flow<Coins> {
        return coinsLocalDataSource.consumeCoins()
            .repeat(2.seconds, ::randomizePrices)
            .map(coinDomainMapper::fromEntity)

    }

    private fun randomizePrices(coinsEntity: CoinsEntity): CoinsEntity {
        return CoinsEntity(
            coins = coinsEntity.coins.map { coinEntity ->
                if (Random.nextDouble() < 0.5) {
                    val factor = 1 + Random.nextDouble(-0.02, 0.02) // -2% to +2%
                    coinEntity.copy(price = coinEntity.price * factor)
                } else {
                    coinEntity
                }
            },
            categories = coinsEntity.categories,
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun<T> Flow<T>.repeat(
    period: Duration,
    transform: (T) -> T,
): Flow<T> = transformLatest { value ->
    while (true) {
        emit(transform(value))
        delay(period)
    }
}
