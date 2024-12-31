@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.main.settings.saved.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.studentnews.core.data.paginator.LENGTH_ERROR
import com.android.studentnews.core.domain.common.ErrorMessageContainer
import com.android.studentnews.core.domain.common.formatDateOrTimeToAgo
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.main.news.domain.destination.NewsDestinations
import com.android.studentnews.core.domain.common.getUrlOfImageNotVideo
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedNewsViewModel
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.LightGray
import com.android.studentnews.ui.theme.Red
import com.android.studentnews.ui.theme.White
import kotlinx.coroutines.launch
import kotlin.let
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SavedNewsScreen(
    navHostController: NavHostController,
    savedNewsViewModel: SavedNewsViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val maxWidth by remember { mutableStateOf(200.dp) }

    val savedNewsList = savedNewsViewModel.savedNewsList.collectAsLazyPagingItems()

    val savedNewsListNotFound = remember {
        derivedStateOf {
            savedNewsList.itemCount == 0
                    && savedNewsList.loadState.refresh is LoadState.NotLoading
                    && savedNewsList.loadState.hasError
        }
    }.value


    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            if (savedNewsList.loadState.refresh is LoadState.NotLoading) {
                items(
                    count = savedNewsList.itemCount,
                    key = savedNewsList.itemKey {
                        it.newsId ?: ""
                    },
                    contentType = savedNewsList.itemContentType {
                        "saved_news_list"
                    }
                ) { index ->
                    val item = savedNewsList[index]

                    var offsetX = remember { Animatable(0f) }
                    var isDragging by remember { mutableStateOf(false) }
                    var itemHeight by remember { mutableStateOf(0.dp) }


                    SavedNewsItem(
                        item = item,
                        context = context,
                        density = density,
                        onItemClick = { newsId ->
                            navHostController.navigate(NewsDestinations.NEWS_DETAIL_SCREEN(newsId))
                        },
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope,
                        offsetX = offsetX.value,
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            if (offsetX.value.dp > maxWidth) {
                                scope.launch {
                                    offsetX.animateTo(maxWidth.value.toFloat())
                                }
                            } else {
                                isDragging = false
                                scope.launch {
                                    offsetX.animateTo(0f)
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val newOffsetX = dragAmount
                            val incrementedOffsetX = (offsetX.value) + newOffsetX
                            scope.launch {
                                with(density) {
                                    if (offsetX.value < maxWidth.toPx() - 100.dp.toPx()) {
                                        offsetX.snapTo(
                                            incrementedOffsetX.coerceIn(
                                                minimumValue = 0f,
                                                maximumValue = maxWidth.toPx()
                                            )
                                        )
                                    } else {
                                        offsetX.animateTo(
                                            incrementedOffsetX.coerceIn(
                                                minimumValue = 0f,
                                                maximumValue = maxWidth.toPx()
                                            )
                                        )
                                    }
                                }
                            }
                        },
                        onGloballyPositioned = { coordinates ->
                            itemHeight = with(density) { coordinates.size.height.toDp() }
                        },
                        itemHeight = itemHeight,
                        maxWidth = { maxWidth },
                        isDragging = isDragging,
                        onRemoveFromSavedListClick = { thisNews ->
                            scope.launch {
                                with(density) {
                                    offsetX.animateTo(configuration.screenWidthDp.dp.toPx())
                                }
                            }
                            savedNewsViewModel.onNewsRemoveFromSave(thisNews)
                        },
                    )
                }
            }

            if (
                savedNewsList.loadState.append is LoadState.Loading
                || savedNewsList.loadState.refresh is LoadState.Loading
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (
                savedNewsList.loadState.refresh is LoadState.Error
            ) {
                item {
                    ErrorMessageContainer(
                        errorMessage =
                        (savedNewsList.loadState.refresh as LoadState.Error
                                ).error.localizedMessage ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            20.dp
                        ),
                    )
                }
            }

            if (
                savedNewsList.loadState.append is LoadState.Error
                && (savedNewsList.loadState.append as LoadState.Error)
                    .error.localizedMessage != LENGTH_ERROR
            ) {
                item {
                    ErrorMessageContainer(
                        errorMessage =
                        (savedNewsList.loadState.append as LoadState.Error
                                ).error.localizedMessage ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            20.dp
                        ),
                    )
                }
            }

        }

        if (savedNewsListNotFound) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(text = "No Saved News Found!")
            }
        }

    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SavedNewsItem(
    item: NewsModel?,
    context: Context,
    density: Density,
    onItemClick: (String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    offsetX: Float,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onHorizontalDrag: (change: PointerInputChange, dragAmount: Float) -> Unit,
    onGloballyPositioned: (coordinates: LayoutCoordinates) -> Unit,
    itemHeight: Dp,
    maxWidth: () -> Dp,
    isDragging: Boolean,
    onRemoveFromSavedListClick: (NewsModel) -> Unit,
) {

    Column(
        modifier = Modifier
            .background(color = if (isSystemInDarkTheme()) DarkGray else LightGray)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            if (isDragging) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .then(
                            with(density) {
                                Modifier
                                    .width((maxWidth().value).toDp())
                            }
                        )
                        .height(itemHeight)
                        .background(color = if (isSystemInDarkTheme()) LightGray else Black)
                        .align(Alignment.CenterStart)
                        .clickable {
                            item?.let {
                                onRemoveFromSavedListClick(it)
                            }
                        }
                ) {
                    AnimatedContent(
                        targetState = (offsetX).dp > maxWidth()
                                || (offsetX).dp == maxWidth(),
                        label = "delete_from_save",
                    ) { targetState ->
                        Icon(
                            imageVector = if (targetState)
                                Icons.Default.Delete else Icons.Outlined.Delete,
                            contentDescription = "Icon for Remove Item from Saved List",
                            tint = if (targetState) Red else {
                                if (isSystemInDarkTheme()) Black else White
                            },
                        )
                    }
                }
            }

            with(sharedTransitionScope) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sharedElement(
                            state = rememberSharedContentState(key = "container/${item?.newsId}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                        .offset {
                            androidx.compose.ui.unit.IntOffset(
                                offsetX.roundToInt(),
                                0
                            )
                        }
                        .pointerInput(true) {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    onDragStart()
                                },
                                onDragEnd = onDragEnd,
                                onHorizontalDrag = { change, dragAmount ->
                                    onHorizontalDrag(change, dragAmount)
                                }
                            )
                        }
                        .background(
                            color = if (isSystemInDarkTheme()) DarkColor else White
                        )
                        .clickable {
                            onItemClick.invoke(item?.newsId ?: "")
                        }
                        .onGloballyPositioned { coordinates ->
                            onGloballyPositioned(coordinates)
                        },
                ) {
                    Row {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(end = 5.dp)
                                    .weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            color = Black.copy(0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Text(
                                        text = item?.category ?: "",
                                        modifier = Modifier
                                            .padding(all = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = item?.title ?: "",
                                    style = TextStyle(
                                        fontSize = (FontSize.MEDIUM - 1).sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .sharedElement(
                                            state = rememberSharedContentState(key = "title/${item?.newsId}"),
                                            animatedVisibilityScope = animatedVisibilityScope,
                                        )
                                )

                                Text(
                                    text = item?.description ?: "",
                                    style = TextStyle(
                                        fontSize = FontSize.SMALL.sp,
                                        color = Gray,
                                    ),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .padding(top = 10.dp)
                                )
                            }

                            val imageRequest = ImageRequest.Builder(context)
                                .data(getUrlOfImageNotVideo(item?.urlList ?: emptyList()))
                                .crossfade(true)
                                .build()

                            AsyncImage(
                                model = imageRequest,
                                contentDescription = "News Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(80.dp)
                                    .heightIn(max = 80.dp)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .sharedElement(
                                        state = rememberSharedContentState(key = "image/${item?.newsId}"),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        renderInOverlayDuringTransition = true,
                                        clipInOverlayDuringTransition = OverlayClip(
                                            RoundedCornerShape(
                                                10.dp
                                            )
                                        )
                                    )
                            )
                        }
                    }

                    item?.timestamp?.let { timestamp ->

                        val dateChar = formatDateOrTimeToAgo(timestamp.toDate())

                        Text(
                            text = "$dateChar when save",
                            style = TextStyle(
                                fontSize = FontSize.SMALL.sp,
                                color = Gray
                            ),
                            modifier = Modifier
                                .padding(all = 10.dp)
                        )
                    }

                }

            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp)
        )
    }

}