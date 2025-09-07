package ru.otus.cryptomvisample.coins.feature.di

import dagger.Component
import ru.otus.cryptomvisample.coins.data.CoinsRepository
import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.coins.feature.CoinListFragment

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
}

interface CoinListComponentDependencies {
    fun getProductRepository(): CoinsRepository
}