package ru.otus.cryptomvisample.coins.domain

interface UnsetFavouriteCoinUseCase {
    operator fun invoke(coinId: String): Result<Unit>
}
