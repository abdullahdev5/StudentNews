package com.android.studentnews.auth.domain

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.android.studentnews.auth.domain.models.RegistrationData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RegistrationDataNavType {

    val registrationDataType = object : NavType<RegistrationData>(
        isNullableAllowed = false
    ) {
        override fun get(
            bundle: Bundle,
            key: String
        ): RegistrationData? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): RegistrationData {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: RegistrationData): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(
            bundle: Bundle,
            key: String,
            value: RegistrationData
        ) {
            bundle.putString(key, Json.encodeToString(value))
        }

    }

}