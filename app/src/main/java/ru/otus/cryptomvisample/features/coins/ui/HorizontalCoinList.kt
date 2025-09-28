package ru.otus.cryptomvisample.features.coins.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.otus.cryptomvisample.features.coins.CoinState

@Composable
fun HorizontalCoinList(
    coins: List<CoinState>,
    onToggleFavourite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(coins) { coin ->
            CoinCard(
                coin = coin,
                onToggleFavourite = { onToggleFavourite(coin.id) },
                modifier = Modifier.width(100.dp)
            )
        }
    }
}
