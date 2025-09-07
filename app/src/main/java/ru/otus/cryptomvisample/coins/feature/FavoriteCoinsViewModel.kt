package ru.otus.cryptomvisample.coins.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.otus.cryptomvisample.coins.domain.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.coins.domain.CoinDomainMapper
import ru.otus.cryptomvisample.coins.domain.UnsetFavouriteCoinUseCase

data class FavoriteCoinsScreenState(
    val favoriteCoins: List<CoinState> = emptyList(),
)

class FavoriteCoinsViewModel(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val coinDomainMapper: CoinDomainMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteCoinsScreenState())
    val state: StateFlow<FavoriteCoinsScreenState> = _state.asStateFlow()

    init {
        loadFavoriteCoins()
    }

    fun onToggleFavourite(coinId: String) {
        unsetFavouriteCoinUseCase(coinId)
    }

    private fun loadFavoriteCoins() {
        consumeFavoriteCoinsUseCase()
            .map { favoriteCoins ->
                favoriteCoins.map { coin ->
                    coinDomainMapper.mapToState(coin)
                }
            }
            .onEach { favoriteCoinsState ->
                _state.value = _state.value.copy(favoriteCoins = favoriteCoinsState)
            }
            .launchIn(viewModelScope)
    }
}
