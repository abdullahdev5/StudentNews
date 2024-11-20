package com.android.studentnewsadmin.main.events.ui.viewModels

import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.studentnewsadmin.core.domain.constants.Status
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.android.studentnewsadmin.main.events.domain.repository.EventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EventsViewModel(
    private val eventsRepository: EventsRepository
): ViewModel() {


    private val _eventsList = MutableStateFlow<List<EventsModel>>(emptyList())
    val eventsList = _eventsList.asStateFlow()

    var eventsListStatus = mutableStateOf("")
        private set
    var eventsListErrorMsg = mutableStateOf("")
        private set

    var eventsDeleteStatus = mutableStateOf("")
        private set
    var eventsDeleteErrorMsg = mutableStateOf("")
        private set


    init {
        getEventsList()
    }


   fun getEventsList() {
       viewModelScope.launch {
           eventsRepository
               .getEventsList()
               .collectLatest { result ->
                   when (result) {
                       is EventsState.Loading -> {
                           eventsListStatus.value = Status.Loading
                       }
                       is EventsState.Success -> {
                           _eventsList.value = result.data
                           eventsListStatus.value = Status.Success
                       }
                       is EventsState.Failed -> {
                           eventsListErrorMsg.value = result.message.localizedMessage ?: ""
                           eventsListStatus.value = Status.Failed
                       }
                       else -> {}
                   }
               }
       }
   }

    fun onEventDelete(eventId: String) {
        viewModelScope.launch {
            eventsRepository
                .onEventDelete(eventId)
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Loading -> {
                            eventsDeleteStatus.value = Status.Loading
                        }
                        is EventsState.Success -> {
                            eventsDeleteStatus.value = Status.Success
                        }
                        is EventsState.Failed -> {
                            eventsDeleteStatus.value = Status.Failed
                            eventsDeleteErrorMsg.value = result.message.localizedMessage ?: ""
                        }
                        else -> {}
                    }
                }
        }
    }


    fun onUploadEventWorkerStart(
        title: String,
        description: String,
        startingDate: Long,
        startingTimeHour: Int,
        startingTimeMinutes: Int,
        startingTimeStatus: String,
        endingDate: Long,
        endingTimeHour: Int,
        endingTimeMinutes: Int,
        endingTimeStatus: String,
        stringArray: Array<String>
    ) = eventsRepository.onUploadEventWorkerStart(
        title, description, startingDate, startingTimeHour, startingTimeMinutes, startingTimeStatus,
        endingDate, endingTimeHour, endingTimeMinutes, endingTimeStatus, stringArray
    )

}