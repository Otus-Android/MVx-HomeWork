package ru.otus.cryptomvisample.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.otus.cryptomvisample.coins.feature.CoinListViewModel
import ru.otus.cryptomvisample.coins.feature.CoinListViewModelFactory
import ru.otus.cryptomvisample.coins.feature.FavoriteCoinsViewModel
import ru.otus.cryptomvisample.coins.feature.FavoriteCoinsViewModelFactory
import ru.otus.cryptomvisample.ui.screens.CoinListScreen
import ru.otus.cryptomvisample.ui.screens.FavoriteCoinsScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object CoinList : Screen("coin_list", "Coins", Icons.Default.List)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
}

@Composable
fun MainNavigation(
    coinListViewModelFactory: CoinListViewModelFactory,
    favoriteCoinsViewModelFactory: FavoriteCoinsViewModelFactory
) {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                listOf(Screen.CoinList, Screen.Favorites).forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.CoinList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.CoinList.route) {
                CoinListScreenContent(
                    factory = coinListViewModelFactory
                )
            }
            composable(Screen.Favorites.route) {
                FavoriteCoinsScreenContent(
                    factory = favoriteCoinsViewModelFactory
                )
            }
        }
    }
}

@Composable
fun CoinListScreenContent(
    factory: CoinListViewModelFactory
) {
    val viewModel: CoinListViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsState()
    
           CoinListScreen(
               state = state,
               onHighlightMoversToggled = viewModel::onHighlightMoversToggled,
               onToggleFavourite = viewModel::onToggleFavourite
           )
}

@Composable
fun FavoriteCoinsScreenContent(
    factory: FavoriteCoinsViewModelFactory
) {
    val viewModel: FavoriteCoinsViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsState()
    
    FavoriteCoinsScreen(
        favoriteCoins = state.favoriteCoins,
        onToggleFavourite = viewModel::onToggleFavourite
    )
}
