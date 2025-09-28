package ru.otus.cryptomvisample.common.data

import ru.otus.cryptomvisample.common.domain_api.Category
import ru.otus.cryptomvisample.common.domain_api.Coin
import ru.otus.cryptomvisample.common.domain_api.Coins
import javax.inject.Inject

class CoinDomainMapper @Inject constructor() {
    fun fromEntity(coinsEntity: CoinsEntity): Coins {
        return Coins(
            coins = coinsEntity.coins.map(::fromEntity),
            categories = coinsEntity.categories.map(::fromEntity),
        )
    }

    private fun fromEntity(coinEntity: CoinEntity): Coin {
        return Coin(
            id = coinEntity.id,
            name = coinEntity.name,
            ticker = coinEntity.ticker,
            price = coinEntity.price,
            change24h = coinEntity.change24h,
            iconPath = coinEntity.iconPath,
            category = coinEntity.category,
        )
    }

    private fun fromEntity(categoryEntity: CategoryEntity): Category {
        return Category(
            id = categoryEntity.id,
            name = categoryEntity.name,
            order = categoryEntity.order,
        )
    }
}