package com.android.studentnews.main.referral_bonus.domain.destination

import androidx.annotation.RawRes
import com.android.studentnews.main.referral_bonus.domain.model.EarnedPointsModel
import kotlinx.serialization.Serializable

sealed class ReferralBonusDestinations {

    @Serializable
    data object REFERRAL_BONUS_SCREEN: ReferralBonusDestinations()

    @Serializable
    data class COLLECTING_POINTS_DIALOG(
        val titleText: String,
        val descriptionText: String,
        val earnedPointsModel: EarnedPointsModel,
    ): ReferralBonusDestinations()

    @Serializable
    data class CONGRATULATION_DIALOG(
        @RawRes val resId: Int,
        val lottieHeight: Int,
        val titleText: String,
        val descriptionText: String,
    )

}