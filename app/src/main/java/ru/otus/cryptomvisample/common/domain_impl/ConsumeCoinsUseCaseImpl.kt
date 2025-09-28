package ru.otus.cryptomvisample.common.domain_impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.otus.cryptomvisample.common.domain_api.CoinGroup
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import javax.inject.Inject

class ConsumeCoinsUseCaseImpl @Inject constructor(
    private val coinsRepository: CoinsRepository,
    private val favouritesRepository: FavouritesRepository,
) : ConsumeCoinsUseCase {
    
    override operator fun invoke(): Flow<List<CoinGroup>> {
        return combine(
            coinsRepository.consumeCoins(),
            favouritesRepository.consumeFavouriteIds(),
        ) { coins, favouriteIds ->
            val categoriesById = coins.categories.associateBy { it.id }
            coins.coins
                .groupBy { it.category }
                .map { (categoryId, coins) ->
                    val category = categoriesById[categoryId]!!
                    CoinGroup(
                        category = category,
                        coins = coins.map { coin ->
                            coin.copy(isFavourite = coin.id in favouriteIds)
                        }
                    )
                }
                .sortedBy { coinGroup ->
                    val categoryEntity = categoriesById[coinGroup.category.id]
                    categoryEntity?.order ?: Int.MAX_VALUE
                }
        }
    }
}
