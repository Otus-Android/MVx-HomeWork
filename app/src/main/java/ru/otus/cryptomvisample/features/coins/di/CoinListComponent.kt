package ru.otus.cryptomvisample.features.coins.di

import dagger.Component
import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.features.coins.DefaultCoinComponent

@FeatureScope
@Component(
    dependencies = [CoinListDependencies::class],
    modules = [CoinListModule::class],
)
interface CoinListComponent {

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: CoinListDependencies,
        ): CoinListComponent
    }

    fun coinComponent(): DefaultCoinComponent
}
