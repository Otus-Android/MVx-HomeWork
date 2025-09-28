package ru.otus.cryptomvisample.di

import dagger.Binds
import dagger.Module
import ru.otus.cryptomvisample.common.data.CoinsRepositoryImpl
import ru.otus.cryptomvisample.common.data.FavouritesRepositoryImpl
import ru.otus.cryptomvisample.common.domain_impl.CoinsRepository
import ru.otus.cryptomvisample.common.domain_impl.FavouritesRepository
import javax.inject.Singleton

@Module
abstract class DataModule {

    @Singleton
    @Binds
    abstract fun bindCoinsRepository(
        coinsRepositoryImpl: CoinsRepositoryImpl
    ): CoinsRepository

    @Singleton
    @Binds
    abstract fun bindFavouritesRepository(
        favouritesRepositoryImpl: FavouritesRepositoryImpl
    ): FavouritesRepository
}
