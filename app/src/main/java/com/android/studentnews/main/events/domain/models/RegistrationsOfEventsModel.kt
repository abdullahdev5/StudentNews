package com.android.studentnews.main.events.domain.models

import com.google.firebase.Timestamp

data class RegistrationsOfEventsModel(
    val eventId: String = "",
    val userId: String = "",
    val registrationCode: String = "",
    val registeredAt: Timestamp,
) {
    constructor() : this(
        eventId = "",
        userId = "",
        registrationCode = "",
        registeredAt = Timestamp.now()
    )
}
