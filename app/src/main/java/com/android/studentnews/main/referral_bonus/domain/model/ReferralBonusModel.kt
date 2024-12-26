package com.android.studentnews.main.referral_bonus.domain.model

// Implementing in a UserModel data class

data class ReferralBonusModel(
    var totalPoints: Double? = 0.0,
    var usedPoints: Double? = 0.0,
    val earnedPointsList: List<EarnedPointsModel>? = null
) {
    constructor(): this(
        totalPoints = 0.0,
        usedPoints = 0.0,
        earnedPointsList = null,
    )
}