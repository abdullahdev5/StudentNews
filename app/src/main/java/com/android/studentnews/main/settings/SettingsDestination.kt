package com.android.studentnews.main.settings

import kotlinx.serialization.Serializable

sealed class SettingsDestination {

    @Serializable
    object SETTINGS_SCREEN: SettingsDestination()

    @Serializable
    object SAVED_SCREEN: SettingsDestination() // Its a Sub Graph

    @Serializable
    object REGISTRATIONS_SCREEN: SettingsDestination() // Its Not a Sub Graph

    @Serializable
    object LIKED_SCREEN: SettingsDestination() // Its Not a Sub Graph

}