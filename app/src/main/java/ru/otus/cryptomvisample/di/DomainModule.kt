package ru.otus.cryptomvisample.di

import dagger.Binds
import dagger.Module
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.common.domain_impl.ConsumeCoinsUseCaseImpl
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_impl.ConsumeFavoriteCoinsUseCaseImpl
import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_impl.SetFavouriteCoinUseCaseImpl
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_impl.UnsetFavouriteCoinUseCaseImpl

@Module
abstract class DomainModule {
    
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
