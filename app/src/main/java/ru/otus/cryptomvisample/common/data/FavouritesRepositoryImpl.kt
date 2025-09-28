package ru.otus.cryptomvisample.common.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.otus.cryptomvisample.common.domain_impl.FavouritesRepository
import javax.inject.Inject

class FavouritesRepositoryImpl @Inject constructor(): FavouritesRepository {
    private val _favouriteCoins = MutableStateFlow<Set<String>>(emptySet())

    override fun consumeFavouriteIds(): Flow<Set<String>> {
        return _favouriteCoins.asStateFlow()
    }

    override fun addToFavourites(coinId: String) {
        _favouriteCoins.value = _favouriteCoins.value + coinId
    }

    override fun removeFromFavourites(coinId: String) {
        _favouriteCoins.value = _favouriteCoins.value - coinId
    }
}
