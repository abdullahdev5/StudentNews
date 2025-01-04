package com.android.studentnews.main.events.ui.viewModels

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.studentnews.main.events.data.repository.EVENTS_LIST_PAGE_SIZE
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EventsViewModel(
    private val eventsRepository: EventsRepository,
) : ViewModel() {

    private val _eventsList = MutableStateFlow<PagingData<EventsModel>>(PagingData.empty())
    val eventsList: StateFlow<PagingData<EventsModel>> = _eventsList

    var selectedCategoryIndex by mutableStateOf<Int?>(null)

    val lazyListState = LazyListState()


    init {
        startEventsWorker()
//        cancelEventsWorker()
    }


    fun getEventsList(
        availableStatus: Boolean?
    ) {
        viewModelScope.launch {
            eventsRepository
                .getEventsList(availableStatus, EVENTS_LIST_PAGE_SIZE)
                .cachedIn(this)
                .collectLatest { pagingData ->
                    _eventsList.value = pagingData
                }
        }
    }

    fun startEventsWorker() = eventsRepository.startEventWorker()

    fun cancelEventsWorker() = eventsRepository.cancelEventsWorker()

}