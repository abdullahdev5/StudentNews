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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EventsViewModel(
    private val eventsRepository: EventsRepository,
): ViewModel() {

    private val _eventsList = MutableStateFlow<List<EventsModel>>(emptyList())
    val eventsList = _eventsList.asStateFlow()

    var eventsListStatus by mutableStateOf("")
        private set

    var eventsListErrorMsg by mutableStateOf("")
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
                            eventsListStatus = Status.Loading
                        }
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

}