package com.android.studentnewsadmin.main.events.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.studentnewsadmin.core.domain.common.formatDateToString
import com.android.studentnewsadmin.core.domain.common.formatTimeToString
import com.android.studentnewsadmin.core.ui.common.OutlinedTextFieldColors
import com.android.studentnewsadmin.main.events.domain.models.EditEventModel
import com.android.studentnewsadmin.main.events.domain.navType.EditEventNavType
import com.android.studentnewsadmin.main.events.ui.viewModels.EventsViewModel
import com.android.studentnewsadmin.ui.theme.Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    eventId: String,
    args: EditEventModel,
    eventsViewModel: EventsViewModel,
    navHostController: NavHostController,
) {

    var comeFor by rememberSaveable { mutableStateOf(START) } // Like for Starting Date & Time OR Ending

    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    var focusManager = LocalFocusManager.current

    // Date Picker State
    val startingDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = args.startingDate
    )
    val endingDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = args.endingDate
    )
    // Time Picker State
    val startingTimePickerState = rememberTimePickerState(
        initialHour = args.startingTimeHour,
        initialMinute = args.startingTimeMinutes,
    )
    val endingTimePickerState = rememberTimePickerState(
        initialHour = args.endingTimeHour,
        initialMinute = args.endingTimeMinutes,
    )

    //  Starting Date and Time Dialog
    var isDatePickerDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isTimePickerDialogOpen by rememberSaveable { mutableStateOf(false) }

    var title by rememberSaveable { mutableStateOf(args.title) }
    var description by rememberSaveable { mutableStateOf(args.description) }
    var address by rememberSaveable { mutableStateOf(args.address) }
    var isAvailable by rememberSaveable { mutableStateOf(args.isAvailable) }

    // Starting Date and Time
    var startingDate by rememberSaveable { mutableLongStateOf(args.startingDate) }
    var startingTimeHour by rememberSaveable { mutableIntStateOf(args.startingTimeHour) }
    var startingTimeMinutes by rememberSaveable { mutableIntStateOf(args.startingTimeMinutes) }
    var startingTimeStatus by rememberSaveable { mutableStateOf(args.endingTimeStatus) }

    // Ending Date and Time
    var endingDate by rememberSaveable { mutableLongStateOf(args.endingDate) }
    var endingTimeHour by rememberSaveable { mutableIntStateOf(args.endingTimeHour) }
    var endingTimeMinutes by rememberSaveable { mutableIntStateOf(args.endingTimeMinutes) }
    var endingTimeStatus by rememberSaveable { mutableStateOf(args.endingTimeStatus) }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Edit Event")
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
        bottomBar = {
            Button(
                onClick = {
                    eventsViewModel
                        .onEventEdit(
                            eventId = eventId,
                            title = title,
                            description = description,
                            address = address,
                            startingDate = startingDate,
                            startingTimeHour = startingTimeHour,
                            startingTimeMinutes = startingTimeMinutes,
                            startingTimeStatus = startingTimeStatus,
                            endingDate = endingDate,
                            endingTimeHour = endingTimeHour,
                            endingTimeMinutes = endingTimeMinutes,
                            endingTimeStatus = endingTimeStatus,
                            isAvailable = isAvailable,
                            context = context
                        )
                },
                enabled = title.isNotEmpty() && description.isNotEmpty() && address.isNotEmpty()
                        && startingDate != 0L && startingTimeHour != 0 && endingDate != 0L
                        && endingTimeHour != 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
                    .navigationBarsPadding()
            ) {
                Text(text = "Edit")
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

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

            TextField(
                value = address,
                onValueChange = {
                    address = it
                },
                label = {
                    Text(text = "Address")
                },
                colors = OutlinedTextFieldColors(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 20.dp)
            ) {
                Column {
                    Text(
                        text = "Status",
                        color = Color.Gray
                    )
                    Text(
                        text = if (isAvailable) "Available" else "Not Available"
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    isAvailable = !isAvailable
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Icon for Changing Status"
                    )
                }
            }

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
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(text = "Starting Date")
                    AnimatedVisibility(startingDate != 0L) {
                        Text(text = formatDateToString(startingDate))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    isDatePickerDialogOpen = true
                    comeFor = START
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

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(text = "Starting Time")

                    AnimatedVisibility(startingTimeHour != 0) {
                        val time =
                            formatTimeToString(startingTimeHour, startingTimeMinutes)
                        startingTimeStatus = getAmPmFromTimeString(time)

                        Text(
                            text = "${time.dropLast(2)} $startingTimeStatus"
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    isTimePickerDialogOpen = true
                    comeFor = START
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(text = "Ending Date")

                    AnimatedVisibility(endingDate != 0L) {
                        Text(text = formatDateToString(endingDate))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    isDatePickerDialogOpen = true
                    comeFor = END
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

                Column {
                    Text(text = "Ending Time")

                    AnimatedVisibility(endingTimeHour != 0) {
                        val time = formatTimeToString(endingTimeHour, endingTimeMinutes)
                        endingTimeStatus = getAmPmFromTimeString(time)

                        Text(
                            text = "${time.dropLast(2)} $endingTimeStatus"
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    isTimePickerDialogOpen = true
                    comeFor = END
                }) {
                    Icon(
                        imageVector = if (endingTimeHour == 0)
                            Icons.Default.Add else Icons.Outlined.Edit,
                        contentDescription = "Icon for Adding Date"
                    )
                }
            }

        }


        // Date Picker
        if (isDatePickerDialogOpen) {
            ModalDatePickerDialog(
                state = if (comeFor == START)
                    startingDatePickerState else endingDatePickerState,
                onDateSelected = { thisStartingDate ->
                    thisStartingDate?.let {
                        if (comeFor == START) {
                            startingDate = it
                        } else if (comeFor == END) {
                            endingDate = it
                        }
                    }
                },
                comeFor = comeFor,
                onDismiss = {
                    isDatePickerDialogOpen = false
                }
            )
        }
        // Time Picker
        if (isTimePickerDialogOpen) {
            ModalTimePickerDialog(
                state = if (comeFor == START)
                    startingTimePickerState else endingTimePickerState,
                onTimeSelected = { hour, minute ->
                    if (hour != 0) {
                        if (comeFor == START) {
                            startingTimeHour = hour
                            startingTimeMinutes = minute
                        } else if (comeFor == END) {
                            endingTimeHour = hour
                            endingTimeMinutes = minute
                        }
                    }
                },
                comeFor = comeFor,
                onDismiss = {
                    isTimePickerDialogOpen = false
                }
            )
        }

    }

}