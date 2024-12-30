package com.android.studentnews.main.referral_bonus.domain.common

fun calculatePercentage(totalPoints: Int, offerPoints: Int): Float {
    return if (offerPoints > 0) {
        (totalPoints.toFloat() / offerPoints.toFloat() * 100).coerceIn(0f, 100f)
    } else {
        0f
    }
}