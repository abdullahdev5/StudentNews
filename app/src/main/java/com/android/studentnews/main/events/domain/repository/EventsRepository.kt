package com.android.studentnews.main.events.domain.repository

import androidx.paging.PagingData
import com.android.studentnews.auth.domain.models.RegistrationData
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    val userDocRef: DocumentReference?
    val eventsColRef: CollectionReference?
    val savedEventsColRef: CollectionReference?
    val registeredEventsCol: CollectionReference?


    fun getEventsList(
        availableStatus: Boolean?,
        limit: Int
    ): Flow<PagingData<EventsModel>>

    fun getEventById(eventId: String): Flow<EventsState<EventsModel?>>

    fun onEventRegister(
        eventId: String,
        registrationData: RegistrationData
    ): Flow<EventsState<String>>

    fun getIsEventRegistered(eventId: String): Flow<EventsState<Boolean>>

    suspend fun onEventSave(eventId: String): Flow<EventsState<String>>

    suspend fun onEventRemoveFromSave(eventId: String): Flow<EventsState<String>>

    fun getIsEventSaved(eventId: String): Flow<EventsState<Boolean>>

    suspend fun getRegisteredEventsList(limit: Int): Flow<PagingData<EventsModel>> // Booked Events of Current User

    fun getSavedEventsList(limit: Int): Flow<PagingData<EventsModel>>

    fun getEventsListByAvailableStatus(availableStatus: Boolean): Flow<EventsState<List<EventsModel>>>


    suspend fun getEventsUpdates(): EventsModel?

    suspend fun getCurrentUserName(): String

    fun startEventWorker()

    fun cancelEventsWorker()

}