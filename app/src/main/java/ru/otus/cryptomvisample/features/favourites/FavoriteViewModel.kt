package ru.otus.cryptomvisample.features.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase


class FavoriteViewModel(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val mapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteCoinsScreenState())
    val state: StateFlow<FavoriteCoinsScreenState> = _state.asStateFlow()

    init {
        loadFavoriteCoins()
    }

    fun removeFavourite(coinId: String) {
        unsetFavouriteCoinUseCase(coinId)
    }

    private fun loadFavoriteCoins() {
        consumeFavoriteCoinsUseCase()
            .map { favoriteCoins ->
                favoriteCoins.map { coin ->
                    mapper.mapToState(coin)
                }
            }
            .onEach { favoriteCoinsState ->
                _state.value = _state.value.copy(favoriteCoins = favoriteCoinsState)
            }
            .launchIn(viewModelScope)
    }
}
