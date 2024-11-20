package com.android.studentnewsadmin.main.events.ui.viewModels

import androidx.lifecycle.ViewModel
import com.android.studentnewsadmin.main.events.domain.repository.EventsRepository

class EventsViewModel(
    private val eventsRepository: EventsRepository
): ViewModel() {

}