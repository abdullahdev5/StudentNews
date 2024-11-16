package com.android.studentnews.news.domain.destination

import kotlinx.serialization.Serializable

sealed class MainDestination {

    @Serializable
    object ACCOUNT_SCREEN: MainDestination()

    @Serializable
    object SEARCH_SCREEN: MainDestination()

}