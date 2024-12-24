package com.android.studentnewsadmin.main.offers.domain.resource

sealed class OffersState<out T> {

    data object Loading: OffersState<Nothing>()
    data class Failed(val error: Throwable): OffersState<Nothing>()
    data class Success<T>(val data: T): OffersState<T>()
    data class Progress(val progress: Float): OffersState<Nothing>()

}