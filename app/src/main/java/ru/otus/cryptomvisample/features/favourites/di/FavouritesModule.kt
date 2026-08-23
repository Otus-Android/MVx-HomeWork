package ru.otus.cryptomvisample.features.favourites.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.otus.common.di.FeatureScope

@Module
object FavouritesModule {

    @Provides
    @FeatureScope
    fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()
}
