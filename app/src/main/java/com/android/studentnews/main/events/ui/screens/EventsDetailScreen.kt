@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnews.main.events.ui.screens

import android.icu.util.Calendar
import android.view.MotionEvent
import android.widget.CalendarView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.android.studentnews.auth.domain.RegistrationDropDownMenuLists
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.auth.ui.CityDropDownMenu
import com.android.studentnews.auth.ui.DegreeDropDownMenu
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.common.formatDateToDay
import com.android.studentnews.core.domain.common.formatDateToMonthInt
import com.android.studentnews.core.domain.common.formatDateToMonthName
import com.android.studentnews.core.domain.common.formatDateToYear
import com.android.studentnews.core.domain.common.formatTimeToHour
import com.android.studentnews.core.domain.common.formatTimeToMinutes
import com.android.studentnews.core.domain.constants.FontSize
import com.android.studentnews.main.events.ui.viewModels.EventsViewModel
import com.android.studentnews.main.news.ui.screens.UrlListPagerIndicator
import com.android.studentnews.ui.theme.Black
import com.android.studentnews.ui.theme.Green
import com.android.studentnews.ui.theme.White
import com.android.studentnews.core.domain.common.formatTimeToString
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.core.ui.common.ButtonColors
import com.android.studentnews.core.ui.common.LoadingDialog
import com.android.studentnews.core.ui.common.OutlinedTextFieldColors
import com.android.studentnews.core.ui.components.TextFieldComponent
import com.android.studentnews.main.events.domain.models.EventsBookingModel
import com.android.studentnews.ui.theme.Gray
import com.android.studentnews.ui.theme.Red
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@UnstableApi
@Composable
fun EventsDetailScreen(
    eventId: String,
    navHostController: NavHostController,
    eventsViewModel: EventsViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
) {
    LaunchedEffect(Unit) {
        eventsViewModel.getEventById(eventId)
        eventsViewModel.getSavedEventById(eventId)
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val bookingSheetState = rememberModalBottomSheetState()

    val eventById by eventsViewModel.eventById.collectAsStateWithLifecycle()
    val currentUser by eventsViewModel.currentUser.collectAsStateWithLifecycle()
    val savedEventById by eventsViewModel.savedEventById.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(pageCount = { eventById?.urlList?.size ?: 0 })

    val userIdsListFromBookings = remember(eventById) {
        eventById?.bookings?.map {
            it.userId
        } ?: emptyList()
    }

    var isSaved by remember(savedEventById) {
        mutableStateOf(
            savedEventById?.eventId == eventId
        )
    }


    var isStartingDateExpanded by rememberSaveable { mutableStateOf(true) }

    var isStartingTimeExpanded by rememberSaveable { mutableStateOf(false) }

    var isEndingDateExpanded by rememberSaveable { mutableStateOf(true) }

    var isEndingTimeExpanded by rememberSaveable { mutableStateOf(false) }

    var isBookingSheetVisible by rememberSaveable { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Icon foe Navigate Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSaved = !isSaved

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
                                bookings = it.bookings,
                            )

                            if (isSaved) {
                                eventsViewModel.onEventSave(event = event)
                            } else {
                                eventsViewModel.onEventRemoveFromSave(event = event)
                            }
                        }

                    }) {
                        AnimatedVisibility(isSaved) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Icon of Saved Event"
                            )
                        }
                        AnimatedVisibility(!isSaved) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Icon of UnSaved Event"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            Button(
                onClick = {
                    if (!userIdsListFromBookings.contains(currentUser?.uid)) {
                        scope.launch {
                            bookingSheetState.show()
                        }.invokeOnCompletion {
                            if (bookingSheetState.isVisible) {
                                isBookingSheetVisible = true
                            }
                        }
                    } else {
                        scope.launch {
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = "This Event is Already Registered!"
                                    )
                                )
                        }
                    }
                },
                enabled = !userIdsListFromBookings.contains(currentUser?.uid),
                colors = ButtonColors(
                    containerColor = Green,
                    contentColor = White
                )
            ) {
                if (userIdsListFromBookings.contains(currentUser?.uid)) {
                    Text(text = "Already Registered")
                } else {
                    Text(text = "Register")
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
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
                            text = if (isAvailable) "Available" else "Not Available",
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

        }


        if (isBookingSheetVisible) {
            EventBookingSheet(
                sheetState = bookingSheetState,
                currentUser = currentUser,
                onDismiss = {
                    scope.launch {
                        bookingSheetState.hide()
                    }.invokeOnCompletion {
                        if (!bookingSheetState.isVisible) {
                            isBookingSheetVisible = false
                        }
                    }
                },
                onBook = { userName, userDegree, userPhoneNumber, userCity, userAddress ->

                    val bookingEvent = EventsBookingModel(
                        userId = currentUser?.uid,
                        userName = userName,
                        userDegree = userDegree,
                        userPhoneNumber = userPhoneNumber,
                        userCity = userCity,
                        userAddress = userAddress,
                        userProfilePic = currentUser?.profilePic,
                        userProfilePicBgColor = currentUser?.profilePicBgColor,
                        timestamp = Timestamp.now()
                    )

                    eventsViewModel.onEventBook(
                        eventId = eventId,
                        eventsBookingModel = bookingEvent,
                    )
                }
            )
        }

        if (eventsViewModel.eventBookingStatus == Status.Loading) {
            LoadingDialog()
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

                Text(
                    text = "$day $monthName $year",
                    style = TextStyle(
                        fontSize = FontSize.SMALL.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
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
                Text(
                    text = time,
                    style = TextStyle(
                        fontSize = FontSize.SMALL.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
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
                .padding(all = 20.dp)
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

@Composable
fun EventBookingSheet(
    sheetState: SheetState,
    currentUser: UserModel?,
    onBook: (String, String, String, String, String) -> Unit,
    onDismiss: () -> Unit,
) {

    val scrollState = rememberScrollState()

    var userName by rememberSaveable {
        mutableStateOf(currentUser?.registrationData?.name ?: "")
    }
    var userDegree by rememberSaveable {
        mutableStateOf(currentUser?.registrationData?.degree ?: "")
    }
    var userCountryCode by rememberSaveable {
        mutableStateOf(currentUser?.registrationData?.countryCode ?: "")
    }
    var userNumber by rememberSaveable {
        mutableStateOf(currentUser?.registrationData?.number ?: "")
    }
    var userCity by rememberSaveable {
        mutableStateOf(currentUser?.registrationData?.city ?: "")
    }
    var userAddress by remember {
        mutableStateOf(currentUser?.registrationData?.address ?: "")
    }


    var isDegreeDropDownMenuOpen by rememberSaveable { mutableStateOf(false) }
    val degreeList = RegistrationDropDownMenuLists.degreeList
    var isCityDropDownMenuOpen by rememberSaveable { mutableStateOf(false) }
    val cityList = RegistrationDropDownMenuLists.cityList


    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {

            Text(
                text = "Book Event",
                style = TextStyle(
                    fontSize = FontSize.LARGE.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(all = 20.dp)
            )


            // Name
            Text(
                text = "Your Name",
                color = Gray,
                modifier = Modifier
                    .padding(start = 20.dp, top = 20.dp)
            )
            TextFieldComponent(
                value = userName,
                onValueChange = {
                    userName = it
                },
                label = {
                    Text(text = "Your Name")
                },
                colors = OutlinedTextFieldColors(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Icon for Person Name"
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Degree
            Text(
                text = "Your Degree",
                color = Gray,
                modifier = Modifier
                    .padding(start = 20.dp)
            )
            // Degree
            DegreeDropDownMenu(
                degree = userDegree,
                degreeList = degreeList,
                expanded = isDegreeDropDownMenuOpen,
                onExpandedChange = {
                    isDegreeDropDownMenuOpen = !isDegreeDropDownMenuOpen
                },
                onItemCLick = {
                    userDegree = it
                },
                onDismiss = {
                    isDegreeDropDownMenuOpen = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Phone Number
            Text(
                text = "Your Phone Number",
                color = Gray,
                modifier = Modifier
                    .padding(start = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country Code
                    TextFieldComponent(
                        value = userCountryCode,
                        onValueChange = {
                            userCountryCode = if (userCountryCode.length < 7) {
                                it
                            } else {
                                if (it.length < userCountryCode.length) {
                                    it
                                } else {
                                    userCountryCode
                                }
                            }
                        },
                        placeholder = {
                            Text(text = "Code")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        colors = OutlinedTextFieldColors(),
                        singleLine = true,
                        isError = if (!userCountryCode.startsWith("+") || userCountryCode.length < 2) true else false,
                        modifier = Modifier
                            .width(100.dp)
                            .height(53.dp)
                            .padding(start = 20.dp, end = 5.dp)
                    )

                    // Number
                    TextFieldComponent(
                        value = userNumber,
                        onValueChange = {
                            userNumber = if (userNumber.length < 15) {
                                it
                            } else {
                                if (it.length < userNumber.length) {
                                    it
                                } else {
                                    userNumber
                                }
                            }
                        },
                        placeholder = {
                            Text(text = "Phone Number")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldColors(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Icon for Phone Number"
                            )
                        },
                        singleLine = true,
                        isError = if (userNumber.isNotEmpty() && userNumber.length < 10 || !userNumber.isDigitsOnly()) true else false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 20.dp)
                    )

                }
                Text(
                    text = if (!userCountryCode.startsWith("+"))
                        "Code must be start with '+' like '+92' "
                    else if (userCountryCode.length < 2)
                        "Code must be at least 2 character long"
                    else if (userNumber.isNotEmpty() && userNumber.length < 10) "Phone Number is Too Short"
                    else if (!userNumber.isDigitsOnly()) "Please Enter only Digits in Phone Number"
                    else "",
                    color = Red,
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                        .align(alignment = Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your City",
                color = Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp),
            )
            // City
            CityDropDownMenu(
                city = userCity,
                cityList = cityList,
                expanded = isCityDropDownMenuOpen,
                onExpandedChange = {
                    isCityDropDownMenuOpen = !isCityDropDownMenuOpen
                },
                onItemCLick = {
                    userCity = it
                },
                onDismiss = {
                    isCityDropDownMenuOpen = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Address
            Text(
                text = "Your Address",
                color = Gray,
                modifier = Modifier
                    .padding(start = 20.dp)
            )
            TextFieldComponent(
                value = userAddress,
                onValueChange = {
                    userAddress = it
                },
                label = {
                    Text(text = "Address")
                },
                colors = OutlinedTextFieldColors(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.AddLocation,
                        contentDescription = "Icon for address"
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(all = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }

                // userName, userDegree, userPhoneNumber, userCity, userAddress
                TextButton(
                    onClick = {
                        onBook.invoke(
                            userName,
                            userDegree,
                            (userCountryCode + userNumber),
                            userCity,
                            userAddress
                        )
                        onDismiss.invoke()
                    },
                    enabled = userName.isNotEmpty() && userDegree.isNotEmpty() &&
                            userCountryCode.startsWith("+") && userCountryCode.length >= 2
                            && userNumber.length >= 10 && userNumber.isDigitsOnly()
                            && userCity.isNotEmpty() && userAddress.isNotEmpty()
                ) {
                    Text(text = "Book")
                }
            }

        }

    }
}