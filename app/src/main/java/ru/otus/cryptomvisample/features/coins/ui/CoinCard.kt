package ru.otus.cryptomvisample.features.coins.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.otus.cryptomvisample.features.coins.CoinState
import ru.otus.cryptomvisample.ui.theme.CryptomvisampleTheme
import ru.otus.cryptomvisample.ui.theme.GreenUp
import ru.otus.cryptomvisample.ui.theme.RedDown
import ru.otus.cryptomvisample.ui.theme.TextPrimary
import ru.otus.cryptomvisample.ui.theme.TextSecondary

@Composable
fun CoinCard(
    coin: CoinState,
    modifier: Modifier = Modifier,
    onToggleFavourite: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = coin.image,
                        contentDescription = coin.name,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    if (coin.highlight) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "🔥",
                            fontSize = 12.sp
                        )
                    }
                }
                
                if (onToggleFavourite != null) {
                    IconButton(
                        onClick = onToggleFavourite,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (coin.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (coin.isFavourite) "Remove from favorites" else "Add to favorites",
                            tint = if (coin.isFavourite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Text(
                text = coin.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = coin.price,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = coin.priceChange,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (coin.isPriceGoesUp) RedDown else GreenUp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun CoinCardPreview() {
    CryptomvisampleTheme {
        CoinCard(
            coin = CoinState(
                id = "btc",
                name = "Bitcoin",
                image = "android.resource://ru.otus.cryptomvisample/drawable/btc",
                price = "67302.04",
                isPriceGoesUp = true,
                priceChange = "17.21%",
                isHotMover = true,
                isFavourite = true,
                highlight = true,
            )
        )
    }
}
