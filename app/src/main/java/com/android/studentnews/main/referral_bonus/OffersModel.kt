package com.android.studentnews.main.referral_bonus

import com.google.firebase.Timestamp

data class OffersModel(
    val offerName: String? = "",
    val offerId: String? = "",
    val offerImageUrl: String? = "",
    val offerDescription: String? = "",
    var pointsWhenAbleToCollect: Double? = 0.0,
    val timestamp: Timestamp? = null,
) {
    constructor() : this(
        offerName = "",
        offerId = "",
        offerImageUrl = "",
        offerDescription = "",
        pointsWhenAbleToCollect = 0.0,
        timestamp = null
    )
}
