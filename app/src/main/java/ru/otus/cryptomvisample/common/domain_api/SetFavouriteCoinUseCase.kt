package ru.otus.cryptomvisample.common.domain_api

interface SetFavouriteCoinUseCase {
    operator fun invoke(coinId: String): Result<Unit>
}
