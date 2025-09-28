package ru.otus.cryptomvisample.features.favourites

import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.common.domain_api.Coin
import ru.otus.cryptomvisample.common.util.ChangeFormatter
import ru.otus.cryptomvisample.common.util.PriceFormatter
import javax.inject.Inject

@FeatureScope
class FavoriteStateMapper @Inject constructor(
    private val priceFormatter: PriceFormatter,
    private val changeFormatter: ChangeFormatter,
) {
    fun mapToState(coin: Coin): FavouriteCoinState {
        return FavouriteCoinState(
            id = coin.id,
            name = coin.name,
            image = coin.iconPath,
            price = priceFormatter.formatPrice(coin.price),
            isPriceGoesUp = coin.change24h > 0,
            priceChange = changeFormatter.format(coin.change24h),
        )
    }
}
