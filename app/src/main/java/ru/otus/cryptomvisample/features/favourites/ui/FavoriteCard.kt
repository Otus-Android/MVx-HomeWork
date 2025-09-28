package ru.otus.cryptomvisample.features.favourites.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import coil.compose.AsyncImage
import ru.otus.cryptomvisample.features.favourites.FavouriteCoinState
import ru.otus.cryptomvisample.ui.theme.CryptomvisampleTheme
import ru.otus.cryptomvisample.ui.theme.GreenUp
import ru.otus.cryptomvisample.ui.theme.RedDown
import ru.otus.cryptomvisample.ui.theme.TextPrimary
import ru.otus.cryptomvisample.ui.theme.TextSecondary

@Composable
fun FavoriteCard(
    favouriteCoin: FavouriteCoinState,
    modifier: Modifier = Modifier,
    onToggleFavourite: (() -> Unit) = {}
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
            Text(
                text = favouriteCoin.name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = favouriteCoin.image,
                        contentDescription = favouriteCoin.name,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                IconButton(
                    onClick = onToggleFavourite,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Text(
                text = favouriteCoin.price,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = favouriteCoin.priceChange,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (favouriteCoin.isPriceGoesUp) RedDown else GreenUp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun FavoriteCardPreview() {
    CryptomvisampleTheme {
        FavoriteCard(
            favouriteCoin = FavouriteCoinState(
                id = "btc",
                name = "Bitcoin",
                image = "android.resource://ru.otus.cryptomvisample/drawable/btc",
                price = "67302.04",
                isPriceGoesUp = true,
                priceChange = "17.21%"
            )
        )
    }
}
