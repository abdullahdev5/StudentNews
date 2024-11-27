package com.android.studentnews.main.settings

import kotlinx.serialization.Serializable

sealed class SettingsDestination {

    @Serializable
    object SETTINGS_SCREEN: SettingsDestination()
}