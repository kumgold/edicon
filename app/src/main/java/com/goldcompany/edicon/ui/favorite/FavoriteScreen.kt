package com.goldcompany.edicon.ui.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.goldcompany.edicon.R
import com.goldcompany.edicon.ui.favorite.model.FavoriteUiItem
import com.goldcompany.edicon.ui.navigation.BottomNavigationBar
import com.goldcompany.edicon.ui.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    navController: NavController,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val favoriteItems by viewModel.favoriteItems.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.screen_title_favorite),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->

        if (favoriteItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(id = R.string.message_favorites_empty))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_large)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_cell_spacing)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_cell_spacing))
            ) {
                items(favoriteItems, key = { it.id }) { item ->
                    FavoriteItemCard(
                        item = item,
                        onItemClick = {
                            navController.navigate(
                                Route.Detail(
                                    id = item.id,
                                    contentType = item.type,
                                )
                            )
                        },
                        onFavoriteClick = {
                            viewModel.removeFromFavorites(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteItemCard(
    item: FavoriteUiItem,
    onItemClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
    ) {
        Card(modifier = Modifier.fillMaxSize().clickable(onClick = onItemClick)) {
            AsyncImage(
                model = item.thumbnailUrl,
                contentDescription = item.tags,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier.align(Alignment.TopEnd).padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = stringResource(id = R.string.cd_remove_from_favorites),
                tint = Color.Red
            )
        }
    }
}