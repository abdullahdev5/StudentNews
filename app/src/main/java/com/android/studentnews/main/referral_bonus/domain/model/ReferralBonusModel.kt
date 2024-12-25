package com.android.studentnews.main.referral_bonus.domain.model

// Implementing in a UserModel data class

data class ReferralBonusModel(
    var totalPoints: Double? = 0.0,
    var usedPoints: Double? = 0.0,
    @field:JvmField
    var isUserCollectThePoints: Boolean? = false,
    var unCollectedPoints: Double? = 0.0,
) {
    constructor(): this(
        totalPoints = 0.0,
        usedPoints = 0.0,
        isUserCollectThePoints = false,
        unCollectedPoints = 0.0,
    )
}