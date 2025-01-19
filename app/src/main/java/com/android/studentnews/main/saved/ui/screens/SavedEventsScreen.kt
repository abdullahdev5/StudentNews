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
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.IntOffset
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
import com.android.studentnews.core.domain.common.formatDateToString
import com.android.studentnews.core.domain.common.formatTimeToString
import com.android.studentnews.core.domain.common.getUrlOfImageNotVideo
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.main.events.domain.destination.EventsDestination
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedEventsViewModel
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.DarkColor
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.LightGray
import com.android.studentnews.ui.theme.Red
import com.android.studentnews.ui.theme.White
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.text.dropLast

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SavedEventsScreen(
    navHostController: NavHostController,
    savedEventsViewModel: SavedEventsViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {

    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current

    val savedEventsList = savedEventsViewModel.savedEventsList.collectAsLazyPagingItems()

    var maxWidth by remember { mutableStateOf(200.dp) }

    val savedEventsListNotFound = remember {
        derivedStateOf {
            savedEventsList.itemCount == 0
                    && savedEventsList.loadState.refresh is LoadState.NotLoading
                    && savedEventsList.loadState.hasError
        }
    }.value


    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {

        if (
            savedEventsList.itemCount != 0
            || savedEventsList.loadState.refresh is LoadState.Loading
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (savedEventsList.loadState.refresh is LoadState.NotLoading) {
                    items(
                        count = savedEventsList.itemCount,
                        key = savedEventsList.itemKey {
                            it.eventId ?: ""
                        },
                        contentType = savedEventsList.itemContentType {
                            "saved_events_list"
                        }
                    ) { index ->
                        val item = savedEventsList[index]

                        var offsetX = remember { Animatable(-0f) }
                        var isDragging by remember { mutableStateOf(false) }
                        var itemHeight by remember { mutableStateOf(0.dp) }


                        SavedEventsItem(
                            item = item,
                            context = context,
                            density = density,
                            animatedVisibilityScope = animatedVisibilityScope,
                            sharedTransitionScope = sharedTransitionScope,
                            onItemClick = { thisNewsId ->
                                navHostController.navigate(
                                    EventsDestination.EVENTS_DETAIL_SCREEN(thisNewsId)
                                )
                            },
                            offsetX = offsetX.value,
                            onDragStart = {
                                isDragging = true
                            },
                            onDragEnd = {
                                if ((offsetX.value).dp > maxWidth) {
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
                                val incrementedOffsetX = (offsetX.value) - newOffsetX
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
                            onEventRemoveFromSaveListClick = { thisEventId ->
                                scope.launch {
                                    with(density) {
                                        offsetX.animateTo((configuration.screenWidthDp.dp.toPx()))
                                    }
                                }
                                savedEventsViewModel.onEventRemoveFromSaveList(thisEventId)
                            },
                        )
                    }
                }

                if (
                    savedEventsList.loadState.append is LoadState.Loading
                    || savedEventsList.loadState.refresh is LoadState.Loading
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
                    savedEventsList.loadState.refresh is LoadState.Error
                ) {
                    item {
                        ErrorMessageContainer(
                            errorMessage =
                            (savedEventsList.loadState.refresh as LoadState.Error
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
                    savedEventsList.loadState.append is LoadState.Error
                    && (savedEventsList.loadState.append as LoadState.Error)
                        .error.localizedMessage != LENGTH_ERROR
                ) {
                    item {
                        ErrorMessageContainer(
                            errorMessage =
                            (savedEventsList.loadState.append as LoadState.Error
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
        }

        if (savedEventsListNotFound) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(text = "No Saved Events Found!")
            }
        }

    }


}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SavedEventsItem(
    item: EventsModel?,
    context: Context,
    density: Density,
    onItemClick: (String) -> Unit,
    onEventRemoveFromSaveListClick: (String) -> Unit,
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
                        .align(Alignment.CenterEnd)
                        .clickable {
                            item?.eventId?.let {
                                onEventRemoveFromSaveListClick(it)
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(0.dp))
                    .offset { IntOffset(-offsetX.roundToInt(), 0) }
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
                        onItemClick(item?.eventId ?: "")
                    }
                    .onGloballyPositioned { coordinates ->
                        onGloballyPositioned(coordinates)
                    },
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp)
                ) {
                    val imageRequest = ImageRequest.Builder(context)
                        .data(getUrlOfImageNotVideo(item?.urlList ?: emptyList()))
                        .crossfade(true)
                        .build()

                    with(sharedTransitionScope) {
                        AsyncImage(
                            model = imageRequest,
                            contentDescription = "News Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(90.dp)
                                .heightIn(max = 100.dp)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .sharedElement(
                                    state = rememberSharedContentState(key = "image/${item?.eventId ?: ""}"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    clipInOverlayDuringTransition = OverlayClip(
                                        RoundedCornerShape(
                                            10.dp
                                        )
                                    )
                                )
                        )
                    }


                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        with(sharedTransitionScope) {
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
                                        state = rememberSharedContentState(key = "title/${item?.eventId ?: ""}"),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                        renderInOverlayDuringTransition = true,
                                    ),
                            )
                        }

                        // Date
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = "Icon for Date",
                                modifier = Modifier
                                    .width(15.dp)
                                    .height(15.dp)
                            )

                            Text(
                                text = "${
                                    formatDateToString(item?.startingDate ?: 0L)
                                } - ${formatDateToString(item?.endingDate ?: 0L)}",
                                style = TextStyle(
                                    fontSize = (FontSize.SMALL).sp,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        // Time
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = "Icon for Time",
                                modifier = Modifier
                                    .width(15.dp)
                                    .height(15.dp),
                            )

                            Text(
                                text = "${
                                    formatTimeToString(
                                        (item?.startingTimeHour ?: 10),
                                        (item?.startingTimeMinutes ?: 0)
                                    ).dropLast(2)
                                } ${item?.startingTimeStatus} - ${
                                    formatTimeToString(
                                        (item?.endingTimeHour ?: 10), (item?.endingTimeMinutes ?: 0)
                                    ).dropLast(2)
                                } ${item?.endingTimeStatus} ",
                                style = TextStyle(
                                    fontSize = (FontSize.SMALL).sp,
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

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