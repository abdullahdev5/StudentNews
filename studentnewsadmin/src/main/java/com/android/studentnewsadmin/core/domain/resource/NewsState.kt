package com.android.studentnewsadmin.core.domain.resource

sealed class NewsState<out T> {
    object Loading: NewsState<Nothing>()
    data class Success<T>(val data: T): NewsState<T>()
    data class Progress(val progress: Float): NewsState<Nothing>()
    data class Failed(val message: Throwable): NewsState<Nothing>()
}