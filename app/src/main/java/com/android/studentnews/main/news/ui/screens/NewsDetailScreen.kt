@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.main.news.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.android.studentnews.core.domain.common.formatDateToDay
import com.android.studentnews.core.domain.common.formatDateToMonthName
import com.android.studentnews.core.domain.common.formatDateToYear
import com.android.studentnews.core.domain.common.getUrlOfImageNotVideo
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.ui.composables.ButtonColors
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import com.android.studentnews.main.news.domain.destination.NewsDestinations
import com.android.studentnews.main.news.ui.viewModel.NewsDetailViewModel
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.ItemBackgroundColor
import com.android.studentnews.ui.theme.Red
import com.android.studentnews.ui.theme.White
import com.google.firebase.Timestamp

@OptIn(ExperimentalSharedTransitionApi::class)
@UnstableApi
@Composable
fun NewsDetailScreen(
    newsId: String,
    navHostController: NavHostController,
    newsDetailViewModel: NewsDetailViewModel,
    accountViewModel: AccountViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {

    LaunchedEffect(Unit) {
        newsDetailViewModel.getNewsById(newsId)
        newsDetailViewModel.getIsNewsSaved(newsId)
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val scrollState = rememberScrollState()

    val newsById by newsDetailViewModel.newsById.collectAsStateWithLifecycle()
    val currentUser by accountViewModel.currentUser.collectAsStateWithLifecycle()

    var isNewsSaved = remember(newsDetailViewModel.isNewsSaved) {
        derivedStateOf {
            newsDetailViewModel.isNewsSaved ?: false
        }
    }.value

    var isLiked by remember(newsById, currentUser) {
        mutableStateOf(
            newsById?.likes?.contains(currentUser?.uid ?: "") ?: false
        )
    }

    val pagerState = rememberPagerState(
        pageCount = {
            newsById?.urlList?.size ?: 1
        }
    )

    val horizontalPagerMaxHeight = with(density) { (300).dp.toPx() }

    var horizontalPagerOffset by remember { mutableFloatStateOf(horizontalPagerMaxHeight) }

    val horizontalPagerScrollConnection = remember(horizontalPagerMaxHeight) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                if (delta >= 0) {
                    return Offset.Zero
                }

                val newOffset = horizontalPagerOffset + delta
                val previousOffset = horizontalPagerOffset

                horizontalPagerOffset = newOffset.coerceIn(0f, horizontalPagerMaxHeight)

                val consumed = horizontalPagerOffset - previousOffset

                return Offset(0f, consumed)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y

                val newOffset = horizontalPagerOffset + delta
                val previousOffset = horizontalPagerOffset

                horizontalPagerOffset = newOffset.coerceIn(0f, horizontalPagerMaxHeight)

                val consumed = horizontalPagerOffset - previousOffset

                return Offset(0f, consumed)
            }

        }
    }

    BackHandler(
        enabled = sharedTransitionScope.isTransitionActive
    ) {}

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
                            color = if (isSystemInDarkTheme()) DarkColor else White
                        )
                        .padding(all = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = {
                            if (!sharedTransitionScope.isTransitionActive) {
                                navHostController.navigateUp()
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isSystemInDarkTheme()) White else Black
                        )
                    ) {
                        Text(text = "Back")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    AnimatedVisibility(scrollState.value > 300) {
                        Row {
                            IconsForLikeAndMore(
                                onShare = {
                                    val title = newsById?.title ?: ""
                                    val imageUrl = getUrlOfImageNotVideo(
                                        newsById?.urlList ?: emptyList()
                                    )

                                    newsDetailViewModel.onNewsShare(
                                        imageUrl = imageUrl,
                                        title = title,
                                        context = context,
                                        newsId = newsId
                                    )
                                },
                                onSave = {
                                    isNewsSaved = !isNewsSaved

                                    newsById?.let {

                                        val news = NewsModel(
                                            newsId = it.newsId,
                                            title = it.title,
                                            description = it.description,
                                            category = it.category,
                                            timestamp = Timestamp.now(),
                                            link = it.link,
                                            linkTitle = it.linkTitle,
                                            urlList = it.urlList,
                                            shareCount = it.shareCount ?: 0,
                                            likes = it.likes
                                        )

                                        if (isNewsSaved) {
                                            newsDetailViewModel.onNewsSave(news)
                                        } else {
                                            newsDetailViewModel.onNewsRemoveFromSave(
                                                news
                                            )
                                        }
                                    }
                                },
                                onLike = {
                                    isLiked = !isLiked

                                    if (isLiked) {
                                        newsDetailViewModel.onNewsLike(newsId)
                                    } else {
                                        newsDetailViewModel.onNewsUnLike(newsId)
                                    }
                                },
                                saveIconBtnContent = {
                                    AnimatedVisibility(isNewsSaved) {
                                        Icon(
                                            imageVector = Icons.Filled.Bookmark,
                                            contentDescription = "Icon for Saved News",
                                        )
                                    }

                                    AnimatedVisibility(!isNewsSaved) {
                                        Icon(
                                            imageVector = Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Icon for unSaved News",
                                        )
                                    }
                                },
                                likedIconBtnContent = {
                                    AnimatedVisibility(isLiked) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Icon of liked News",
                                        )
                                    }

                                    AnimatedVisibility(!isLiked) {
                                        Icon(
                                            imageVector = Icons.Default.FavoriteBorder,
                                            contentDescription = "Icon of unliked News",
                                        )
                                    }
                                },
                                likeBtnColors = IconButtonDefaults.iconButtonColors(
                                    contentColor = if (isLiked) Red else {
                                        if (isSystemInDarkTheme()) White else Black
                                    }
                                ),
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        with(sharedTransitionScope) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .sharedElement(
                        state = rememberSharedContentState(key = "container/$newsId"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        zIndexInOverlay = 1f
                    )
                    .nestedScroll(horizontalPagerScrollConnection)
                    .verticalScroll(scrollState)

            ) {

                Box(
                    modifier = Modifier
                        .then(
                            with(density) {
                                Modifier
                                    .height(
                                        (horizontalPagerOffset.toInt()).toDp()
                                    )
                            }
                        )
//                        .offset { IntOffset(0, horizontalPagerOffset) }
                ) {

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) { page ->

                        val item = newsById?.urlList?.get(page)

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
                                                .fillMaxSize(),
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

                            UrlListPagerIndicator(
                                state = pagerState,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 10.dp, end = 10.dp)
                            )
                        }

                    }

                }

                Box {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(bottom = 20.dp, end = 10.dp),
                    ) {
                        IconsForLikeAndMore(
                            // on Share
                            onShare = {
                                val title = newsById?.title ?: ""
                                val imageUrl = getUrlOfImageNotVideo(
                                    newsById?.urlList ?: emptyList()
                                )

                                newsDetailViewModel.onNewsShare(
                                    imageUrl = imageUrl,
                                    title = title,
                                    context = context,
                                    newsId = newsId
                                )
                            },
                            // on Save
                            onSave = {
                                isNewsSaved = !isNewsSaved

                                newsById?.let {
                                    val news = NewsModel(
                                        newsId = it.newsId,
                                        title = it.title,
                                        description = it.description,
                                        category = it.category,
                                        timestamp = Timestamp.now(),
                                        link = it.link,
                                        linkTitle = it.linkTitle,
                                        urlList = it.urlList,
                                        shareCount = it.shareCount ?: 0,
                                        likes = it.likes
                                    )

                                    if (isNewsSaved) {
                                        newsDetailViewModel.onNewsSave(news)
                                    } else {
                                        newsDetailViewModel.onNewsRemoveFromSave(news)
                                    }
                                }
                            },
                            // on Like
                            onLike = {
                                isLiked = !isLiked

                                if (isLiked) {
                                    newsDetailViewModel.onNewsLike(newsId)
                                } else {
                                    newsDetailViewModel.onNewsUnLike(newsId)
                                }
                            },
                            // Save Icon
                            saveIconBtnContent = {
                                this@Column.AnimatedVisibility(isNewsSaved) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = "Icon of Liked News",
                                    )
                                }

                                this@Column.AnimatedVisibility(!isNewsSaved) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = "Icon of unliked News",
                                    )
                                }
                            },
                            // Like Icon
                            likedIconBtnContent = {
                                this@Column.AnimatedVisibility(isLiked) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Icon of Liked News",
                                    )
                                }

                                this@Column.AnimatedVisibility(!isLiked) {
                                    Icon(
                                        imageVector = Icons.Default.FavoriteBorder,
                                        contentDescription = "Icon of unliked News",
                                    )
                                }
                            },
                            // Share Count
                            shareCountContent = {
                                if ((newsById?.shareCount ?: 0) > 0) {
                                    Text(text = (newsById?.shareCount ?: 0).toString())
                                }
                            },
                            // Like Count
                            likeCountContent = {
                                // Like Count
                                if (
                                    isLiked && (newsById?.likes?.size ?: 0) > 0
                                ) {
                                    Text(text = (newsById?.likes?.size ?: 0).toString())
                                }
                            },
                            // Like Button Colors
                            likeBtnColors = IconButtonDefaults.iconButtonColors(
                                contentColor = if (isLiked) Red else {
                                    if (isSystemInDarkTheme()) White else Black
                                }
                            ),
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 100.dp)
                            .background(
                                color = ItemBackgroundColor,
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
                                text = newsById?.category ?: "",
                                style = TextStyle(
                                    fontSize = FontSize.MEDIUM.sp,
                                    fontWeight = FontWeight.Bold,
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
                            text = newsById?.title ?: "",
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
                                    top = 20.dp,
                                    bottom = 10.dp
                                )
                        )

                        SelectionContainer {
                            Text(
                                text = newsById?.description ?: "",
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

                        if (!newsById?.link.isNullOrEmpty()) {
                            if (!newsById?.link.isNullOrEmpty()) {

                                Spacer(modifier = Modifier.height(10.dp))

                                if (newsById?.link.toString().toUri().isAbsolute) {
                                    FilledTonalButton(
                                        onClick = {
                                            navHostController.navigate(
                                                NewsDestinations.NEWS_LINK_SCREEN(
                                                    link = newsById?.link ?: ""
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
                                        Text(text = newsById?.linkTitle ?: "")
                                    }
                                }
                            }
                        }

                        newsById?.timestamp?.let { timestamp ->
                            Column(
                                modifier = Modifier
                                    .padding(
                                        top = 10.dp,
                                        start = 20.dp,
                                        end = 20.dp,
                                        bottom = 5.dp
                                    )
                            ) {
                                val day = formatDateToDay(timestamp.toDate().time)
                                val monthName = formatDateToMonthName(timestamp.toDate().time)
                                val year = formatDateToYear(timestamp.toDate().time)

                                Text(
                                    text = "News added on $day $monthName $year",
                                    style = TextStyle(
                                        fontSize = FontSize.SMALL.sp,
                                        color = Gray
                                    )
                                )

                            }
                        }

                    }

                }

            }
        }
    }
}

@Composable
fun UrlListPagerIndicator(
    state: PagerState,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Black.copy(0.3f)
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(all = 5.dp)
        ) {
            Text(text = "${state.currentPage + 1}", color = White)
            Text(text = "/", color = White)
            Text(text = "${state.pageCount}", color = White)
        }
    }
}

@Composable
fun IconsForLikeAndMore(
    onShare: () -> Unit,
    onSave: () -> Unit,
    onLike: () -> Unit,
    saveIconBtnContent: @Composable () -> Unit,
    likedIconBtnContent: @Composable () -> Unit,
    shareCountContent: @Composable (() -> Unit)? = null,
    likeCountContent: @Composable (() -> Unit)? = null,
    likeBtnColors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
) {
    // Share
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Share Icon
        IconButton(
            onClick = onShare,
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Icon of unliked News",
            )
        }
        // Share Count
        shareCountContent?.invoke()
    }

    // Save
    IconButton(
        onClick = onSave,
    ) {
        saveIconBtnContent()
    }

    // Like
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Like Icon
        IconButton(
            onClick = onLike,
            colors = likeBtnColors,
        ) {
            likedIconBtnContent()
        }
        // Like Count
        likeCountContent?.invoke()
    }
}