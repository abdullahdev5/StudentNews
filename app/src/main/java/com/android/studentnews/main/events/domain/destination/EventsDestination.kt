package com.android.studentnews.main.events.domain.destination

import kotlinx.serialization.Serializable

sealed class EventsDestination {
    @Serializable
    object EVENTS_SCREEN : EventsDestination()

    @Serializable
    data class EVENTS_DETAIL_SCREEN(val eventId: String) : EventsDestination()

    @Serializable
    object BOOKED_EVENTS_SCREEN: EventsDestination()

    @Serializable
    object SAVED_EVENTS_SCREEN: EventsDestination()
}