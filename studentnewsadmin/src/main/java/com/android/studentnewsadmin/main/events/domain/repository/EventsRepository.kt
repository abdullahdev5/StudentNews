package com.android.studentnewsadmin.main.events.domain.repository

import android.content.Context
import android.net.Uri
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    val eventsColRef: CollectionReference?
    val storageRef: StorageReference?

    fun onEventUpload(
        title: String,
        description: String,
        address: String,
        startingDate: Long,
        startingTimeHour: Int,
        startingTimeMinutes: Int,
        startingTimeStatus: String,
        endingDate: Long,
        endingTimeHour: Int,
        endingTimeMinutes: Int,
        endingTimeStatus: String,
        uriList: List<Uri>,
        isAvailable: Boolean,
        context: Context
    ): Flow<EventsState<String>>

    fun onEventEdit(
        eventId: String,
        title: String,
        description: String,
        address: String,
        startingDate: Long,
        startingTimeHour: Int,
        startingTimeMinutes: Int,
        startingTimeStatus: String,
        endingDate: Long,
        endingTimeHour: Int,
        endingTimeMinutes: Int,
        endingTimeStatus: String,
        isAvailable: Boolean,
    ): Flow<EventsState<String>>


    fun getEventsList(): Flow<EventsState<List<EventsModel>>>

    fun onEventDelete(eventId: String): Flow<EventsState<String>>


    fun onUploadEventWorkerStart(
        title: String,
        description: String,
        address: String,
        startingDate: Long,
        startingTimeHour: Int,
        startingTimeMinutes: Int,
        startingTimeStatus: String,
        endingDate: Long,
        endingTimeHour: Int,
        endingTimeMinutes: Int,
        endingTimeStatus: String,
        stringArray: Array<String>,
        iaAvailable: Boolean,
    )

}