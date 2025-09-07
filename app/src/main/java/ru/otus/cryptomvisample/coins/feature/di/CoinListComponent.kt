package ru.otus.cryptomvisample.coins.feature.di

import dagger.Component
import ru.otus.cryptomvisample.coins.data.CoinsRepository
import ru.otus.cryptomvisample.coins.data.FavouritesRepository
import ru.otus.cryptomvisample.coins.domain.CoinDomainMapper
import ru.otus.cryptomvisample.coins.domain.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.coins.domain.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.coins.domain.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.coins.domain.UnsetFavouriteCoinUseCase
import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.coins.feature.CoinListFragment
import ru.otus.cryptomvisample.coins.feature.CoinListViewModelFactory
import ru.otus.cryptomvisample.coins.feature.FavoriteCoinsViewModelFactory

@FeatureScope
@Component(dependencies = [CoinListComponentDependencies::class])
interface CoinListComponent {

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: CoinListComponentDependencies,
        ): CoinListComponent
    }

    fun inject(coinListFragment: CoinListFragment)
    fun viewModelFactory(): CoinListViewModelFactory
    fun favoriteCoinsViewModelFactory(): FavoriteCoinsViewModelFactory
}

interface CoinListComponentDependencies {
    fun getProductRepository(): CoinsRepository
    fun favouritesRepository(): FavouritesRepository
    fun coinDomainMapper(): CoinDomainMapper
    fun consumeCoinsUseCase(): ConsumeCoinsUseCase
    fun consumeFavoriteCoinsUseCase(): ConsumeFavoriteCoinsUseCase
    fun setFavouriteCoinUseCase(): SetFavouriteCoinUseCase
    fun unsetFavouriteCoinUseCase(): UnsetFavouriteCoinUseCase
}