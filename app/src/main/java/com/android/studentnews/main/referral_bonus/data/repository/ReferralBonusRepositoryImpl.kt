package com.android.studentnews.main.referral_bonus.data.repository

import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.main.referral_bonus.domain.model.OffersModel
import com.android.studentnews.main.referral_bonus.domain.repository.ReferralBonusRepository
import com.android.studentnews.main.referral_bonus.domain.resource.ReferralBonusState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReferralBonusRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : ReferralBonusRepository {

    override val userDocRef: DocumentReference?
        get() = firestore.collection(FirestoreNodes.USERS_COL)
            .document(auth.currentUser?.uid.toString())

    override val offersColRef: CollectionReference?
        get() = firestore.collection(FirestoreNodes.OFFERS_COL)


    override suspend fun getOffers(): Flow<ReferralBonusState<List<OffersModel>>> {
        return callbackFlow {

            trySend(ReferralBonusState.Loading)

            try {

                val data = offersColRef
                    ?.get(Source.SERVER)
                    ?.await()

                val offersList = data?.map {
                    it.toObject(OffersModel::class.java)
                }

                trySend(ReferralBonusState.Success(offersList!!))

            } catch (e: Exception) {
                e.printStackTrace()
                trySend(ReferralBonusState.Failed(e))
            }


            awaitClose {
                close()
            }
        }
    }


}