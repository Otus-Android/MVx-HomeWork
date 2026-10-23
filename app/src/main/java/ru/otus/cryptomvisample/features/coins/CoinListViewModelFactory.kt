package ru.otus.cryptomvisample.features.coins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import ru.otus.common.di.FeatureScope
import javax.inject.Inject

@FeatureScope
class CoinListViewModelFactory @Inject constructor(
    private val coinListStoreFactory: CoinListStoreFactory,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        when {
            modelClass.isAssignableFrom(CoinListViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return CoinListViewModel(
                    storeFactory = coinListStoreFactory,
                ) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
