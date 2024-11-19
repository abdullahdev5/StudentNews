package com.android.studentnewsadmin.core.domain.resource

sealed class EventsState<out T> {
    object Loading : EventsState<Nothing>()
    data class Success<T>(val data: T) : EventsState<T>()
    data class Progress(val progress: Float) : EventsState<Nothing>()
    data class Failed(val message: Throwable) : EventsState<Nothing>()
}