package com.android.studentnews.main.referral_bonus.domain.model

data class EarnedPointsModel(
    var earnedPoints: Double? = 0.0,
    val earnedPointId: String = "",
) {
    constructor() : this(
        earnedPoints = 0.0,
        earnedPointId = "",
    )
}
