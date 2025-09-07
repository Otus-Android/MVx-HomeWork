package ru.otus.cryptomvisample.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.otus.cryptomvisample.coins.feature.CoinCategoryState
import ru.otus.cryptomvisample.coins.feature.CoinsScreenState
import ru.otus.cryptomvisample.ui.components.CategoryHeader
import ru.otus.cryptomvisample.ui.components.CoinCard
import ru.otus.cryptomvisample.ui.components.HorizontalCoinList
import ru.otus.cryptomvisample.ui.theme.TextPrimary

@Composable
fun CoinListScreen(
    state: CoinsScreenState,
    onHighlightMoversToggled: (Boolean) -> Unit,
    onShowAllToggled: (Boolean) -> Unit,
    onToggleFavourite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Coins",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Filter chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Highlight movers chip
            Card(
                onClick = { onHighlightMoversToggled(!state.highlightMovers) },
                colors = CardDefaults.cardColors(
                    containerColor = if (state.highlightMovers) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.highlightMovers,
                        onCheckedChange = onHighlightMoversToggled,
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.onPrimary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Highlight movers",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (state.highlightMovers) 
                            MaterialTheme.colorScheme.onPrimary 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Show all chip
            Card(
                onClick = { onShowAllToggled(!state.showAll) },
                colors = CardDefaults.cardColors(
                    containerColor = if (state.showAll) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.showAll,
                        onCheckedChange = onShowAllToggled,
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.onPrimary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Show All",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (state.showAll) 
                            MaterialTheme.colorScheme.onPrimary 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Coins list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
        items(state.categories) { category ->
            CategorySection(
                category = category,
                onToggleFavourite = onToggleFavourite,
                modifier = Modifier.fillMaxWidth()
            )
        }
        }
    }
}

@Composable
private fun CategorySection(
    category: CoinCategoryState,
    onToggleFavourite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CategoryHeader(title = category.name)
        
        if (category.coins.size >= 10) {
            // Show horizontal list for categories with many coins
            HorizontalCoinList(
                coins = category.coins,
                onToggleFavourite = onToggleFavourite
            )
        } else {
            // Show grid for categories with fewer coins
            val rows = (category.coins.size + 1) / 2
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(rows) { rowIndex ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val leftCoin = category.coins.getOrNull(rowIndex * 2)
                        val rightCoin = category.coins.getOrNull(rowIndex * 2 + 1)
                        
                        if (leftCoin != null) {
                            CoinCard(
                                coin = leftCoin,
                                onToggleFavourite = { onToggleFavourite(leftCoin.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rightCoin != null) {
                            CoinCard(
                                coin = rightCoin,
                                onToggleFavourite = { onToggleFavourite(rightCoin.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
