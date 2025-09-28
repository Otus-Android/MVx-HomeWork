package ru.otus.cryptomvisample.common.domain_impl

import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {
    fun consumeFavouriteIds(): Flow<Set<String>>
    fun addToFavourites(coinId: String)
    fun removeFromFavourites(coinId: String)
}