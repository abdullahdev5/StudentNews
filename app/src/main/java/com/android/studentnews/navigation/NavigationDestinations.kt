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
}