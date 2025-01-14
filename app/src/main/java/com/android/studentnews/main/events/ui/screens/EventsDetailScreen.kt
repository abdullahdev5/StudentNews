@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.main.events.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.android.studentnews.core.domain.common.formatDateToDay
import com.android.studentnews.core.domain.common.formatDateToMonthName
import com.android.studentnews.core.domain.common.formatDateToYear
import com.android.studentnews.core.domain.common.formatTimeToHour
import com.android.studentnews.core.domain.common.formatTimeToMinutes
import com.android.studentnews.core.domain.common.formatTimeToString
import com.android.studentnews.core.domain.common.getUrlOfImageNotVideo
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.core.ui.composables.ButtonColors
import com.android.studentnews.main.account.ui.viewmodel.AccountViewModel
import com.android.studentnews.main.events.EVENT_ID
import com.android.studentnews.main.events.domain.destination.EventsDestination
import com.android.studentnews.main.events.ui.viewModels.EventsDetailViewModel
import com.android.studentnews.main.news.ui.screens.UrlListPagerIndicator
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.Red
import com.android.studentnews.ui.theme.White
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.Timestamp

@OptIn(ExperimentalSharedTransitionApi::class)
@UnstableApi
@Composable
fun EventsDetailScreen(
    eventId: String,
    isComeForRegistration: Boolean,
    notificationId: Int?,
    navHostController: NavHostController,
    eventsDetailViewModel: EventsDetailViewModel,
    accountViewModel: AccountViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
) {
    LaunchedEffect(Unit) {
        eventsDetailViewModel.getEventById(eventId)
        eventsDetailViewModel.getIsEventSaved(eventId)
        eventsDetailViewModel.getIsEventRegistered(eventId)
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val eventById by eventsDetailViewModel.eventById.collectAsStateWithLifecycle()
    val currentUser by accountViewModel.currentUser.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(pageCount = { eventById?.urlList?.size ?: 0 })

    val isEventRegistered = remember {
        derivedStateOf {
            eventsDetailViewModel.isEventRegistered
        }
    }.value

    var isEventSaved = remember(eventsDetailViewModel.isEventSaved) {
        derivedStateOf {
            eventsDetailViewModel.isEventSaved ?: false
        }
    }.value

    val isEventFailedToLoad = remember(eventsDetailViewModel.eventByIdStatus) {
        derivedStateOf {
            eventsDetailViewModel.eventByIdStatus == Status.FAILED
        }
    }.value


    var isStartingDateExpanded by rememberSaveable { mutableStateOf(true) }

    var isStartingTimeExpanded by rememberSaveable { mutableStateOf(true) }

    var isEndingDateExpanded by rememberSaveable { mutableStateOf(true) }

    var isEndingTimeExpanded by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(currentUser) {
        if (isComeForRegistration) {
            currentUser?.let { thisCurrentUser ->
                if (isEventRegistered == false) {
                    navHostController.navigate(
                        EventsDestination
                            .BottomSheetDestinations
                            .REGISTRATION_EVENTS_BOTTOM_SHEET_DESTINATION +
                                "/$EVENT_ID=$eventId"
                    )
                }
                notificationId?.let { thisId ->
                    eventsDetailViewModel.cancelNotification(thisId)
                }
            }
        }
    }

    BackHandler(
        enabled = sharedTransitionScope.isTransitionActive,
        onBack = {}
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = scrollState.value > 550,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val imageRequest = ImageRequest.Builder(context)
                                .data(getUrlOfImageNotVideo(eventById?.urlList ?: emptyList()))
                                .crossfade(true)
                                .build()

                            AsyncImage(
                                model = imageRequest,
                                contentDescription = "Event Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )

                            Text(
                                text = eventById?.title ?: "",
                                fontSize = FontSize.MEDIUM.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!sharedTransitionScope.isTransitionActive) {
                            navHostController.navigateUp()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Icon foe Navigate Back"
                        )
                    }
                },
                actions = {
                    if (!isEventFailedToLoad) {
                        IconButton(onClick = {
                            isEventSaved = !isEventSaved

                            eventById?.let {
                                val event = EventsModel(
                                    title = it.title,
                                    description = it.description,
                                    eventId = it.eventId,
                                    address = it.address,
                                    startingDate = it.startingDate,
                                    startingTimeHour = it.startingTimeHour,
                                    startingTimeMinutes = it.startingTimeMinutes,
                                    startingTimeStatus = it.startingTimeStatus,
                                    endingDate = it.endingDate,
                                    endingTimeHour = it.endingTimeHour,
                                    endingTimeMinutes = it.endingTimeMinutes,
                                    endingTimeStatus = it.endingTimeStatus,
                                    timestamp = Timestamp.now(),
                                    urlList = it.urlList,
                                )

                                if (isEventSaved) {
                                    eventsDetailViewModel.onEventSave(event = event)
                                } else {
                                    eventsDetailViewModel.onEventRemoveFromSave(event = event)
                                }
                            }

                        }) {
                            AnimatedVisibility(isEventSaved) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Icon of Saved Event"
                                )
                            }
                            AnimatedVisibility(!isEventSaved) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Icon of UnSaved Event"
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isEventRegistered != null
                        && isEventRegistered == false
                        && eventById?.isAvailable == true,
                enter = slideInVertically(
                    animationSpec = tween(1000)
                ) { it },
                exit = slideOutVertically(
                    animationSpec = tween(1000)
                ) { -it }
            ) {
                Button(
                    onClick = {
                        if (isEventRegistered == false) {
                            navHostController.navigate(
                                EventsDestination
                                    .BottomSheetDestinations
                                    .REGISTRATION_EVENTS_BOTTOM_SHEET_DESTINATION +
                                        "/$EVENT_ID=$eventId"
                            )
                        }
                    },
                    enabled = isEventRegistered == false
                            && eventById?.isAvailable ?: true,
                    colors = ButtonColors(
                        containerColor = Green,
                        contentColor = White,
                        disableContainerColor = Gray,
                        disableContentColor = White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp)
                        .background(color = Color.Transparent)
                        .navigationBarsPadding()
                ) {
                    Text(
                        text = if (isEventRegistered == true) {
                            "Registered"
                        } else if (!(eventById?.isAvailable ?: true)) {
                            "UnAvailable"
                        } else "Register"
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateStartPadding(LayoutDirection.Rtl),
                )
                .then(
                    if (!sharedTransitionScope.isTransitionActive) {
                        Modifier.verticalScroll(scrollState)
                    } else Modifier
                )
        ) {

            with(sharedTransitionScope) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .sharedElement(
                            state = rememberSharedContentState(key = "image/$eventId"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(10.dp))
                        ),
                ) {

                    if (isEventFailedToLoad) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Icon for When Unable to Load the Image",
                            tint = Red,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 20.dp)
                        )
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) { page ->

                        val item = eventById?.urlList?.get(page)

                        Box {

                            if (item?.contentType.toString().startsWith("image/")) {
                                val imageRequest = ImageRequest.Builder(context)
                                    .data(item?.url ?: "")
                                    .crossfade(true)
                                    .build()

                                SubcomposeAsyncImage(
                                    model = imageRequest,
                                    contentDescription = "Events Images",
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
                                    error = {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .fillMaxSize()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Error,
                                                contentDescription = "Icon for When Unable to Load the Image",
                                                tint = Red
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                            }

                            if (item?.contentType.toString().startsWith("video/")) {
                                val mediaItem = MediaItem.Builder()
                                    .setUri(item?.url ?: "")
                                    .build()

                                val exoplayer = remember(context, mediaItem) {
                                    ExoPlayer.Builder(context)
                                        .build()
                                        .apply {
                                            setMediaItem(mediaItem)
                                            prepare()
                                        }
                                }

                                var isPlaying by remember { mutableStateOf(false) }

                                DisposableEffect(
                                    Box {
                                        AndroidView(
                                            factory = { context ->
                                                PlayerView(context)
                                                    .apply {
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
                                        override fun onIsPlayingChanged(mIsPlaying: Boolean) {
                                            if (mIsPlaying) {
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
            }


            if (!isEventFailedToLoad) {
                // Title, desc Container
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp)
                ) {

                    val customLineBreak = LineBreak(
                        strategy = LineBreak.Strategy.HighQuality,
                        strictness = LineBreak.Strictness.Strict,
                        wordBreak = LineBreak.WordBreak.Phrase
                    )

                    with(sharedTransitionScope) {
                        // Title
                        Text(
                            text = eventById?.title ?: "",
                            style = TextStyle(
                                fontSize = FontSize.LARGE.sp,
                                fontWeight = FontWeight.Bold,
                                lineBreak = customLineBreak,
                                hyphens = Hyphens.Auto,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 20.dp)
                                .sharedElement(
                                    state = rememberSharedContentState(key = "title/$eventId"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                )
                        )
                    }

                    SelectionContainer {
                        Text(
                            text = eventById?.description ?: "",
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp),
                    ) {
                        Text(
                            text = "Address",
                            style = TextStyle(
                                fontSize = FontSize.MEDIUM.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = if (!eventById?.address.isNullOrEmpty()) eventById?.address
                                ?: "" else "No Address Available",
                            style = TextStyle(
                                fontSize = FontSize.SMALL.sp,
                            )
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(all = 20.dp)
                    ) {
                        eventById?.isAvailable?.let { isAvailable ->
                            Text(
                                text = "Status",
                                style = TextStyle(
                                    fontSize = FontSize.MEDIUM.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = buildAnnotatedString {
                                    if (isAvailable) {
                                        append("Available")
                                        if (isEventRegistered == true) {
                                            append(" and You Already Registered this Event")
                                        }
                                    } else {
                                        append("UnAvailable")
                                    }
                                },
                                fontSize = FontSize.SMALL.sp
                            )
                        }
                    }

                    Column {

                        eventById?.startingDate?.let {
                            // Starting Date
                            DateContainer(
                                title = "Starting Date",
                                isExpanded = isStartingDateExpanded,
                                dateMillis = eventById?.startingDate ?: 0L,
                                onClick = {
                                    isStartingDateExpanded = !isStartingDateExpanded
                                }
                            )
                        }

                        if (eventById?.startingTimeHour != null && eventById?.startingTimeMinutes != null) {
                            // Starting Time
                            TimeContainer(
                                title = "Starting Time",
                                time = "${
                                    formatTimeToString(
                                        (eventById?.startingTimeHour ?: 10),
                                        (eventById?.startingTimeMinutes ?: 0)
                                    ).dropLast(2)
                                } ${eventById?.startingTimeStatus}",
                                timeHour = eventById?.startingTimeHour ?: 10,
                                timeMinutes = eventById?.startingTimeMinutes ?: 0,
                                timeStatus = eventById?.startingTimeStatus ?: "",
                                isExpanded = isStartingTimeExpanded,
                                onClick = {
                                    isStartingTimeExpanded = !isStartingTimeExpanded
                                }
                            )
                        }

                        eventById?.endingDate?.let {
                            // Ending Date
                            DateContainer(
                                title = "Ending Date",
                                dateMillis = eventById?.endingDate ?: 0L,
                                isExpanded = isEndingDateExpanded,
                                onClick = {
                                    isEndingDateExpanded = !isEndingDateExpanded
                                }
                            )
                        }

                        if (eventById?.endingTimeHour != null && eventById?.endingTimeMinutes != null) {
                            // Ending Time
                            TimeContainer(
                                title = "Ending Time",
                                time = "${
                                    formatTimeToString(
                                        (eventById?.endingTimeHour ?: 10),
                                        (eventById?.endingTimeMinutes ?: 0)
                                    ).dropLast(2)
                                } ${eventById?.endingTimeStatus}",
                                timeHour = eventById?.endingTimeHour ?: 10,
                                timeMinutes = eventById?.endingTimeMinutes ?: 0,
                                timeStatus = eventById?.endingTimeStatus ?: "",
                                isExpanded = isEndingTimeExpanded,
                                onClick = {
                                    isEndingTimeExpanded = !isEndingTimeExpanded
                                }
                            )
                        }

                    }

                }

            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                )
            }

        }

    }
}

@Composable
fun DateContainer(
    title: String,
    dateMillis: Long,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {

    val day = formatDateToDay(dateMillis)
    val monthName = formatDateToMonthName(dateMillis)
    val year = formatDateToYear(dateMillis)

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
                .pointerInput(true) {
                    detectTapGestures(
                        onTap = {
                            onClick.invoke()
                        }
                    )
                }
        ) {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = "Ico for Date",
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = FontSize.MEDIUM.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                AnimatedVisibility(
                    visible = !isExpanded,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "$day $monthName $year",
                        style = TextStyle(
                            fontSize = FontSize.SMALL.sp
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                ExposedDropdownMenuDefaults.TrailingIcon(isExpanded)
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            modifier = Modifier
                .padding(all = 5.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Month Name
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Gray,
                    )
                ) {
                    Text(
                        text = monthName,
                        color = White,
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 10.dp,
                                bottom = 10.dp
                            )
                    )
                }

                // Day
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Gray,
                    )
                ) {
                    Text(
                        text = day.toString(),
                        color = White,
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 10.dp,
                                bottom = 10.dp
                            )
                    )
                }

                // Year
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Green,
                    )
                ) {
                    Text(
                        text = year.toString(),
                        color = White,
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 10.dp,
                                bottom = 10.dp
                            )
                    )
                }
            }
        }
    }

}

