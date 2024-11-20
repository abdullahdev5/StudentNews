package com.android.studentnews.main.events.domain.repository

import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    val eventsColRef: CollectionReference?

    fun getEventsList(): Flow<EventsState<List<EventsModel>>>

}