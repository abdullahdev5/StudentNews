package com.android.studentnews.main.events.ui.viewModels

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.auth.domain.models.RegistrationData
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

class EventsDetailViewModel(
    private val eventsRepository: EventsRepository,
    private val notificationManager: NotificationManagerCompat
) : ViewModel() {

    private val _eventById = MutableStateFlow<EventsModel?>(null)
    val eventById = _eventById.asStateFlow()

    var isEventSaved by mutableStateOf<Boolean?>(null)

    var isEventRegistered by mutableStateOf<Boolean?>(null)
        private set

    var eventByIdStatus by mutableStateOf("")
        private set
    var eventByIdErrorMsg by mutableStateOf("")
        private set

    var eventRegisteringStatus by mutableStateOf("")
        private set


    fun getEventById(eventId: String) {
        viewModelScope.launch() {
            eventsRepository
                .getEventById(eventId)
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Success -> {
                            _eventById.value = result.data
                            eventByIdStatus = Status.SUCCESS
                        }

                        is EventsState.Failed -> {
                            eventByIdStatus = Status.FAILED
                            eventByIdErrorMsg = result.error.localizedMessage ?: ""
                        }

                        else -> {}
                    }
                }
        }
    }

    fun onEventSave(event: EventsModel) {
        viewModelScope.launch {
            try {
                eventsRepository
                    .onEventSave(event)
                    .collectLatest { result ->
                        when (result) {
                            is EventsState.Failed -> {
                                SnackBarController.sendEvent(
                                    SnackBarEvents(
                                        message = result.error.localizedMessage ?: "",
                                        duration = SnackbarDuration.Long,
                                    )
                                )
                            }

                            else -> {}
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                println("In Snapshot Block, Error:- $e")
                SnackBarController.sendEvent(
                    SnackBarEvents(
                        message = e.localizedMessage ?: "",
                        duration = SnackbarDuration.Long,
                    )
                )
            }
        }
    }

    fun onEventRemoveFromSave(event: EventsModel) {
        viewModelScope.launch {
            eventsRepository
                .onEventRemoveFromSave(event)
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Failed -> {
                            SnackBarController.sendEvent(
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

    fun getIsEventSaved(eventId: String) {
        viewModelScope.launch {
            eventsRepository
                .getIsEventSaved(eventId)
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Success -> {
                            isEventSaved = result.data
                        }

                        else -> {}
                    }
                }
        }
    }


    fun onEventRegister(
        eventId: String,
        registrationData: RegistrationData,
    ) {
        viewModelScope.launch {
            eventsRepository
                .onEventRegister(eventId, registrationData)
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Success -> {
                            eventRegisteringStatus = Status.SUCCESS
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.data
                                    )
                                )
                        }

                        is EventsState.Failed -> {
                            eventRegisteringStatus = Status.FAILED
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.error.localizedMessage ?: ""
                                    )
                                )
                        }

                        is EventsState.Loading -> {
                            eventRegisteringStatus = Status.Loading
                        }

                        else -> {}
                    }
                }
        }
    }

    fun getIsEventRegistered(eventId: String) {
        viewModelScope.launch {
            eventsRepository
                .getIsEventRegistered(eventId)
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Success -> {
                            isEventRegistered = result.data
                        }
                        else -> {}
                    }
                }
        }
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    override fun onCleared() {
        isEventSaved = null
        isEventRegistered = null
    }

}