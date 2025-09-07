package ru.otus.cryptomvisample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.otus.cryptomvisample.coins.feature.CoinListViewModelFactory
import ru.otus.cryptomvisample.coins.feature.FavoriteCoinsViewModelFactory
import ru.otus.cryptomvisample.coins.feature.di.DaggerCoinListComponent
import ru.otus.cryptomvisample.ui.navigation.MainNavigation
import ru.otus.cryptomvisample.ui.theme.CryptomvisampleTheme

class MainActivity : ComponentActivity() {

    private lateinit var coinListViewModelFactory: CoinListViewModelFactory
    private lateinit var favoriteCoinsViewModelFactory: FavoriteCoinsViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Create CoinListComponent and get factories
        val appComponent = (applicationContext as CoinsSampleApp).appComponent
        val coinListComponent = DaggerCoinListComponent.factory().create(appComponent)
        coinListViewModelFactory = coinListComponent.viewModelFactory()
        favoriteCoinsViewModelFactory = coinListComponent.favoriteCoinsViewModelFactory()
        
        setContent {
            CryptomvisampleTheme {
                MainNavigation(
                    coinListViewModelFactory = coinListViewModelFactory,
                    favoriteCoinsViewModelFactory = favoriteCoinsViewModelFactory
                )
            }
        }
    }
}
