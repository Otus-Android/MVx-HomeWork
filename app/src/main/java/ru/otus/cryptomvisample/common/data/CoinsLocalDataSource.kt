package ru.otus.cryptomvisample.common.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinsLocalDataSource @Inject constructor(
    private val context: Context
) {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    fun consumeCoins(): Flow<CoinsEntity> = flowOf(loadCoinsFromAssets())
    
    private fun loadCoinsFromAssets(): CoinsEntity {
        return try {
            val jsonString = context.assets.open("coins_data.json")
                .bufferedReader()
                .use { it.readText() }

            json.decodeFromString<CoinsEntity>(jsonString)
        } catch (e: Exception) {
            CoinsEntity(emptyList(), emptyList())
        }
    }
}
