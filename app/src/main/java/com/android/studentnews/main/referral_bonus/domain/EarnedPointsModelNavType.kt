package com.android.studentnews.main.referral_bonus.domain

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.android.studentnews.main.referral_bonus.domain.model.EarnedPointsModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object EarnedPointsModelNavType {

    val earnedPointsModelType = object : NavType<EarnedPointsModel>(
        isNullableAllowed = false
    ) {
        override fun get(
            bundle: Bundle,
            key: String,
        ): EarnedPointsModel? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): EarnedPointsModel {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: EarnedPointsModel): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(
            bundle: Bundle,
            key: String,
            value: EarnedPointsModel,
        ) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override val isNullableAllowed: Boolean
            get() = super.isNullableAllowed

    }

}