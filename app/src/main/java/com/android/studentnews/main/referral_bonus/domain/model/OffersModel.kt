package com.android.studentnews.main.referral_bonus.domain.model

import com.android.studentnews.main.referral_bonus.domain.constants.OfferTypes
import com.google.firebase.Timestamp

data class OffersModel(
    val offerName: String = "", // Offer Name
    val offerId: String = "", // Offer Id
    val offerImageUrl: String = "", // Offer Image Url
    val offerDescription: String = "", // Offer Description
    val pointsRequired: Double = 0.0, // Points Required
    val offerType: String = OfferTypes.ACTIVE, // Offer Type Like Active, In Active, or Expired
    val discountAmount: Double? = null, // Discount Amount
    val offerTermsAndCondition: String? = "", // Offer Terms & Condition
    val createdAt: Timestamp = Timestamp.now(), // Created At
    val updatedAt: Timestamp? = null, // Updated At
    val offerExpiryDate: Timestamp? = null, // Offer ExpiryDate If 'offerType' is Expired
) {
    constructor(): this(
        offerName = "",
        offerId = "",
        offerImageUrl = "",
        offerDescription = "",
        pointsRequired = 0.0,
        offerType = OfferTypes.ACTIVE,
        discountAmount = null,
        offerTermsAndCondition = "",
        createdAt = Timestamp.now(),
        updatedAt = null,
        offerExpiryDate = null,
    )
}
