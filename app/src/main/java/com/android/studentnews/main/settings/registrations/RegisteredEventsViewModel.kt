package com.android.studentnews.main.settings.registrations

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.io.Closeable

class RegisteredEventsViewModel(
    private val eventsRepository: EventsRepository,
): ViewModel() {

    private val _registeredEventsList = MutableStateFlow<List<EventsModel?>>(emptyList())
    val registeredEVentsList = _registeredEventsList.asStateFlow()

    var registeredEventsListStatus by mutableStateOf("")
        private set


    init {
        getRegisteredEventsList()
    }



    fun getRegisteredEventsList() {
        viewModelScope.launch {
            eventsRepository.getRegisteredEventsList()
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Loading -> {
                            registeredEventsListStatus = Status.Loading
                        }
                        is EventsState.Failed -> {
                            registeredEventsListStatus = Status.FAILED
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.error.localizedMessage ?: "",
                                        duration = SnackbarDuration.Long
                                    )
                                )
                        }
                        is EventsState.Success -> {
                            registeredEventsListStatus = Status.SUCCESS
                            _registeredEventsList.value = result.data
                        }
                        else -> {}
                    }
                }
        }
    }

}