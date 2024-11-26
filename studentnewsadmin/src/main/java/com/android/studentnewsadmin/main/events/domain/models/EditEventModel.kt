package com.android.studentnewsadmin.main.events.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class EditEventModel(
    val title: String,
    val description: String,
    val address: String, // Address
    val startingDate: Long, // starting date
    val startingTimeHour: Int, // starting time hour
    val startingTimeMinutes: Int, // starting time minutes
    val startingTimeStatus: String, // starting time status, Like am OR pm
    val endingDate: Long, // ending Date
    val endingTimeHour: Int, // ending time hour
    val endingTimeMinutes: Int, // ending time minutes
    val endingTimeStatus: String, // ending time status
    val isAvailable: Boolean = true
) {
    constructor() : this(
        title = "",
        description = "",
        address = "",
        startingDate = 0L,
        startingTimeHour = 0,
        startingTimeMinutes = 0,
        startingTimeStatus = "",
        endingDate = 0L,
        endingTimeHour = 0,
        endingTimeMinutes = 0,
        endingTimeStatus = "",
        isAvailable = true
    )
}