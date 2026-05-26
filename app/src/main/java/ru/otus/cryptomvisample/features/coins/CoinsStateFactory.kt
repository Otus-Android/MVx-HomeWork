package ru.otus.cryptomvisample.features.coins

import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.common.domain_api.Coin
import ru.otus.cryptomvisample.common.domain_api.CoinGroup
import ru.otus.cryptomvisample.common.util.ChangeFormatter
import ru.otus.cryptomvisample.common.util.PriceFormatter
import javax.inject.Inject
import kotlin.math.abs

@FeatureScope
class CoinsStateFactory @Inject constructor(
    private val priceFormatter: PriceFormatter,
    private val changeFormatter: ChangeFormatter,
) {
    fun create(coinGroup: CoinGroup): UiCoinCategory {
        return UiCoinCategory(
            id = coinGroup.category.id,
            name = coinGroup.category.name,
            coins = coinGroup.coins.map { coin -> createCoinState(coin) }
        )
    }
    
    private fun createCoinState(coin: Coin): UiCoinState {
        val formattedPrice = priceFormatter.formatPrice(coin.price)
        val formattedChange = changeFormatter.format(coin.change24h)
        
        return UiCoinState(
            id = coin.id,
            name = coin.name,
            image = coin.iconPath,
            price = formattedPrice,
            isPriceGoesUp = coin.change24h < 0,
            priceChange = formattedChange,
            isHotMover = abs(coin.change24h) > 5.0,
            isFavourite = coin.isFavourite,
        )
    }
}
