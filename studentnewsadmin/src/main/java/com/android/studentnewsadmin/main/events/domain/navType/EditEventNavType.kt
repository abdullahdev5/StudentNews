package com.android.studentnewsadmin.main.events.domain.navType

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import com.android.studentnewsadmin.main.events.domain.models.EditEventModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

object EditEventNavType {

    val editEventNavType = object: NavType<EditEventModel>(isNullableAllowed = false) {
        override fun get(
            bundle: Bundle,
            key: String,
        ): EditEventModel? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): EditEventModel {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: EditEventModel): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(
            bundle: Bundle,
            key: String,
            value: EditEventModel,
        ) {
            bundle.putString(key, Json.encodeToString(value))
        }

    }


}