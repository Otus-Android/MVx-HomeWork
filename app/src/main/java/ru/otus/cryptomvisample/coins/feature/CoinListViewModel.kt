package ru.otus.cryptomvisample.coins.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import ru.otus.cryptomvisample.coins.domain.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.coins.domain.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.coins.domain.UnsetFavouriteCoinUseCase

class CoinListViewModel(
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsScreenState())
    val state: StateFlow<CoinsScreenState> = _state.asStateFlow()

    private var fullCategories: List<CoinCategoryState> = emptyList()
    private var highlightMovers = false
    private var showAll = true

    init {
        requestCoins()
    }

    fun onHighlightMoversToggled(isChecked: Boolean) {
        highlightMovers = isChecked
        updateUiState()
    }

    fun onShowAllToggled(isChecked: Boolean) {
        showAll = isChecked
        updateUiState()
    }

    fun onToggleFavourite(coinId: String) {
        // Check if coin is currently favorite by looking at the current state
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
        var processedCategories = if (showAll) {
            fullCategories
        } else {
            fullCategories.map { category ->
                category.copy(coins = category.coins.take(4))
            }
        }

        processedCategories = processedCategories.map { category ->
            category.copy(coins = category.coins.map { coin ->
                coin.copy(
                    highlight = highlightMovers && coin.isHotMover
                )
            })
        }

        _state.update { 
            it.copy(
                categories = processedCategories,
                highlightMovers = highlightMovers,
                showAll = showAll
            ) 
        }
    }
}
