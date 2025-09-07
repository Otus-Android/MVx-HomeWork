package ru.otus.cryptosample.coins.feature.di

import dagger.Component
import ru.otus.cryptosample.coins.data.CoinsRepository
import ru.otus.common.di.FeatureScope
import ru.otus.cryptosample.coins.feature.CoinListFragment

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