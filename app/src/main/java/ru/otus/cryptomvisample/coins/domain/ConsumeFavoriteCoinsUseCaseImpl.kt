package ru.otus.cryptomvisample.coins.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import ru.otus.cryptomvisample.coins.data.CoinsRepository
import ru.otus.cryptomvisample.coins.data.FavouritesRepository
import javax.inject.Inject

class ConsumeFavoriteCoinsUseCaseImpl @Inject constructor(
    private val coinsRepository: CoinsRepository,
    private val coinDomainMapper: CoinDomainMapper,
    private val favouritesRepository: FavouritesRepository,
) : ConsumeFavoriteCoinsUseCase {
    
    override operator fun invoke(): Flow<List<Coin>> {
        return combine(
            coinsRepository.consumeCoins(),
            favouritesRepository.favouriteCoins
        ) { coinEntities, favouriteIds ->
            val allCoins = coinEntities.coins.map { coinEntity ->
                coinDomainMapper.fromEntity(coinEntity)
            }
            allCoins.filter { coin -> coin.id in favouriteIds }
        }
    }
}
