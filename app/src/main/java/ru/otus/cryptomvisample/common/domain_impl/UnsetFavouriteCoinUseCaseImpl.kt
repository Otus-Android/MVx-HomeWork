package ru.otus.cryptomvisample.common.domain_impl

import ru.otus.cryptomvisample.common.domain_api.UnsetFavouriteCoinUseCase
import javax.inject.Inject

class UnsetFavouriteCoinUseCaseImpl @Inject constructor(
    private val favouritesRepository: FavouritesRepository
) : UnsetFavouriteCoinUseCase {
    
    override operator fun invoke(coinId: String): Result<Unit> {
        return try {
            favouritesRepository.removeFromFavourites(coinId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
