package com.android.studentnewsadmin.main.offers.domain.model

import com.google.firebase.Timestamp

data class OffersModel(
    val offerName: String? = "",
    val offerImageUrl: String? = "",
    val offerId: String? = "",
    val pointsWhenAbleToCollect: Double? = 0.0,
    val offerDescription: String? = "",
    val timestamp: Timestamp? = null,
) {
    constructor(): this(
        offerName = "",
        offerImageUrl = "",
        offerId = "",
        pointsWhenAbleToCollect = 0.0,
        offerDescription = "",
        timestamp = null,
    )
}
