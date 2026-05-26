package ru.otus.cryptomvisample.features.favourites

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import ru.otus.cryptomvisample.features.mvi.BaseViewModel


class FavoriteViewModel(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val mapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : BaseViewModel<FavoriteContract.State, FavoriteContract.Intent, FavoriteContract.Effect>(
    initialState = FavoriteContract.State()
) {

    init {
        loadFavoriteCoins()
    }

    override fun reduce(intent: FavoriteContract.Intent) {
        when (intent) {
            is FavoriteContract.Intent.RemoveFavoriteCoin -> handleRemoveFavoriteCoin(intent.id)
        }
    }

    private fun handleRemoveFavoriteCoin(coinId: String) {
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
                _state.value = _state.value.copy(coins = favoriteCoinsState)
            }
            .launchIn(viewModelScope)
    }
}
