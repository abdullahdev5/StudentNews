package com.android.studentnewsadmin.main.events.domain.repository

import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    val eventsColRef: CollectionReference?
    val storageRef: StorageReference?

    fun onEventAdd(): Flow<EventsState<String>>

}