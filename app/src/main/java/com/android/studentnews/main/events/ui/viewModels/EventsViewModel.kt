package com.android.studentnews.main.events.ui.viewModels

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.auth.domain.repository.AuthRepository
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.main.events.data.repository.EVENTS_LIST_PAGE_SIZE
import com.android.studentnews.main.events.domain.models.EventsBookingModel
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnews.news.domain.resource.NewsState
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EventsViewModel(
    private val eventsRepository: EventsRepository,
    private val authRepository: AuthRepository,
    private val notificationManager: NotificationManagerCompat
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _eventsList = MutableStateFlow<PagingData<EventsModel>>(PagingData.empty())
    val eventsList: StateFlow<PagingData<EventsModel>> = _eventsList

    private val _eventById = MutableStateFlow<EventsModel?>(null)
    val eventById = _eventById.asStateFlow()
    var eventsByIdStatus by mutableStateOf("")
        private set
    var eventsByIdErrorMsg by mutableStateOf("")
        private set


    var eventRegisteringStatus by mutableStateOf("")
        private set

    private val _savedEventById = MutableStateFlow<EventsModel?>(null)
    val savedEventById = _savedEventById.asStateFlow()

    var selectedCategoryIndex by mutableStateOf<Int?>(null)

    val lazyListState = LazyListState()


    init {
        getCurrentUser()
        startEventsWorker()
//        cancelEventsWorker()
    }


    fun getEventsList(
        availableStatus: Boolean?
    ) {
        viewModelScope.launch {
            eventsRepository
                .getEventsList(availableStatus, EVENTS_LIST_PAGE_SIZE)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _eventsList.value = pagingData
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

    fun onEventRegister(
        eventId: String,
        eventsBookingModel: EventsBookingModel,
    ) {
        viewModelScope.launch {
            eventsRepository
                .onEventRegister(eventId, eventsBookingModel)
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

    fun onEventSave(event: EventsModel) {
        viewModelScope.launch {
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

                        is EventsState.Success -> {

                        }

                        else -> {}
                    }
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

                        is EventsState.Success -> {

                        }

                        else -> {}
                    }
                }
        }
    }

    fun getSavedEventById(eventId: String) {
        viewModelScope.launch {
            eventsRepository
                .getSavedEventById(eventId)
                .collectLatest { result ->
                    when (result) {
                        is EventsState.Success -> {
                            _savedEventById.value = result.data
                        }

                        else -> {}
                    }
                }
        }
    }


    fun getCurrentUser() {
        viewModelScope.launch {
            authRepository
                .getCurrentUser()
                .collectLatest { result ->
                    when (result) {
                        is NewsState.Success -> {
                            _currentUser.value = result.data
                        }

                        else -> {}
                    }
                }
        }
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    fun startEventsWorker() = eventsRepository.startEventWorker()

    fun cancelEventsWorker() = eventsRepository.cancelEventsWorker()

}