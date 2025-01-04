package com.android.studentnews.main.events.domain.destination

import kotlinx.serialization.Serializable

sealed class EventsDestination {
    @Serializable
    data object EVENTS_SCREEN : EventsDestination()

    @Serializable
    data class EVENTS_DETAIL_SCREEN(
        val eventId: String,
        val isComeForRegistration: Boolean = false,
        val notificationId: Int? = null, // Id of Notification when user click the Register Action
    ) : EventsDestination()

    @Serializable
    data object REGISTERED_EVENTS_SCREEN: EventsDestination()

    class BottomSheetDestinations {
        companion object {
            const val REGISTRATION_EVENTS_BOTTOM_SHEET_DESTINATION =
                "registration_events_bottom_sheet"
        }
    }
}