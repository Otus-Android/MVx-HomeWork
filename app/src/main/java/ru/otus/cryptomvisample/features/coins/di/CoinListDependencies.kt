package ru.otus.cryptomvisample.features.coins.di

import ru.otus.common.di.Dependencies
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase

interface CoinListDependencies: Dependencies {
    fun coinListStoreFactory(): StoreFactory
    fun consumeCoinsUseCase(): ConsumeCoinsUseCase
    fun setFavouriteCoinUseCase(): SetFavouriteCoinUseCase
    fun unsetFavouriteCoinUseCase(): UnsetFavouriteCoinUseCase
}
