package com.android.studentnews.news.domain.destination

import kotlinx.serialization.Serializable

sealed class MainDestination {

    @Serializable
    data object ACCOUNT_SCREEN: MainDestination()

    @Serializable
    data object SEARCH_SCREEN: MainDestination()

}