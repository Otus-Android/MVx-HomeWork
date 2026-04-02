package ru.otus.cryptomvisample.features.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase


class FavoriteViewModel(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val mapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel(), ContainerHost<FavoriteCoinsScreenState, Nothing> {

    override val container = container<FavoriteCoinsScreenState, Nothing>(
        FavoriteCoinsScreenState()
    ) {
        loadFavoriteCoins()
    }

    fun removeFavourite(coinId: String) = intent {
        unsetFavouriteCoinUseCase(coinId)
    }

    private fun loadFavoriteCoins() = intent {
        consumeFavoriteCoinsUseCase()
            .map { favoriteCoins ->
                favoriteCoins.map { mapper.mapToState(coin = it) }
            }
            .collect { favoriteCoinsState ->
                reduce {
                    state.copy(favoriteCoins = favoriteCoinsState)
                }
            }
    }
}
