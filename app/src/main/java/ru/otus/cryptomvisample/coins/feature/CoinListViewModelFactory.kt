package ru.otus.cryptomvisample.coins.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import ru.otus.common.di.FeatureScope
import ru.otus.cryptomvisample.coins.domain.ConsumeCoinsUseCase
import javax.inject.Inject

@FeatureScope
class CoinListViewModelFactory @Inject constructor(
    private val consumeCoinsUseCase: ConsumeCoinsUseCase,
    private val coinsStateFactory: CoinsStateFactory,
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        when {
            modelClass.isAssignableFrom(CoinListViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return CoinListViewModel(
                    consumeCoinsUseCase = consumeCoinsUseCase,
                    coinsStateFactory = coinsStateFactory,
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}