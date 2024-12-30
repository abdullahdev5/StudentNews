package com.android.studentnews.main.referral_bonus.data.repository

import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.main.referral_bonus.domain.model.RedeemedOffersModel
import com.android.studentnews.main.referral_bonus.domain.model.EarnedPointsModel
import com.android.studentnews.main.referral_bonus.domain.model.OffersModel
import com.android.studentnews.main.referral_bonus.domain.repository.ReferralBonusRepository
import com.android.studentnews.main.referral_bonus.domain.resource.ReferralBonusState
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReferralBonusRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ReferralBonusRepository {

    override val userDocRef: DocumentReference? =
        firestore.collection(FirestoreNodes.USERS_COL)
            .document(auth.currentUser?.uid.toString())

    override val offersColRef: CollectionReference? =
        firestore.collection(FirestoreNodes.OFFERS_COL)

    override val collectedOffersColRef: CollectionReference? =
        userDocRef?.collection(FirestoreNodes.REDEEMED_OFFERS_COL)


    override suspend fun getOffers(): Flow<ReferralBonusState<List<OffersModel>>> {
        return callbackFlow {

            trySend(ReferralBonusState.Loading)

            try {

                val collectedOfferIds = collectedOffersColRef
                    ?.get()
                    ?.await()
                    ?.documents
                    ?.mapNotNull { document ->
                        document.getString("offerId")
                    } ?: emptyList()

                val query = if (collectedOfferIds.isEmpty())
                    offersColRef
                else offersColRef?.whereNotIn("offerId", collectedOfferIds)

                query
                    ?.addSnapshotListener { value, error ->
                        if (error != null) {
                            trySend(ReferralBonusState.Failed(error))
                        }

                        val offersList = value?.mapNotNull {
                            it.toObject(OffersModel::class.java)
                        } ?: emptyList()
                        trySend(ReferralBonusState.Success(offersList))
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                trySend(ReferralBonusState.Failed(error = e))
            }


            awaitClose {
                close()
            }
        }
    }

    override fun onReferralPointsCollect(earnedPointsModel: EarnedPointsModel) {
        try {
            userDocRef
                ?.update(
                    "referralBonus.totalPoints",
                    FieldValue.increment(earnedPointsModel.earnedPoints ?: 0.0),
                    "referralBonus.earnedPointsList",
                    FieldValue.arrayRemove(earnedPointsModel),
                    "referralBonus.prevCollectedPointsTimestamp",
                    Timestamp.now()

                )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onOfferCollect(offerId: String): Flow<ReferralBonusState<String>> {
        return callbackFlow {

            val collectedOffer = RedeemedOffersModel(
                offerId = offerId,
                collectedAt = Timestamp.now()
            )

            collectedOffersColRef
                ?.document(offerId)
                ?.set(collectedOffer)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        trySend(
                            ReferralBonusState.Success(
                                data = "Offer has been added to Your Collected Offers Collection"
                            )
                        )
                    } else {
                        trySend(ReferralBonusState.Failed(error = task.exception!!))
                    }
                }


            awaitClose {
                close()
            }
        }
    }

}