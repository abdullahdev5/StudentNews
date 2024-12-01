package com.android.studentnewsadmin.main.events.domain.models

import androidx.compose.runtime.Immutable
import com.android.studentnews.main.events.domain.models.EventsBookingModel
import com.android.studentnews.news.domain.model.UrlList
import com.google.firebase.Timestamp
import kotlinx.serialization.SerialName

@Immutable
data class EventsModel(
    val title: String? = "", // title
    val description: String? = "", // description,
    val eventId: String? = "", // event ID
    val address: String? = "", // address
    val startingDate: Long? = 0L, // starting date
    val startingTimeHour: Int? = 0, // starting time hour
    val startingTimeMinutes: Int? = 0, // starting time minutes
    val startingTimeStatus: String? = "", // starting time status, Like am OR pm
    val endingDate: Long? = 0L, // ending Date
    val endingTimeHour: Int? = 0, // ending time hour
    val endingTimeMinutes: Int? = 0, // ending time minutes
    val endingTimeStatus: String? = "", // ending time status
    val timestamp: Timestamp? = null, // timestamp
    val urlList: List<UrlList>? = emptyList(), // url List
    val bookings: List<EventsBookingModel>? = emptyList(), // Bookings
    @field:JvmField
    val isAvailable: Boolean? = true // Available
) {
    constructor() : this(
        title = "",
        description = "",
        eventId = "",
        address = "",
        startingDate = 0L,
        startingTimeHour = 0,
        startingTimeMinutes = 0,
        startingTimeStatus = "",
        endingDate = 0L,
        endingTimeHour = 0,
        endingTimeMinutes = 0,
        endingTimeStatus = "",
        timestamp = null,
        urlList = emptyList(),
        bookings = emptyList(),
        isAvailable = true
    )
}
