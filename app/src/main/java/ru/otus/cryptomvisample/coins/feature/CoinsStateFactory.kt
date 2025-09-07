package ru.otus.cryptomvisample.coins.feature

import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.coins.domain.Coin
import ru.otus.cryptomvisample.coins.domain.CoinCategory
import javax.inject.Inject
import java.text.DecimalFormat
import kotlin.math.abs

@FeatureScope
class CoinsStateFactory @Inject constructor() {
    private val priceFormatter = DecimalFormat("#,##0.########")
    private val percentageFormatter = DecimalFormat("#,##0.##%")
    
    fun create(coinCategory: CoinCategory): CoinCategoryState {
        return CoinCategoryState(
            id = coinCategory.id,
            name = coinCategory.name,
            coins = coinCategory.coins.map { coin ->
                createCoinState(coin)
            }
        )
    }
    
    private fun createCoinState(coin: Coin): CoinState {
        val formattedPrice = if (coin.price >= 1) {
            "$${priceFormatter.format(coin.price)}"
        } else {
            "$${String.format("%.8f", coin.price).trimEnd('0').trimEnd('.')}"
        }
        
        val changePercent = coin.change24h / 100.0
        val formattedChange = "${if (coin.change24h >= 0) "+" else ""}${percentageFormatter.format(changePercent)}"
        
        return CoinState(
            id = coin.id,
            name = coin.name,
            image = coin.iconPath,
            price = formattedPrice,
            goesUp = coin.change24h < 0,
            discount = formattedChange,
            isHotMover = abs(coin.change24h) > 5.0,
            isFavourite = coin.isFavourite,
        )
    }
}