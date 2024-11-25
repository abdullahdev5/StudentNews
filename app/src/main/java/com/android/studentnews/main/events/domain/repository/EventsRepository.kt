package com.android.studentnews.main.events.domain.repository

import com.android.studentnews.main.events.domain.models.EventsBookingModel
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    val userDocRef: DocumentReference?

    val eventsColRef: CollectionReference?

    val savedEventsColRef: CollectionReference?


    fun getEventsList(): Flow<EventsState<List<EventsModel?>>>

    fun getEventById(eventId: String): Flow<EventsState<EventsModel?>>

    fun onEventBook(
        eventId: String,
        eventsBookingModel: EventsBookingModel
    ): Flow<EventsState<String>>

    fun onEventSave(event: EventsModel): Flow<EventsState<String>>

    fun onEventRemoveFromSave(event: EventsModel): Flow<EventsState<String>>

    fun getSavedEventById(eventId: String): Flow<EventsState<EventsModel?>>

    fun getRegisteredEventsList(): Flow<EventsState<List<EventsModel?>>> // Booked Events of Current User

    fun getSavedEventsList(): Flow<EventsState<List<EventsModel?>>>

}