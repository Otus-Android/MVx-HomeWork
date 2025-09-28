package ru.otus.cryptomvisample.features.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.common.domain_api.ConsumeFavoriteCoinsUseCase
import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import javax.inject.Inject

@FeatureScope
class FavoriteViewModelFactory @Inject constructor(
    private val consumeFavoriteCoinsUseCase: ConsumeFavoriteCoinsUseCase,
    private val favoriteStateMapper: FavoriteStateMapper,
    private val unsetFavouriteCoinUseCase: UnsetFavouriteCoinUseCase,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        when {
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return FavoriteViewModel(
                    consumeFavoriteCoinsUseCase = consumeFavoriteCoinsUseCase,
                    mapper = favoriteStateMapper,
                    unsetFavouriteCoinUseCase = unsetFavouriteCoinUseCase,
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
