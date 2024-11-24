package com.android.studentnews.navigation

import kotlinx.serialization.Serializable

sealed class SubGraph {
    @Serializable
    object AUTH: SubGraph()

    @Serializable
    object Main: SubGraph()

    @Serializable
    object NEWS: SubGraph()

    @Serializable
    object EVENTS: SubGraph()

    @Serializable
    object SETTINGS: SubGraph()

    @Serializable
    object SETTINGS_SAVED: SubGraph()
}