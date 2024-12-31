package com.android.studentnews.main.referral_bonus.domain.repository

import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.main.referral_bonus.domain.model.EarnedPointsModel
import com.android.studentnews.main.referral_bonus.domain.model.OffersModel
import com.android.studentnews.main.referral_bonus.domain.resource.ReferralBonusState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface ReferralBonusRepository {

    val userDocRef: DocumentReference?
    val offersColRef: CollectionReference?
    val collectedOffersColRef: CollectionReference?


    suspend fun getOffers(): Flow<ReferralBonusState<List<OffersModel>>>

    fun onReferralPointsCollect(
        earnedPointsModel: EarnedPointsModel
    )

    fun onOfferCollect(
        offerId: String
    ): Flow<ReferralBonusState<String>>

    suspend fun getCurrentUserWithAwait(): Flow<ReferralBonusState<UserModel?>>

}