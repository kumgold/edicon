package com.goldcompany.edicon.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.goldcompany.edicon.R
import com.goldcompany.edicon.data.local.image.ImageEntity
import com.goldcompany.edicon.data.local.video.VideoEntity
import com.goldcompany.edicon.ui.navigation.BottomNavigationBar
import com.goldcompany.edicon.ui.navigation.Route
import com.goldcompany.edicon.ui.util.ContentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val imagePagingItems: LazyPagingItems<ImageEntity> = viewModel.imagePagingData.collectAsLazyPagingItems()

    val favoriteImageIds by viewModel.favoriteImageIds.collectAsStateWithLifecycle()
    val favoriteVideoIds by viewModel.favoriteVideoIds.collectAsStateWithLifecycle()

    val lazyGridState = rememberLazyGridState()

    LaunchedEffect(uiState.isLoading) {
        if (imagePagingItems.loadState.refresh is LoadState.Loading) {
            lazyGridState.scrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.screen_title_home),
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val commonPadding = dimensionResource(id = R.dimen.padding_large)
            SearchTextField(
                modifier = Modifier.padding(
                    start = commonPadding,
                    end = commonPadding,
                    top = commonPadding
                ),
                query = searchQuery,
                onQueryChanged = viewModel::onQueryChanged,
                onSearch = {
                    viewModel.search()
                }
            )

            val hasContent = imagePagingItems.itemCount > 0 || uiState.videoResult != null

            if (hasContent && !uiState.isLoading) {
                Text(
                    stringResource(id = R.string.label_search_results),
                    modifier = Modifier
                        .padding(
                            start = commonPadding,
                            end = commonPadding,
                            bottom = commonPadding
                        ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = commonPadding),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_cell_spacing)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_cell_spacing))
                ) {
                    if (uiState.videoResult != null) {
                        uiState.videoResult?.let { video ->
                            item(key = "video_result_${uiState.videoResult!!.id}", span = { GridItemSpan(2) }) {
                                val isFavorite = favoriteVideoIds.contains(video.id)

                                VideoResultItem(
                                    video = video,
                                    isFavorite = isFavorite,
                                    onItemClick = {
                                        navController.navigate(
                                            Route.Detail(
                                                id = video.id,
                                                contentType = ContentType.VIDEO
                                            )
                                        )
                                    },
                                    onFavoriteClick = {
                                        viewModel.toggleVideoFavorite(video, isFavorite)
                                    }
                                )
                            }
                        }
                    }

                    items(
                        count = imagePagingItems.itemCount,
                        key = imagePagingItems.itemKey { "image_${it.id}" }
                    ) { index ->
                        val image = imagePagingItems[index]
                        image?.let {
                            val isFavorite = favoriteImageIds.contains(it.id)
                            ImageItem(
                                image = it,
                                isFavorite = isFavorite,
                                onItemClick = {
                                    navController.navigate(
                                        Route.Detail(
                                            id = image.id,
                                            contentType = ContentType.IMAGE
                                        )
                                    )
                                },
                                onFavoriteClick = { viewModel.toggleImageFavorite(it, isFavorite) }
                            )
                        }
                    }

                    item(key = "paging_state_footer", span = { GridItemSpan(2) }) {
                        if (searchQuery.isNotBlank()) {
                            val refreshState = imagePagingItems.loadState.refresh
                            val appendState = imagePagingItems.loadState.append

                            Box(modifier = Modifier.fillMaxWidth().padding(dimensionResource(id = R.dimen.padding_large)), contentAlignment = Alignment.Center) {
                                when {
                                    refreshState is LoadState.Loading || uiState.isLoading -> CircularProgressIndicator()
                                    appendState is LoadState.Loading -> CircularProgressIndicator(modifier = Modifier.size(dimensionResource(id = R.dimen.progress_indicator_size)))
                                    refreshState is LoadState.Error -> Text(stringResource(id = R.string.error_image_load_failed), color = Color.Red)
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            text = stringResource(R.string.search_for_images_and_videos),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchTextField(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier.fillMaxWidth().padding(bottom = dimensionResource(id = R.dimen.padding_large)),
        placeholder = { Text(stringResource(id = R.string.hint_search)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = stringResource(id = R.string.cd_search_icon)) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onQueryChanged("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_text)
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch()
            keyboardController?.hide()
        })
    )
}

@Composable
fun VideoResultItem(
    video: VideoEntity,
    isFavorite: Boolean,
    onItemClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember(video.id) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(video.largeVideoUrl))
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Box {
        Card(modifier = Modifier.clickable(onClick = onItemClick)) {
            AndroidView(
                factory = { PlayerView(it).apply { player = exoPlayer; useController = false } },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(MaterialTheme.shapes.medium)
            )
        }

        FavoriteIconButton(
            isFavorite = isFavorite,
            onClick = onFavoriteClick,
            modifier = Modifier.align(Alignment.TopEnd).padding(dimensionResource(id = R.dimen.padding_medium))
        )
    }
}

@Composable
fun ImageItem(
    image: ImageEntity,
    isFavorite: Boolean,
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
                model = image.previewUrl,
                contentDescription = image.tags,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        FavoriteIconButton(
            isFavorite = isFavorite,
            onClick = onFavoriteClick,
            modifier = Modifier.align(Alignment.TopEnd).padding(dimensionResource(id = R.dimen.padding_medium))
        )
    }
}

@Composable
fun FavoriteIconButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) stringResource(id = R.string.cd_remove_from_favorites) else stringResource(id = R.string.cd_add_to_favorites),
            tint = if (isFavorite) Color.Red else Color.White
        )
    }
}