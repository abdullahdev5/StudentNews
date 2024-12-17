package com.android.studentnews.auth.domain.models

data class UserModel(
    val email: String? = "",
    val password: String,
    val uid: String? = "",
    val registrationData: RegistrationData? = null,
    val profilePic: String? = "",
    val profilePicBgColor: Int? = 0,
    val bonusPoints: Long? = 0L,
) {
    constructor(): this (
        email = "",
        password = "",
        uid = "",
        registrationData = null,
        profilePic = "",
        profilePicBgColor = 0,
        bonusPoints = 0L
    )
}
