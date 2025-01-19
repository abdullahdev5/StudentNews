package com.android.studentnews.main.settings.saved.ui.viewModels

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.main.events.data.repository.SAVED_EVENTS_LIST_PAGE_SIZE
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedEventsViewModel(
    private val eventsRepository: EventsRepository
): ViewModel() {


    private val _savedEventsList = MutableStateFlow<PagingData<EventsModel>>(PagingData.empty())
    val savedEventsList: StateFlow<PagingData<EventsModel>> = _savedEventsList


    init {
        getSavedEventsList()
    }


    fun getSavedEventsList() {
        viewModelScope.launch {
            eventsRepository
                .getSavedEventsList(limit = SAVED_EVENTS_LIST_PAGE_SIZE)
                .cachedIn(this)
                .collectLatest { pagingData ->
                    _savedEventsList.value = pagingData
                }
        }
    }

    fun onEventRemoveFromSaveList(eventId: String) {
        viewModelScope.launch {
            delay(1000)
            _savedEventsList.update {
                it.filter { data ->
                    eventId != data.eventId
                }
            }
            eventsRepository
                .onEventRemoveFromSave(eventId)
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Success -> {
                            SnackBarController
                                .sendEvent(
                                    SnackBarEvents(
                                        message = result.data,
                                        duration = SnackbarDuration.Long,
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


}