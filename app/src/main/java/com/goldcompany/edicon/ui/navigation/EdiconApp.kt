package com.goldcompany.edicon.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.goldcompany.edicon.ui.detail.ContentDetailScreen
import com.goldcompany.edicon.ui.favorite.FavoriteScreen
import com.goldcompany.edicon.ui.home.HomeScreen

val items = listOf(
    BottomTab.Home,
    BottomTab.Favorite,
)

@Composable
fun EdiconApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Home,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable<Route.Home> {
            HomeScreen(
                navController = navController
            )
        }
        composable<Route.Favorite> {
            FavoriteScreen(
                navController = navController
            )
        }
        composable<Route.Detail> {
            ContentDetailScreen(
                navController = navController,
            )
        }
    }
}