package ru.otus.cryptomvisample.features.favourites

import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import ru.otus.common.di.FeatureScope
import javax.inject.Inject

@FeatureScope
class DefaultFavoriteComponent @Inject constructor(
    favoriteStoreFactory: FavoriteStoreFactory,
) : FavoriteComponent {

    private val store = favoriteStoreFactory.create()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<FavoriteStore.State> = store.stateFlow

    override fun onRemoveFavourite(coinId: String) {
        store.accept(FavoriteStore.Intent.RemoveFavourite(coinId))
    }

    fun dispose() {
        store.dispose()
    }
}
