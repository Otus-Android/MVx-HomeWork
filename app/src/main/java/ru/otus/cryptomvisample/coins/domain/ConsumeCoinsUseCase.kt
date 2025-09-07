package ru.otus.cryptosample.coins.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.otus.cryptosample.coins.data.CoinsRepository
import javax.inject.Inject

class ConsumeCoinsUseCase @Inject constructor(
    private val coinsRepository: CoinsRepository,
    private val coinDomainMapper: CoinDomainMapper,
) {
    operator fun invoke(): Flow<List<CoinCategory>> {
        return coinsRepository.consumeCoins()
            .map { coinEntities ->
                val categoriesById = coinEntities.categories.associateBy { it.id }
                coinEntities.coins
                    .groupBy { it.category }
                    .map { (categoryId, coins) ->
                        val category = categoriesById[categoryId]
                        CoinCategory(
                            id = categoryId,
                            name = category?.name ?: categoryId,
                            coins = coins.map { coinDomainMapper.fromEntity(it) }
                        )
                    }
                    .sortedBy { category ->
                        val categoryEntity = categoriesById[category.id]
                        categoryEntity?.order ?: Int.MAX_VALUE
                    }
            }
    }
}