@Composable
fun TimeContainer(
    title: String,
    time: String,
    timeHour: Int,
    timeMinutes: Int,
    timeStatus: String,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {

    val hour = formatTimeToHour(timeHour)
    val minutes = formatTimeToMinutes(timeMinutes)

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
                .pointerInput(true) {
                    detectTapGestures(
                        onTap = {
                            onClick.invoke()
                        }
                    )
                }
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer,
                contentDescription = "Ico for Time",
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = FontSize.MEDIUM.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                AnimatedVisibility(
                    visible = !isExpanded,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = time,
                        style = TextStyle(
                            fontSize = FontSize.SMALL.sp
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                ExposedDropdownMenuDefaults.TrailingIcon(isExpanded)
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            modifier = Modifier
                .padding(all = 5.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Hour
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Gray,
                    )
                ) {
                    Text(
                        text = hour,
                        color = White,
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 10.dp,
                                bottom = 10.dp
                            )
                    )
                }

                Text(
                    text = ":",
                    fontSize = FontSize.LARGE.sp
                )

                // Minutes
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Gray,
                    )
                ) {
                    Text(
                        text = minutes,
                        color = White,
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 10.dp,
                                bottom = 10.dp
                            )
                    )
                }

                // Status, am or pm
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Green,
                    )
                ) {
                    Text(
                        text = timeStatus.uppercase(),
                        color = White,
                        modifier = Modifier
                            .padding(
                                start = 20.dp,
                                end = 20.dp,
                                top = 10.dp,
                                bottom = 10.dp
                            )
                    )
                }
            }
        }
    }
}