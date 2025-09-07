package ru.otus.cryptomvisample.coins.domain

import ru.otus.cryptomvisample.coins.data.FavouritesRepository
import javax.inject.Inject

class SetFavouriteCoinUseCaseImpl @Inject constructor(
    private val favouritesRepository: FavouritesRepository
) : SetFavouriteCoinUseCase {
    
    override operator fun invoke(coinId: String): Result<Unit> {
        return try {
            favouritesRepository.addToFavourites(coinId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
