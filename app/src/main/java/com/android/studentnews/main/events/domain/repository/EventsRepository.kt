package com.android.studentnews.main.events.domain.repository

import com.android.studentnews.main.events.domain.models.EventsBookingModel
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    val eventsColRef: CollectionReference?

    fun getEventsList(): Flow<EventsState<List<EventsModel?>>>

    fun getEventById(eventId: String): Flow<EventsState<EventsModel?>>

    fun onEventBook(
        eventId: String,
        eventsBookingModel: EventsBookingModel
    ): Flow<EventsState<String>>

}