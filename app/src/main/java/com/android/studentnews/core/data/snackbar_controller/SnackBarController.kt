package com.android.studentnews.core.data.snackbar_controller

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.internal.ChannelFlow
import kotlinx.coroutines.flow.receiveAsFlow


data class SnackBarEvents(
    val message: String,
    val action: SnackBarActions? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
)


data class SnackBarActions(
    val label: String,
    val action: suspend () -> Unit,
)


object SnackBarController {

    private val _events = Channel<SnackBarEvents>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackBarEvents) {
        _events.send(event)
    }

}