@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.android.studentnews.main.events.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.core.ui.common.LoadingDialog
import com.android.studentnews.main.events.domain.destination.EventsDestination
import com.android.studentnews.main.events.ui.viewModels.EventsViewModel
import com.android.studentnews.main.news.ui.screens.getUrlOfImageNotVideo
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.core.domain.common.formatDateToString
import com.android.studentnews.core.domain.common.formatTimeToString
import com.android.studentnews.news.ui.viewModel.NewsViewModel
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.DarkGray
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.LightGray
import com.android.studentnews.ui.theme.White
import com.android.studentnewsadmin.main.events.domain.models.EventsModel

enum class EventsCategoryList(
    val category: String,
    val index: Int,
) {
    AVAILABLE("Available", 0),
    NOT_AVAILABLE("Not Available", 1)
}

@Composable
fun EventsScreen(
    navHostController: NavHostController,
    eventsViewModel: EventsViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
) {

    val context = LocalContext.current

    val eventsList by eventsViewModel.eventsList.collectAsState()

    var categoryList = listOf(
        EventsCategoryList.AVAILABLE,
        EventsCategoryList.NOT_AVAILABLE,
    )


    if (eventsList.size != 0) {

        Column {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 10.dp, bottom = 10.dp)
            ) {
                AnimatedVisibility(eventsViewModel.selectedCategoryIndex != null) {
                    Text("sortedBy:-")
                }
                // Category
                categoryList
                    .sortedByDescending {
                        eventsViewModel.selectedCategoryIndex != null
                                && eventsViewModel.selectedCategoryIndex == it.index
                    }
                    .forEach { item ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSystemInDarkTheme()) {
                                    if (
                                        eventsViewModel.selectedCategoryIndex != null
                                        && eventsViewModel.selectedCategoryIndex == item.index
                                    ) White else DarkGray
                                } else {
                                    if (
                                        eventsViewModel.selectedCategoryIndex != null
                                        && eventsViewModel.selectedCategoryIndex == item.index
                                    ) Black else LightGray
                                },
                                contentColor = if (isSystemInDarkTheme()) {
                                    if (
                                        eventsViewModel.selectedCategoryIndex != null
                                        && eventsViewModel.selectedCategoryIndex == item.index
                                    ) Black else White
                                } else {
                                    if (
                                        eventsViewModel.selectedCategoryIndex != null
                                        && eventsViewModel.selectedCategoryIndex == item.index
                                    ) White else Black
                                }
                            ),
                            modifier = Modifier
                                .padding(start = 5.dp, end = 5.dp)
                                .clickable {
                                    eventsViewModel.selectedCategoryIndex = item.index
                                    if (eventsViewModel.selectedCategoryIndex == 0) {
                                        eventsViewModel.getEventsListByAvailableStatus(true)
                                    } else if (eventsViewModel.selectedCategoryIndex == 1) {
                                        eventsViewModel.getEventsListByAvailableStatus(false)
                                    }
                                }
                        ) {
                            Text(
                                text = item.category,
                                modifier = Modifier
                                    .padding(all = 5.dp)
                            )
                        }
                    }

            }


            LazyColumn(
                state = eventsViewModel.lazyListState,
                modifier = Modifier
                    .fillMaxSize(),
            ) {

                items(eventsList.size) { index ->
                    val item = eventsList[index]

                    EventsItem(
                        item = item,
                        context = context,
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope,
                        onItemClick = { clickedEventId ->
                            navHostController.navigate(
                                EventsDestination.EVENTS_DETAIL_SCREEN(clickedEventId)
                            )
                        },
                    )
                }

            }
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (eventsViewModel.eventsListErrorMsg.isNotEmpty()) {
                Text(text = eventsViewModel.eventsListErrorMsg)
            } else {
                Text(text = "No Events Available")
            }
        }
    }


    if (eventsViewModel.eventsListStatus == Status.Loading) {
        LoadingDialog()
    }
}

@Composable
fun EventsItem(
    item: EventsModel?,
    context: Context,
    onItemClick: (String) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 10.dp,
                bottom = 10.dp,
            )
            .clickable {
                onItemClick(item?.eventId ?: "")
            },
        colors = CardDefaults.cardColors(
            containerColor = Green.copy(0.1f) // LightGray.copy(alpha = 0.3f)
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(all = 10.dp),
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
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(10.dp))
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
                                (item?.startingTimeHour ?: 10), (item?.startingTimeMinutes ?: 0)
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