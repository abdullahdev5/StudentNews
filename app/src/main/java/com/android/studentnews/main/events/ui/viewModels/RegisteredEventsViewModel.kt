package com.android.studentnews.main.settings.registered_events

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.events.data.repository.REGISTERED_EVENTS_LIST_PAGE_SIZE
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisteredEventsViewModel(
    private val eventsRepository: EventsRepository,
): ViewModel() {

    private val _registeredEventsList = MutableStateFlow<PagingData<EventsModel>>(PagingData.empty())
    val registeredEventsList: StateFlow<PagingData<EventsModel>> = _registeredEventsList

    init {
        getRegisteredEventsList()
    }

    fun getRegisteredEventsList() {
        viewModelScope.launch {
            eventsRepository.getRegisteredEventsList(limit = REGISTERED_EVENTS_LIST_PAGE_SIZE)
                .cachedIn(this)
                .collectLatest { pagingData ->
                    _registeredEventsList.value = pagingData
                }
        }
    }

}