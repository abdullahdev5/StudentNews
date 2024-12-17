package com.android.studentnews.navigation

import kotlinx.serialization.Serializable

sealed class SubGraph {
    @Serializable
    data object AUTH: SubGraph()

    @Serializable
    data object Main: SubGraph()

    @Serializable
    data object NEWS: SubGraph()

    @Serializable
    data object EVENTS: SubGraph()

    @Serializable
    data object SETTINGS: SubGraph()

    @Serializable
    data object SAVED: SubGraph()

}