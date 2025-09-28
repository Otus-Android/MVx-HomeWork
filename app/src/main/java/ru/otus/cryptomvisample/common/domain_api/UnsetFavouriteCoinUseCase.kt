package ru.otus.cryptomvisample.common.domain_api

interface UnsetFavouriteCoinUseCase {
    operator fun invoke(coinId: String): Result<Unit>
}
