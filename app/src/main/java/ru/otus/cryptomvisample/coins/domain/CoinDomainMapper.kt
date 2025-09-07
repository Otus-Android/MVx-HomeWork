package ru.otus.cryptomvisample.coins.domain

import ru.otus.cryptomvisample.coins.data.CoinEntity
import ru.otus.cryptomvisample.coins.feature.CoinState
import javax.inject.Inject

class CoinDomainMapper @Inject constructor() {
    fun fromEntity(coinEntity: CoinEntity): Coin {
        return Coin(
            id = coinEntity.id,
            name = coinEntity.name,
            ticker = coinEntity.ticker,
            price = coinEntity.price,
            change24h = coinEntity.change24h,
            iconPath = coinEntity.iconPath
        )
    }
    
    fun mapToState(coin: Coin): CoinState {
        return CoinState(
            id = coin.id,
            name = coin.name,
            image = coin.iconPath,
            price = "$${String.format("%.2f", coin.price)}",
            goesUp = coin.change24h > 0,
            discount = "${if (coin.change24h > 0) "+" else ""}${String.format("%.2f", coin.change24h)}%",
            isHotMover = false, // This would need to be determined by business logic
            highlight = false,
            isFavourite = false // This will be set by the repository
        )
    }
}
