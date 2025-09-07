package ru.otus.cryptomvisample.coins.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.otus.cryptomvisample.coins.domain.Coin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouritesRepository @Inject constructor() {
    
    private val _favouriteCoins = MutableStateFlow<Set<String>>(emptySet())
    val favouriteCoins: StateFlow<Set<String>> = _favouriteCoins.asStateFlow()
    
    fun addToFavourites(coinId: String) {
        _favouriteCoins.value = _favouriteCoins.value + coinId
    }
    
    fun removeFromFavourites(coinId: String) {
        _favouriteCoins.value = _favouriteCoins.value - coinId
    }
    
    fun isFavourite(coinId: String): Boolean {
        return coinId in _favouriteCoins.value
    }
    
    fun toggleFavourite(coinId: String) {
        if (isFavourite(coinId)) {
            removeFromFavourites(coinId)
        } else {
            addToFavourites(coinId)
        }
    }
}
