package com.android.studentnews.main.account.domain.resource

sealed class AccountState<out T> {
    object Loading : AccountState<Nothing>()
    data class Failure(val error: Throwable) : AccountState<Nothing>()
    data class Success<out T>(val data: T) : AccountState<T>()
    data class Progress(val progress: Float) : AccountState<Nothing>()
}