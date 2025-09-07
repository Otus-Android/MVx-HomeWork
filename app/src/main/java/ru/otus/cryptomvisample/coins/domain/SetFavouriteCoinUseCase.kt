package ru.otus.cryptomvisample.coins.domain

interface SetFavouriteCoinUseCase {
    operator fun invoke(coinId: String): Result<Unit>
}
