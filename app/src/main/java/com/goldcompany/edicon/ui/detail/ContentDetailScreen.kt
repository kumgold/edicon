package com.goldcompany.edicon.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.goldcompany.edicon.R
import com.goldcompany.edicon.ui.detail.model.DetailContent
import com.goldcompany.edicon.ui.util.ContentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDetailScreen(
    navController: NavController,
    viewModel: ContentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.screen_title_detail)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_back_button)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.errorMessage != null -> {
                    Text(text = uiState.errorMessage ?: stringResource(id = R.string.error_default))
                }
                uiState.content != null -> {
                    DetailContent(content = uiState.content!!, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun DetailContent(content: DetailContent, viewModel: ContentDetailViewModel) {
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    var isFullScreen by remember { mutableStateOf(false) }
    val commonPadding = dimensionResource(id = R.dimen.padding_large)

    val exoPlayer = if (content.type == ContentType.VIDEO) {
        rememberExoPlayer(url = content.contentUrl ?: "")
    } else {
        null
    }

    if (isFullScreen) {
        FullScreenDialog(
            content = content,
            exoPlayer = exoPlayer,
            onDismiss = { isFullScreen = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(MaterialTheme.shapes.medium)
                .border(
                    width = 1.dp,
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            when (content.type) {
                ContentType.IMAGE -> {
                    AsyncImage(
                        model = content.contentUrl,
                        contentDescription = stringResource(id = R.string.cd_content_detail_image),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(commonPadding)
                            .aspectRatio(16f / 9f),
                        contentScale = ContentScale.Fit
                    )
                }
                ContentType.VIDEO -> {
                    exoPlayer?.let {
                        VideoPlayer(exoPlayer = it, useController = false)
                    }
                }
            }

            IconButton(
                onClick = { isFullScreen = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    Icons.Default.Fullscreen,
                    stringResource(R.string.full_screen),
                    tint = Color.White
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = commonPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    stringResource(id = R.string.label_uploader),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    content.user,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(
                onClick = { viewModel.toggleFavorite() }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavorite) stringResource(id = R.string.cd_remove_from_favorites)
                        else stringResource(id = R.string.cd_add_to_favorites),
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
        HorizontalDivider(modifier = Modifier.padding(horizontal = commonPadding))
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = commonPadding),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(title = stringResource(id = R.string.label_type), value = content.type.toString())
            StatItem(title = stringResource(id = R.string.label_views), value = content.views.toString())
            StatItem(title = stringResource(id = R.string.label_likes), value = content.likes.toString())
            StatItem(title = stringResource(id = R.string.label_downloads), value = content.downloads.toString())
        }

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
        HorizontalDivider(modifier = Modifier.padding(horizontal = commonPadding))
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = commonPadding)
        ) {
            Text(
                stringResource(id = R.string.label_tags),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
            Text(
                text = content.tags.split(",").joinToString(" #", prefix = "#"),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(commonPadding))
    }
}

@Composable
private fun StatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FullScreenDialog(
    content: DetailContent,
    exoPlayer: ExoPlayer?,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            when (content.type) {
                ContentType.IMAGE -> {
                    ZoomableImage(model = content.contentUrl)
                }
                ContentType.VIDEO -> {
                    exoPlayer?.let {
                        VideoPlayer(exoPlayer = it, useController = true)
                    }
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Close, "닫기", tint = Color.White)
            }
        }
    }
}

@Composable
private fun ZoomableImage(model: String?) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    AsyncImage(
        model = model,
        contentDescription = "전체 화면 이미지",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .transformable(state = transformableState)
    )
}

@Composable
private fun rememberExoPlayer(url: String): ExoPlayer {
    val context = LocalContext.current
    val exoPlayer = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            playWhenReady = true
            prepare()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
    return exoPlayer
}

@Composable
private fun VideoPlayer(exoPlayer: ExoPlayer, useController: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(MaterialTheme.shapes.medium)
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { PlayerView(it).apply { player = exoPlayer; this.useController = useController } },
            modifier = Modifier.fillMaxSize()
        )
    }
}