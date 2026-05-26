package ru.otus.cryptomvisample.features.coins

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import ru.otus.cryptomvisample.features.mvi.BaseViewModel

class CoinListViewModel(
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : BaseViewModel<CoinsListContract.State, CoinsListContract.Intent, CoinsListContract.Effect>(initialState = CoinsListContract.State()) {

    private var fullCategories: List<UiCoinCategory> = emptyList()
    private var highlightMovers = false

    init {
        requestCoins()
    }

    override fun reduce(intent: CoinsListContract.Intent){
        when(intent){
            is CoinsListContract.Intent.OnHighlightMoversToggled -> handleOnHighlightMoversToggled(intent.toggled)

            is CoinsListContract.Intent.OnToggleFavourite -> handleOnToggleFavourite(intent.id)
        }
    }

    private fun handleOnHighlightMoversToggled(isChecked: Boolean) {
        highlightMovers = isChecked
        updateUiState()
    }

    private fun handleOnToggleFavourite(coinId: String) {
        val isCurrentlyFavorite = fullCategories.any { category ->
            category.coins.any { coin -> coin.id == coinId && coin.isFavourite }
        }
        
        if (isCurrentlyFavorite) {
            unsetFavouriteCoinUseCase(coinId)
        } else {
            setFavouriteCoinUseCase(coinId)
        }
    }

    private fun requestCoins() {
        consumeCoinsUseCase()
            .map { categories ->
                categories.map { category -> coinsStateFactory.create(category) }
            }
            .onEach { categoryListState ->
                fullCategories = categoryListState
                updateUiState()
            }
            .catch {
                fullCategories = emptyList()
                updateUiState()
            }
            .launchIn(viewModelScope)
    }

    private fun updateUiState() {
        val processedCategories = fullCategories.map { category ->
            category.copy(coins = category.coins.map { coin ->
                coin.copy(
                    highlight = highlightMovers && coin.isHotMover
                )
            })
        }

        _state.update { 
            it.copy(
                categories = processedCategories,
                highlightMovers = highlightMovers
            )
        }
    }
}
