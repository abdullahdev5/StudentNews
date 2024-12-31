package com.android.studentnews.main.settings.saved.domain.destination

import kotlinx.serialization.Serializable

sealed class SavedDestination {

    @Serializable
    data object SAVED_SCREEN: SavedDestination()

    @Serializable
    data object SAVED_NEWS_SCREEN: SavedDestination()

    @Serializable
    data object SAVED_EVENTS_SCREEN: SavedDestination()

}