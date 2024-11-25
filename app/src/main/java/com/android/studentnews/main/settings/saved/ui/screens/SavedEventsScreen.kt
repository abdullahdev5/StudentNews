@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.android.studentnews.main.settings.saved.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.studentnews.core.domain.common.formatDateToString
import com.android.studentnews.core.domain.common.formatTimeToString
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.core.ui.common.LoadingDialog
import com.android.studentnews.main.events.domain.destination.EventsDestination
import com.android.studentnews.main.settings.saved.ui.viewModels.SavedEventsViewModel
import com.android.studentnews.main.news.ui.screens.getUrlOfImageNotVideo
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Red
import com.android.studentnews.ui.theme.White
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.Timestamp
import kotlin.math.roundToInt
import kotlin.text.dropLast

@Composable
fun SharedTransitionScope.SavedEventsScreen(
    navHostController: NavHostController,
    savedEventsViewModel: SavedEventsViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    val context = LocalContext.current
    val density = LocalDensity.current


    val savedEventsList by savedEventsViewModel.savedEventsList.collectAsStateWithLifecycle()


    Surface(
        color = if (isSystemInDarkTheme()) Color.Unspecified else White,
        modifier = Modifier
            .fillMaxSize(),
    ) {

        if (savedEventsList.size != 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(savedEventsList.size) { index ->
                    val item = savedEventsList[index]

                    SavedEventsItem(
                        item = item,
                        context = context,
                        density = density,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onItemClick = { thisNewsId ->
                            navHostController.navigate(
                                EventsDestination.EVENTS_DETAIL_SCREEN(thisNewsId)
                            )
                        },
                        onEventRemoveFromSaveList = { thisItem ->
                            savedEventsViewModel.onEventRemoveFromSaveList(thisItem)
                        }
                    )
                }
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(text = "No Save Events")
            }
        }


        if (savedEventsViewModel.savedEventsListStatus == Status.Loading) {
            LoadingDialog()
        }

    }


}

@Composable
fun SharedTransitionScope.SavedEventsItem(
    item: EventsModel?,
    context: Context,
    density: Density,
    onItemClick: (String) -> Unit,
    onEventRemoveFromSaveList: (EventsModel) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    var offsetX by remember { mutableStateOf(0) }
    var isDragging by remember { mutableStateOf(false) }
    var maxWidth = 250.dp
    var itemHeight by remember { mutableStateOf(80.dp) }


    Column {

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            if (isDragging) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .background(color = Black)
                ) {
                    AnimatedContent(
                        targetState = offsetX.dp > maxWidth,
                        label = "start_align",
                        modifier = Modifier
                            .align(Alignment.CenterStart),
                    ) { targetState ->
                        Icon(
                            imageVector = if (targetState)
                                Icons.Default.Delete else Icons.Outlined.Delete,
                            contentDescription = "Icon for Remove Item from Saved List",
                            tint = if (targetState) Red else White,
                        )
                    }

                    AnimatedContent(
                        targetState = (-offsetX).dp > maxWidth,
                        label = "end_align",
                        modifier = Modifier
                            .align(Alignment.CenterEnd),
                    ) { targetState ->
                        Icon(
                            imageVector = if (targetState)
                                Icons.Default.Delete else Icons.Outlined.Delete,
                            contentDescription = "Icon for Remove Item from Saved List",
                            tint = if (targetState) Red else White,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(0.dp))
                    .clickable {
                        onItemClick(item?.eventId ?: "")
                    }
                    .offset { androidx.compose.ui.unit.IntOffset(offsetX, 0) }
                    .pointerInput(true) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                isDragging = true
                            },
                            onDragEnd = {
                                if (offsetX.dp > maxWidth || (-offsetX.dp) > maxWidth) {
                                    item?.let { thisItem ->
                                        onEventRemoveFromSaveList.invoke(thisItem)
                                    }
                                } else {
                                    isDragging = false
                                    offsetX = 0
                                }
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                val newOffsetX = dragAmount
                                offsetX += newOffsetX.roundToInt()
                                println("OffsetX: $offsetX")
                            }
                        )
                    }
                    .background(color = if (isSystemInDarkTheme()) DarkGray else White)
                    .onGloballyPositioned { coordinates ->
                        itemHeight = with(density) { coordinates.size.height.toDp() }
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
                                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(10.dp))
                            )
                    )


                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
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
            color = Gray,
            modifier = Modifier.fillMaxWidth()
        )
    }
}