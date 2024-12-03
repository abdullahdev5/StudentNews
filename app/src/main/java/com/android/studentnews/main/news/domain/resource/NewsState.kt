package com.android.studentnews.news.domain.resource

sealed class NewsState<out T> {

    object Loading: NewsState<Nothing>()
    data class Failed(val error: Throwable): NewsState<Nothing>()
    data class Success<out T>(val data: T): NewsState<T>()
    data class IsAfterPaginateDocumentsExist(val isExists: Boolean): NewsState<Nothing>()
}