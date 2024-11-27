package com.android.studentnews.main.settings.saved.ui.viewModels

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.core.data.snackbar_controller.SnackBarActions
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SavedEventsViewModel(
    private val eventsRepository: EventsRepository
): ViewModel() {


    private val _savedEventsList = MutableStateFlow<List<EventsModel?>>(emptyList())
    val savedEventsList = _savedEventsList.asStateFlow()

    var savedEventsListStatus by mutableStateOf("")
        private set


    init {
        getSavedEventsList()
    }


    fun getSavedEventsList() {
        viewModelScope.launch {
            eventsRepository
                .getSavedEventsList()
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Loading -> {
                            savedEventsListStatus = Status.Loading
                        }
                        is EventsState.Success -> {
                            savedEventsListStatus = Status.SUCCESS
                            _savedEventsList.value = result.data
                        }
                        is EventsState.Failed -> {
                            savedEventsListStatus = Status.FAILED
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.error.localizedMessage ?: "",
                                        duration = SnackbarDuration.Long
                                    )
                                )
                        }
                        else -> {}
                    }
                }
        }
    }

    fun onEventRemoveFromSaveList(event: EventsModel) {
        viewModelScope.launch {
            eventsRepository
                .onEventRemoveFromSave(event)
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Success -> {
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.data,
                                        duration = SnackbarDuration.Long,
                                        action = SnackBarActions(
                                            label = "Undo",
                                            action = {
                                                onEventRemoveFromSaveListUndo(event)
                                            }
                                        )
                                    )
                                )
                        }
                        is EventsState.Failed -> {
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.error.localizedMessage ?: "",
                                        duration = SnackbarDuration.Long,
                                    )
                                )
                        }
                        else -> {}
                    }
                }
        }
    }

    fun onEventRemoveFromSaveListUndo(event: EventsModel) {
        viewModelScope.launch {
            eventsRepository
                .onEventSave(event)
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Failed -> {
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.error.localizedMessage ?: "",
                                        duration = SnackbarDuration.Long,
                                    )
                                )
                        }
                        else -> {}
                    }
                }
        }
    }


}