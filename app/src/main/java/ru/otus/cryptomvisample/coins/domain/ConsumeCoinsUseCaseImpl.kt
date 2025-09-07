package ru.otus.cryptomvisample.coins.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import ru.otus.cryptomvisample.coins.data.CoinsRepository
import ru.otus.cryptomvisample.coins.data.FavouritesRepository
import javax.inject.Inject

class ConsumeCoinsUseCaseImpl @Inject constructor(
    private val coinsRepository: CoinsRepository,
    private val coinDomainMapper: CoinDomainMapper,
    private val favouritesRepository: FavouritesRepository,
) : ConsumeCoinsUseCase {
    
    override operator fun invoke(): Flow<List<CoinCategory>> {
        return combine(
            coinsRepository.consumeCoins(),
            favouritesRepository.favouriteCoins
        ) { coinEntities, favouriteIds ->
            val categoriesById = coinEntities.categories.associateBy { it.id }
            coinEntities.coins
                .groupBy { it.category }
                .map { (categoryId, coins) ->
                    val category = categoriesById[categoryId]
                    CoinCategory(
                        id = categoryId,
                        name = category?.name ?: categoryId,
                        coins = coins.map { coinEntity ->
                            val coin = coinDomainMapper.fromEntity(coinEntity)
                            coin.copy(isFavourite = coin.id in favouriteIds)
                        }
                    )
                }
                .sortedBy { category ->
                    val categoryEntity = categoriesById[category.id]
                    categoryEntity?.order ?: Int.MAX_VALUE
                }
        }
    }
}
