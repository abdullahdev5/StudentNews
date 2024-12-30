package com.android.studentnews.main.referral_bonus.domain.model

import com.google.firebase.Timestamp

data class RedeemedOffersModel(
    val offerId: String = "",
    val collectedAt: Timestamp? = null,
) {
    constructor() : this(
        offerId = "",
        collectedAt = null,
    )
}