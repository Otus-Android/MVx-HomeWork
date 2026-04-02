package ru.otus.cryptomvisample.features.coins

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
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import ru.otus.cryptomvisample.common.domain_api.ConsumeCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase

class CoinListViewModel(
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
    private val setFavouriteCoinUseCase: SetFavouriteCoinUseCase,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModel(), ContainerHost<CoinsScreenState, CoinListSideEffect> {

    override val container = container<CoinsScreenState, CoinListSideEffect>(CoinsScreenState()) {
        requestCoins()
    }

    private var fullCategories: List<CoinCategoryState> = emptyList()
    private var highlightMovers = false

    fun onHighlightMoversToggled(isChecked: Boolean) = intent {
        reduce {
            state.copy(
                highlightMovers = isChecked,
                // Пересчитываем категории сразу при обновлении стейта
                categories = updateHighlight(state.categories, isChecked)
            )
        }
    }

    fun onToggleFavourite(coinId: String) = intent {
        val isCurrentlyFavorite = state.categories.any { category ->
            category.coins.any { coin -> coin.id == coinId && coin.isFavourite }
        }
        
        if (isCurrentlyFavorite) {
            unsetFavouriteCoinUseCase(coinId)
        } else {
            setFavouriteCoinUseCase(coinId)
        }
    }

    private suspend fun Syntax<CoinsScreenState, CoinListSideEffect>.requestCoins() {
        consumeCoinsUseCase()
            .map { categories ->
                categories.map { category -> coinsStateFactory.create(category) }
            }
            .catch {
                postSideEffect(CoinListSideEffect.ShowError(it.message ?: "Ошибка загрузки данных"))
            }
            .collect { categoryListState ->
                reduce {
                    state.copy(
                        categories = updateHighlight(categoryListState, state.highlightMovers)
                    )
                }
            }
    }

    private fun updateHighlight(
        categories: List<CoinCategoryState>,
        highlight: Boolean
    ): List<CoinCategoryState> {
        return categories.map { category ->
            category.copy(coins = category.coins.map { coin ->
                coin.copy(highlight = highlight && coin.isHotMover)
            })
        }
    }
}
