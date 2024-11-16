package com.android.studentnews.auth.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationData(
    val name: String,
    val degree: String,
    val degreeTitle: String,
    val semester: String,
    val countryCode: String,
    val number: String,
    val phoneNumber: String,
    val city: String,
    val address: String,
) {
    constructor() : this("", "", "", "", "", "", "", "", "")
}