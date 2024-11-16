package com.android.studentnews.auth.domain.resource

sealed class UserState<out T> {
    object Loading : UserState<Nothing>()
    data class Created<out T>(val data: T) : UserState<T>()
    data class Failed(val error: Throwable) : UserState<Nothing>()
}

class AuthenticationStatus {
    companion object {
        const val forCreation = "Creation"
        const val forAuthentication = "Authentication"
    }
}