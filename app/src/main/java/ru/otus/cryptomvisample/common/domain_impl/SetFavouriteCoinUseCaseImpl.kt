package ru.otus.cryptomvisample.common.domain_impl

import ru.otus.cryptomvisample.common.domain_api.SetFavouriteCoinUseCase
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
