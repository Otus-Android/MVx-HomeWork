package ru.otus.cryptomvisample.features.coins.di

import dagger.Component
import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.features.coins.CoinListViewModelFactory

@FeatureScope
@Component(dependencies = [CoinListDependencies::class])
interface CoinListComponent {

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: CoinListDependencies,
        ): CoinListComponent
    }

    fun viewModelFactory(): CoinListViewModelFactory
}
