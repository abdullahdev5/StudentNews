package com.android.studentnews.main.events.domain.models

import com.google.firebase.Timestamp

data class EventsBookingModel(
    val userId: String? = "",
    val userName: String? = "",
    val userDegree: String? = "",
    val userPhoneNumber: String? = "",
    val userCity: String? = "",
    val userAddress: String? = "",
    val userProfilePic: String? = "",
    val userProfilePicBgColor: Int? = 0,
    val timestamp: Timestamp? = null
) {
    constructor() : this(
        userId = "",
        userName = "",
        userDegree = "",
        userPhoneNumber = "",
        userCity = "",
        userAddress = "",
        userProfilePic = "",
        userProfilePicBgColor = 0,
        timestamp = null
    )
}