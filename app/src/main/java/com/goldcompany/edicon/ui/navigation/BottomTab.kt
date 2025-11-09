package com.goldcompany.edicon.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomTab(val route: Any, val title: String, val icon: ImageVector) {
    data object Home : BottomTab(route = Route.Home, "홈", Icons.Default.Home)
    data object Favorite : BottomTab(route = Route.Favorite, "즐겨찾기", Icons.Default.Favorite)
}