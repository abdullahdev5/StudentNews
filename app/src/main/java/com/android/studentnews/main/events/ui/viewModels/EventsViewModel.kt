package com.android.studentnews.main.events.ui.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EventsViewModel(
    private val eventsRepository: EventsRepository,
): ViewModel() {

    private val _eventsList = MutableStateFlow<List<EventsModel?>>(emptyList())
    val eventsList = _eventsList.asStateFlow()

    var eventsListStatus by mutableStateOf("")
        private set

    var eventsListErrorMsg by mutableStateOf("")
        private set


    private val _eventById = MutableStateFlow<EventsModel?>(null)
    val eventById = _eventById.asStateFlow()

    var eventsByIdStatus by mutableStateOf("")
        private set

    var eventsByIdErrorMsg by mutableStateOf("")
        private set


    init {
        getEventsList()
    }


    fun getEventsList() {
        viewModelScope.launch {
            eventsListStatus = Status.Loading
            delay(500L)
            eventsRepository
                .getEventsList()
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Success -> {
                            _eventsList.value = result.data
                            eventsListStatus = Status.SUCCESS
                        }
                        is EventsState.Failed -> {
                            eventsListStatus = Status.FAILED
                            eventsListErrorMsg = result.error.localizedMessage ?: ""
                        }
                        else -> {}
                    }
                }
        }
    }

    fun getEventById(eventId: String) {
        viewModelScope.launch {
            eventsByIdStatus = Status.Loading
            eventsRepository
                .getEventById(eventId)
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Success -> {
                            _eventById.value = result.data
                            eventsByIdStatus = Status.SUCCESS
                        }
                        is EventsState.Failed -> {
                            eventsByIdStatus = Status.FAILED
                            eventsByIdErrorMsg = result.error.localizedMessage ?: ""
                        }
                        else -> {}
                    }
                }
        }
    }

}