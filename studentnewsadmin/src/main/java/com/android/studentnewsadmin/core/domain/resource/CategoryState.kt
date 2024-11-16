package com.android.studentnewsadmin.core.domain.resource

sealed class CategoryState<out T> {
    object Loading : CategoryState<Nothing>()
    data class Success<T>(val data: T) : CategoryState<T>()
    data class Progress(val progress: Float) : CategoryState<Nothing>()
    data class Failed(val error: Throwable) : CategoryState<Nothing>()
}