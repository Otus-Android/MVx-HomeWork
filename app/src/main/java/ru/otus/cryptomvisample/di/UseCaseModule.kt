package ru.otus.cryptomvisample.di

import dagger.Binds
import dagger.Module
import ru.otus.cryptomvisample.coins.domain.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.coins.domain.ConsumeCoinsUseCaseImpl
import ru.otus.cryptomvisample.coins.domain.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.coins.domain.ConsumeFavoriteCoinsUseCaseImpl
import ru.otus.cryptomvisample.coins.domain.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.coins.domain.SetFavouriteCoinUseCaseImpl
import ru.otus.cryptomvisample.coins.domain.UnsetFavouriteCoinUseCase
import ru.otus.cryptomvisample.coins.domain.UnsetFavouriteCoinUseCaseImpl

@Module
abstract class UseCaseModule {
    
    @Binds
    abstract fun bindConsumeCoinsUseCase(
        impl: ConsumeCoinsUseCaseImpl
    ): ConsumeCoinsUseCase
    
    @Binds
    abstract fun bindConsumeFavoriteCoinsUseCase(
        impl: ConsumeFavoriteCoinsUseCaseImpl
    ): ConsumeFavoriteCoinsUseCase
    
    @Binds
    abstract fun bindSetFavouriteCoinUseCase(
        impl: SetFavouriteCoinUseCaseImpl
    ): SetFavouriteCoinUseCase
    
    @Binds
    abstract fun bindUnsetFavouriteCoinUseCase(
        impl: UnsetFavouriteCoinUseCaseImpl
    ): UnsetFavouriteCoinUseCase
}
