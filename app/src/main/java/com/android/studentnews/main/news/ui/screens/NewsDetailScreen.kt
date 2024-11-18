@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.android.studentnews.main.news.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.common.isInternetAvailable
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.ui.common.ButtonColors
import com.android.studentnews.main.news.domain.destination.NewsDestination
import com.android.studentnews.main.news.ui.viewModel.NewsDetailViewModel
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.model.UrlList
import com.android.studentnews.news.ui.viewModel.NewsViewModel
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.White
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

@UnstableApi
@Composable
fun SharedTransitionScope.NewsDetailScreen(
    newsId: String,
    navHostController: NavHostController,
    newsDetailViewModel: NewsDetailViewModel,
    newsViewModel: NewsViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val newsById = newsDetailViewModel.newsById.collectAsStateWithLifecycle()
    val savedNewsById = newsDetailViewModel.savedNewsById.collectAsStateWithLifecycle()

    var isSaved by remember(savedNewsById.value) {
        mutableStateOf(savedNewsById.value != null)
    }

    val pagerState = rememberPagerState(
        pageCount = {
            newsById.value?.urlList?.size ?: 1
        }
    )

    LaunchedEffect(Unit) {
        newsDetailViewModel.getNewsById(newsId)
        newsDetailViewModel.getSavedNewsById(newsId)
    }


    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                HorizontalDivider(color = Gray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Green.copy(0.1f)/*LightGray.copy(0.3f)*/
                        )
                        .padding(all = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ExtendedFloatingActionButton(
                        text = {
                            Text(text = "Back")
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIos,
                                contentDescription = "Icon for Navigate Back",
                            )
                        },
                        onClick = {
                            navHostController.navigateUp()
                        },
                        expanded = scrollState.value < 20,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                        ),
                        containerColor = Green.copy(0.5f),
                        contentColor = White,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Gray,
                                shape = FloatingActionButtonDefaults.extendedFabShape
                            )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            if (isInternetAvailable(context)) {

                                isSaved = !isSaved

                                newsById.value?.let {
                                    if (isSaved) {
                                        val news = NewsModel(
                                            newsId = it.newsId,
                                            title = it.title,
                                            description = it.description,
                                            category = it.category,
                                            timestamp = Timestamp.now(),
                                            link = it.link,
                                            linkTitle = it.linkTitle,
                                            urlList = it.urlList,
                                            shareCount = it.shareCount ?: 0
                                        )

                                        newsViewModel.viewModelScope.launch {
                                            newsViewModel
                                                .onNewsSave(news)
                                                .collect { result ->
                                                    when (result) {
                                                        else -> {}
                                                    }
                                                }
                                        }
                                    } else {
                                        newsViewModel.onNewsRemoveFromSave(
                                            it.newsId ?: "",
                                            wantToShowSnackBar = false
                                        )
                                    }
                                }

                            } else {
                                scope.launch {
                                    SnackBarController
                                        .sendEvent(
                                            SnackBarEvents(
                                                message = "No Internet Connection!",
                                                duration = SnackbarDuration.Long
                                            )
                                        )
                                }
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Green.copy(0.5f),
                            contentColor = White
                        ),
                    ) {
                        AnimatedVisibility(isSaved) {
                            Icon(
                                imageVector = Icons.Filled.Bookmark,
                                contentDescription = "Icon for Saved News",
                            )
                        }

                        AnimatedVisibility(!isSaved) {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkAdd,
                                contentDescription = "Icon for unSaved News",
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        IconButton(
                            onClick = {
                                val title = newsById.value?.title ?: ""
                                val imageUrl = getUrlOfImageNotVideo(
                                    newsById.value?.urlList ?: emptyList()
                                )

                                newsDetailViewModel.onShareNews(
                                    imageUrl,
                                    context,
                                    onShare = { fileUri ->
                                        Intent(
                                            Intent.ACTION_SEND,
                                        ).apply {
                                            if (fileUri != null) {
                                                putExtra(Intent.EXTRA_STREAM, fileUri)
                                                type = "image/*"
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            putExtra(Intent.EXTRA_TEXT, title)
                                            type = "text/plain"
                                        }.let { intent ->
                                            val sharedIntent = Intent.createChooser(
                                                intent,
                                                null,
                                            )

                                            context.startActivity(sharedIntent)
                                            newsDetailViewModel.storeShareCount(newsId)
                                        }
                                    }
                                )

                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Green.copy(0.5f),
                                contentColor = White
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Icon for unSaved News",
                            )
                        }
                        AnimatedVisibility((newsById.value?.shareCount ?: 0) > 0) {
                            Text(
                                text = "${newsById.value?.shareCount ?: 0}",
                                style = TextStyle(
                                    fontSize = FontSize.MEDIUM.sp
                                ),
                            )
                        }
                    }

                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateLeftPadding(LayoutDirection.Rtl),
                )
                .verticalScroll(scrollState)
                .sharedElement(
                    state = rememberSharedContentState(key = "container/$newsId"),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
        ) {

            Box {

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                ) { page ->

                    val item = newsById.value?.urlList?.get(page)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {

                        if (item?.contentType.toString().startsWith("image")) {

                            val imageRequest = ImageRequest.Builder(context)
                                .data(item?.url ?: "")
                                .crossfade(true)
                                .build()

                            SubcomposeAsyncImage(
                                model = imageRequest,
                                contentDescription = "Image",
                                contentScale = ContentScale.Fit,
                                loading = {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                    ) {
                                        CircularProgressIndicator(color = Green)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .sharedElement(
                                        state = rememberSharedContentState(key = "image/$newsId"),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                    ),
                            )

                        }

                        if (item?.contentType.toString().startsWith("video")) {

                            val mediaItem = MediaItem.Builder()
                                .setUri(item?.url)
                                .build()

                            val exoplayer = remember(context, mediaItem) {
                                ExoPlayer.Builder(context)
                                    .build()
                                    .apply {
                                        setMediaItem(mediaItem)
                                        prepare()
                                        // playWhenReady = true
                                    }
                            }

                            var isPlaying by remember { mutableStateOf(false) }

                            DisposableEffect(
                                Box {
                                    AndroidView(
                                        factory = {
                                            PlayerView(it).apply {
                                                player = exoplayer
                                                useController = true
                                                imageDisplayMode =
                                                    PlayerView.IMAGE_DISPLAY_MODE_FILL
                                                hideController()
                                                setShowFastForwardButton(false)
                                                setShowRewindButton(false)
                                                setShowNextButton(false)
                                                setShowPreviousButton(false)
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .sharedElement(
                                                state = rememberSharedContentState(key = "image/$newsId"),
                                                animatedVisibilityScope = animatedVisibilityScope,
                                            ),
                                    )

                                    // Video Play Icon
                                    if (!isPlaying) {
                                        IconButton(
                                            onClick = {
                                                exoplayer.play()
                                            },
                                            modifier = Modifier
                                                .background(
                                                    color = White,
                                                    shape = CircleShape
                                                )
                                                .align(Alignment.Center)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Icon for Playing the Video",
                                                tint = Black
                                            )
                                        }
                                    }
                                }
                            ) {
                                onDispose {
                                    exoplayer.release()
                                    isPlaying = false
                                }
                            }

                            val listener = remember {
                                object : Player.Listener {
                                    override fun onIsPlayingChanged(myIsPlaying: Boolean) {
                                        if (myIsPlaying) {
                                            isPlaying = true
                                        }
                                    }
                                }
                            }

                            exoplayer.addListener(listener)

                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Black.copy(0.3f)
                            ),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 10.dp, end = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(all = 5.dp)
                            ) {
                                Text(text = "${pagerState.currentPage + 1}", color = White)
                                Text(text = "/", color = White)
                                Text(text = "${pagerState.pageCount}", color = White)
                            }
                        }
                    }

                }

            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp)
                    .background(
                        color = Green.copy(0.1f)/*LightGray.copy(0.3f)*/,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp,
                        )
                    ),
            ) {
                // Category Container
                Box(
                    modifier = Modifier
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 20.dp,
                            bottom = 5.dp
                        )
                        .background(
                            color = Black.copy(0.1f),
                            shape = RoundedCornerShape(5.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = newsById.value?.category ?: "",
                        style = TextStyle(
                            fontSize = FontSize.MEDIUM.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        ),
                        modifier = Modifier
                            .padding(all = 5.dp)
                    )
                }

                val customLineBreak = LineBreak(
                    strategy = LineBreak.Strategy.HighQuality,
                    strictness = LineBreak.Strictness.Strict,
                    wordBreak = LineBreak.WordBreak.Phrase
                )

                Text(
                    text = newsById.value?.title ?: "",
                    style = TextStyle(
                        fontSize = FontSize.LARGE.sp,
                        fontWeight = FontWeight.Bold,
                        lineBreak = customLineBreak,
                        hyphens = Hyphens.Auto,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 10.dp,
                            bottom = 10.dp
                        )
                        .sharedElement(
                            state = rememberSharedContentState(key = "title/$newsId"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                )

                SelectionContainer {
                    Text(
                        text = newsById.value?.description ?: "",
                        style = TextStyle(
                            fontSize = FontSize.MEDIUM.sp,
                            lineBreak = customLineBreak,
                            hyphens = Hyphens.Auto,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    )
                }

                if (!newsById.value?.link.isNullOrEmpty()) {

                    Spacer(modifier = Modifier.height(10.dp))

                    if (newsById.value?.link.toString().toUri().isAbsolute) {
                        FilledTonalButton(
                            onClick = {
                                navHostController.navigate(
                                    NewsDestination.NEWS_LINK_SCREEN(
                                        link = newsById.value?.link ?: ""
                                    )
                                )
                            },
                            colors = ButtonColors(
                                containerColor = Green.copy(0.5f),
                                contentColor = White
                            ),
                            modifier = Modifier
                                .padding(all = 20.dp)
                        ) {
                            Text(text = newsById.value?.linkTitle ?: "")
                        }
                    }
                }
            }
        }

    }

}

fun getUrlOfImageNotVideo(urlList: List<UrlList?>): String {
    var imageIndex = mutableIntStateOf(0)
    val imageUrl =
        if (
            urlList.get(imageIndex.intValue)
                ?.contentType.toString().startsWith("image/")
        ) urlList.get(imageIndex.intValue)?.url ?: ""
        else urlList.get(imageIndex.intValue++)?.url ?: ""

    return imageUrl
}