package com.android.studentnews.auth.domain.destination

import com.android.studentnews.auth.domain.models.RegistrationData
import kotlinx.serialization.Serializable

sealed class AuthDestination {
    @Serializable
    object REGISTRATION_FORM_SCREEN: AuthDestination()

    @Serializable
    data class AUTHENTICATION_SCREEN(
        val comeFor: String,
        val registrationData: RegistrationData
    ): AuthDestination()
}