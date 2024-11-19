@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.studentnewsadmin.main.events.ui.screens

import android.icu.util.Calendar
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.android.studentnewsadmin.core.domain.common.formatDateToString
import com.android.studentnewsadmin.core.domain.common.formatTimeToString
import com.android.studentnewsadmin.core.ui.common.OutlinedTextFieldColors
import com.android.studentnewsadmin.main.news.ui.screens.ImageOrVideoPickDialog

@UnstableApi
@Composable
fun UploadEVentsScreen(
    navHostController: NavHostController,
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val calendar = Calendar.getInstance()

    // Starting Date and Time Picker State
    val startingDatePickerState = rememberDatePickerState()
    val startingTimePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE)
    )

    // Ending Date and Time Picker State
    val endingDatePickerState = rememberDatePickerState()
    val endingTimePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE)
    )

    var isImageOrVideoPickDialogOpen by rememberSaveable { mutableStateOf(false) }

    //  Starting Date and Time Dialog
    var isStartingDatePickerDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isStartingTimePickerDialogOpen by rememberSaveable { mutableStateOf(false) }

    // Ending Date and Time Dialog
    var isEndingDatePickerDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isEndingTimePickerDialogOpen by rememberSaveable { mutableStateOf(false) }

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    val uriList = remember { mutableStateListOf<Uri>() }

    // Starting Date and Time
    var startingDate by rememberSaveable { mutableLongStateOf(0L) }
    var startingTimeHour by rememberSaveable { mutableIntStateOf(0) }
    var startingTimeMinute by rememberSaveable { mutableIntStateOf(0) }
    var startingTimeStatus by rememberSaveable { mutableStateOf("") }

    // Ending Date and Time
    var endingDate by remember { mutableLongStateOf(0L) }
    var endingTimeHour by remember { mutableIntStateOf(0) }
    var endingTimeMinute by remember { mutableIntStateOf(0) }
    var endingTimeStatus by remember { mutableStateOf("") }


    val pagerState = rememberPagerState(
        pageCount = {
            uriList.size
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                uriList.add(it)
            }
        }
    )

    BackHandler(uriList.isNotEmpty()) {
        uriList.removeAll(uriList)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Upload Events")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Icon for Navigate Back"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(all = 20.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable(uriList.isEmpty()) {

                            isImageOrVideoPickDialogOpen = true

                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {

                        if (uriList.isNotEmpty()) {
                            HorizontalPager(
                                state = pagerState,
                            ) { page ->
                                val item = uriList[page]

                                val mimeType =
                                    item?.let { url -> context.contentResolver.getType(url) }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {

                                    // Image
                                    if (mimeType.toString().startsWith("image")) {

                                        AsyncImage(
                                            model = item,
                                            contentDescription = "Image For News",
                                            contentScale = ContentScale.FillWidth,
                                            modifier = Modifier
                                                .fillMaxSize()
                                        )
                                    }

                                    // Video
                                    if (mimeType.toString().startsWith("video")) {

                                        val mediaItem = MediaItem.Builder()
                                            .setUri(item)
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

                                        DisposableEffect(
                                            AndroidView(
                                                factory = {
                                                    PlayerView(it).apply {
                                                        player = exoplayer
                                                        useController = true
                                                        imageDisplayMode =
                                                            PlayerView.IMAGE_DISPLAY_MODE_FILL
                                                        setShowFastForwardButton(false)
                                                        setShowRewindButton(false)
                                                        setShowNextButton(false)
                                                        setShowPreviousButton(false)
                                                    }
                                                },
                                                modifier = Modifier
                                                    .fillMaxSize()
                                            )
                                        ) {
                                            onDispose {
                                                exoplayer.release()
                                            }
                                        }
                                    }

                                    // Icon for Remove Uri
                                    IconButton(
                                        onClick = {
                                            uriList.removeAt(page)
                                        },
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(20.dp)
                                            .align(Alignment.TopEnd)
                                            .padding(end = 5.dp, top = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Icon for remove uri",
                                            tint = Color.Black,
                                            modifier = Modifier
                                                .background(color = Color.White)
                                        )
                                    }
                                }
                            }
                        }

                        if (uriList.isEmpty()) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Icon for Image Picker",
                                modifier = Modifier
                                    .align(alignment = Alignment.Center)
                            )
                        }
                    }
                }

                if (uriList.isNotEmpty()) {

                    FloatingActionButton(
                        onClick = {
                            isImageOrVideoPickDialogOpen = true
                        },
                        containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
                        contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        modifier = Modifier
                            .clip(CircleShape)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Icon for Add Image or Video",
                        )
                    }

                }

            }

            Spacer(modifier = Modifier.height(5.dp))

            // Pager Indicator
            Row(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
            ) {
                repeat(uriList.size) { index ->
                    Card(
                        modifier = Modifier
                            .width(if (pagerState.currentPage == index) 18.dp else 15.dp)
                            .height(if (pagerState.currentPage == index) 18.dp else 15.dp)
                            .clip(shape = CircleShape)
                            .padding(all = 5.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (pagerState.currentPage == index) {
                                if (isSystemInDarkTheme()) Color.LightGray else Color.Black
                            } else {
                                if (isSystemInDarkTheme()) Color.Black else Color.LightGray
                            }
                        )
                    ) {

                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = title,
                onValueChange = {
                    title = it
                },
                label = {
                    Text(text = "Title")
                },
                colors = OutlinedTextFieldColors(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp)
                    .focusRequester(focusRequester)
            )

            TextField(
                value = description,
                onValueChange = {
                    description = it
                },
                label = {
                    Text(text = "Description")
                },
                colors = OutlinedTextFieldColors(),
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 250.dp)
                    .padding(all = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Starting

            // Starting Date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Icon for Date"
                )
                Text(
                    text = "Starting Date${
                        if (startingDate != 0L) ":- ${
                            formatDateToString(
                                startingDate
                            )
                        }" else ""
                    }"
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    isStartingDatePickerDialogOpen = true
                }) {
                    Icon(
                        imageVector = if (startingDate == 0L)
                            Icons.Default.Add else Icons.Outlined.Edit,
                        contentDescription = "Icon for Adding Date"
                    )
                }
            }

            // Starting Time
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = "Icon for Date"
                )

                if (startingTimeHour != 0) {
                    val time =
                        formatTimeToString(startingTimeHour, startingTimeMinute)
                    startingTimeStatus = getAmPmFromTimeString(time)

                    Text(
                        text = "Starting Time:- ${time.dropLast(2)} $startingTimeStatus"
                    )
                } else {
                    Text(text = "Starting Time")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    isStartingTimePickerDialogOpen = true
                }) {
                    Icon(
                        imageVector = if (startingTimeHour == 0)
                            Icons.Default.Add else Icons.Outlined.Edit,
                        contentDescription = "Icon for Adding Date"
                    )
                }
            }


            // Ending

            // Ending Date
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Icon for Date"
                )
                Text(
                    text = "Ending Date${
                        if (endingDate != 0L) ":- ${
                            formatDateToString(
                                endingDate
                            )
                        }" else ""
                    }"
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    isEndingDatePickerDialogOpen = true
                }) {
                    Icon(
                        imageVector = if (endingDate == 0L)
                            Icons.Default.Add else Icons.Outlined.Edit,
                        contentDescription = "Icon for Adding Date"
                    )
                }
            }
            // Ending Time
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = "Icon for Date"
                )

                if (endingTimeHour != 0) {
                    val time = formatTimeToString(endingTimeHour, endingTimeMinute)
                    endingTimeStatus = getAmPmFromTimeString(time)

                    Text(
                        text = "Ending Time:- ${time.dropLast(2)} $endingTimeStatus"
                    )
                } else {
                    Text(text = "Ending Time")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    isEndingTimePickerDialogOpen = true
                }) {
                    Icon(
                        imageVector = if (endingTimeHour == 0)
                            Icons.Default.Add else Icons.Outlined.Edit,
                        contentDescription = "Icon for Adding Date"
                    )
                }
            }


        }


        // Starting

        // Starting Date Picker
        if (isStartingDatePickerDialogOpen) {
            ModalDatePickerDialog(
                state = startingDatePickerState,
                onDateSelected = { thisStartingDate ->
                    thisStartingDate?.let {
                        startingDate = it
                    }
                },
                onDismiss = {
                    isStartingDatePickerDialogOpen = false
                }
            )
        }
        // Starting Time Picker
        if (isStartingTimePickerDialogOpen) {
            ModalTimePickerDialog(
                state = startingTimePickerState,
                onTimeSelected = { hour, minute ->
                    startingTimeHour = hour
                    startingTimeMinute = minute
                },
                onDismiss = {
                    isStartingTimePickerDialogOpen = false
                }
            )
        }

        // Ending

        // Ending Date Picker
        if (isEndingDatePickerDialogOpen) {
            ModalDatePickerDialog(
                state = endingDatePickerState,
                onDateSelected = { thisEndingDate ->
                    thisEndingDate?.let {
                        endingDate = it
                    }
                },
                onDismiss = {
                    isEndingDatePickerDialogOpen = false
                }
            )
        }
        // Ending Time Picker
        if (isEndingTimePickerDialogOpen) {
            ModalTimePickerDialog(
                state = endingTimePickerState,
                onTimeSelected = { hour, minute ->
                    endingTimeHour = hour
                    endingTimeMinute = minute
                },
                onDismiss = {
                    isEndingTimePickerDialogOpen = false
                }
            )
        }



        if (isImageOrVideoPickDialogOpen) {
            ImageOrVideoPickDialog(
                onImageClick = {
                    galleryLauncher.launch("image/*")
                },
                onVideoClick = {
                    galleryLauncher.launch("video/*")
                },
                onDismiss = {
                    isImageOrVideoPickDialogOpen = false
                }
            )
        }

    }


}

@Composable
fun ModalDatePickerDialog(
    state: DatePickerState,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDateSelected.invoke(state.selectedDateMillis)
                onDismiss.invoke()
            }) {
                Text(text = "Confirm")
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        colors = DatePickerDefaults.colors(
            containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.White
        )
    ) {
        DatePicker(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun ModalTimePickerDialog(
    state: TimePickerState,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
            ) {
                TimePicker(
                    state = state,
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    TextButton(onClick = {
                        onDismiss.invoke()
                    }) {
                        Text(text = "Cancel")
                    }

                    TextButton(onClick = {
                        onTimeSelected.invoke(state.hour, state.minute)
                        onDismiss.invoke()
                    }) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }
}

fun getAmPmFromTimeString(timeString: String): String {
    return timeString.takeLast(2)
}