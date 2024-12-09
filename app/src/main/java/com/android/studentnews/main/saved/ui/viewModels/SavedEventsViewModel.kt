package com.android.studentnews.main.settings.saved.ui.viewModels

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.studentnews.core.data.snackbar_controller.SnackBarActions
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.events.data.repository.SAVED_EVENTS_LIST_PAGE_SIZE
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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