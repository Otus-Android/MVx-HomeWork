package ru.otus.cryptomvisample.coins.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.coins.domain.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.coins.domain.CoinDomainMapper
import ru.otus.cryptomvisample.coins.domain.UnsetFavouriteCoinUseCase
import javax.inject.Inject

@FeatureScope
class FavoriteCoinsViewModelFactory @Inject constructor(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val coinDomainMapper: CoinDomainMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        when {
            modelClass.isAssignableFrom(FavoriteCoinsViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return FavoriteCoinsViewModel(
                    consumeFavoriteCoinsUseCase = consumeFavoriteCoinsUseCase,
                    coinDomainMapper = coinDomainMapper,
                    unsetFavouriteCoinUseCase = unsetFavouriteCoinUseCase,
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
