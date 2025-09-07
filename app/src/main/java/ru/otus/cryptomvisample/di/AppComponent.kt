package ru.otus.cryptomvisample.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.otus.cryptomvisample.coins.data.FavouritesRepository
import ru.otus.cryptomvisample.coins.domain.CoinDomainMapper
import ru.otus.cryptomvisample.coins.domain.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.coins.domain.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.coins.domain.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.coins.domain.UnsetFavouriteCoinUseCase
import ru.otus.cryptomvisample.coins.feature.di.CoinListComponentDependencies
import ru.otus.common.di.Dependencies
import javax.inject.Singleton

@Singleton
@Component(modules = [UseCaseModule::class])
interface AppComponent:
    Dependencies,
    CoinListComponentDependencies
{
    override fun favouritesRepository(): FavouritesRepository
    override fun coinDomainMapper(): CoinDomainMapper
    override fun consumeCoinsUseCase(): ConsumeCoinsUseCase
    override fun consumeFavoriteCoinsUseCase(): ConsumeFavoriteCoinsUseCase
    override fun setFavouriteCoinUseCase(): SetFavouriteCoinUseCase
    override fun unsetFavouriteCoinUseCase(): UnsetFavouriteCoinUseCase
    
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }
}