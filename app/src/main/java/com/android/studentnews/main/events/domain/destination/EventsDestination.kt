package com.android.studentnews.main.events.domain.destination

import kotlinx.serialization.Serializable

sealed class EventsDestination {
    @Serializable
    data object EVENTS_SCREEN : EventsDestination()

    @Serializable
    data class EVENTS_DETAIL_SCREEN(val eventId: String) : EventsDestination()

    @Serializable
    data object REGISTERED_EVENTS_SCREEN: EventsDestination()
}