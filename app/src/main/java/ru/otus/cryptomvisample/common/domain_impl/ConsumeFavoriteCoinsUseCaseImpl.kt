package ru.otus.cryptomvisample.common.domain_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.otus.cryptomvisample.common.domain_api.Coin
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import javax.inject.Inject

class ConsumeFavoriteCoinsUseCaseImpl @Inject constructor(
    private val coinsRepository: CoinsRepository,
    private val favouritesRepository: FavouritesRepository,
) : ConsumeFavoriteCoinsUseCase {
    
    override operator fun invoke(): Flow<List<Coin>> {
        return combine(
            coinsRepository.consumeCoins(),
            favouritesRepository.consumeFavouriteIds(),
        ) { coins, favouriteIds ->
            coins.coins.filter { coin -> coin.id in favouriteIds }
        }
    }
}
