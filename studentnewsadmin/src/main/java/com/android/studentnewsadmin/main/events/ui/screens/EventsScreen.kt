package com.android.studentnewsadmin.main.events.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.android.studentnewsadmin.core.domain.common.formatDateToString
import com.android.studentnewsadmin.core.domain.common.formatTimeToString
import com.android.studentnewsadmin.core.domain.constants.FontSize
import com.android.studentnewsadmin.core.domain.constants.Status
import com.android.studentnewsadmin.core.ui.common.LoadingDialog
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.android.studentnewsadmin.main.events.ui.viewModels.EventsViewModel
import kotlin.math.roundToInt

@Composable
fun EventsScreen(
    navHostController: NavHostController,
    eventsViewModel: EventsViewModel,
) {

    val context = LocalContext.current


    val eventsList by eventsViewModel.eventsList.collectAsStateWithLifecycle()

    var isErrorDialogOpen by remember { mutableStateOf(false) }


    LaunchedEffect(eventsViewModel.eventsDeleteStatus.value) {
        if (eventsViewModel.eventsDeleteStatus.value == Status.Failed) {
            isErrorDialogOpen = true

        } else if (eventsViewModel.eventsDeleteStatus.value == Status.Success) {
            Toast.makeText(context, "This Event Deleted Successfully", Toast.LENGTH_SHORT).show()
        }
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {

            item {
                if (eventsList.isEmpty()) {
                    Text(
                        text = "No Events Found",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    )
                } else if (eventsViewModel.eventsListErrorMsg.value.isNotEmpty()) {
                    Text(
                        text = eventsViewModel.eventsListErrorMsg.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    )
                }
            }

            items(eventsList.size) { index ->
                val item = eventsList[index]

                EventsItem(
                    item = item,
                    onEventDelete = { eventId ->
                        eventsViewModel.onEventDelete(eventId)
                    }
                )
            }
        }

        if (eventsViewModel.eventsListStatus.value == Status.Loading) {
            LoadingDialog()
        }

        if (eventsViewModel.eventsDeleteErrorMsg.value.isNotEmpty()) {
            AlertDialog(
                onDismissRequest = {
                    isErrorDialogOpen = false
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error Icon"
                    )
                },
                title = {
                    Text(text = "Failed to Delete This Event")
                },
                text = {
                    Text(text = eventsViewModel.eventsDeleteErrorMsg.value)
                },
                confirmButton = {
                    TextButton(onClick = {
                        isErrorDialogOpen = false
                    }) {
                        Text(text = "OK")
                    }
                }
            )
        }

        if (eventsViewModel.eventsDeleteStatus.value == Status.Loading) {
            LoadingDialog()
        }

    }
}

@Composable
fun EventsItem(
    item: EventsModel,
    onEventDelete: (String) -> Unit
) {

    var offsetx by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var maxWidth = 150.dp
    var draggedItemId by remember { mutableStateOf("") }

    Row {

        AnimatedVisibility(
            offsetx != 0f,
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .background(color = Color.Red)
                    .clickable {
                        isDragging = false
                        offsetx = 0f
                        onEventDelete.invoke(draggedItemId)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Icon for Delete News",
                    tint = Color.White
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray.copy(
                    0.5f
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
                .offset {
                    IntOffset(offsetx.roundToInt(), 0)
                }
                .pointerInput(true) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            if (offsetx == maxWidth.toPx() - 100.dp.toPx()) {
                                draggedItemId = item.eventId ?: ""
                                isDragging = false
                            } else {
                                offsetx = 0f
                                isDragging = false
                            }
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consumeAllChanges()
                            val newOffsetX = offsetx + dragAmount
                            offsetx =
                                newOffsetX.coerceIn(
                                    0f,
                                    maxWidth.toPx() - 100.dp.toPx()
                                )
                        },
                    )
                },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                AsyncImage(
                    model = item.urlList?.first()?.url ?: "",
                    contentDescription = "Event First Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                )

                Column(
                    modifier = Modifier
                        .padding(all = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    // Title
                    Text(
                        text = item.title ?: "",
                        style = TextStyle(
                            fontSize = FontSize.MEDIUM.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Description
                    Text(
                        text = item.description ?: "",
                        style = TextStyle(
                            fontSize = FontSize.SMALL.sp,
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Date:- ${
                            formatDateToString(item.startingDate ?: 0L)
                        } - ${
                            formatDateToString(item.endingDate ?: 0L)
                        }"
                    )

                    Text(
                        text = "Time:- ${
                            formatTimeToString(
                                item.startingTimeHour ?: 0,
                                item.startingTimeMinutes ?: 0
                            ).dropLast(2)
                        } ${item.startingTimeStatus} - ${
                            formatTimeToString(
                                item.endingTimeHour ?: 0,
                                item.endingTimeMinutes ?: 0
                            ).dropLast(2)
                        } ${item.endingTimeStatus}"
                    )
                }
            }
        }
    }
}