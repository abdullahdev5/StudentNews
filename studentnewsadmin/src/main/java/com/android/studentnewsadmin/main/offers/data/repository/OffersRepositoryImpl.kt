package com.android.studentnewsadmin.main.offers.data.repository

import android.net.Uri
import com.android.studentnewsadmin.core.domain.constants.FirestoreNodes
import com.android.studentnewsadmin.core.domain.constants.StorageNodes
import com.android.studentnewsadmin.main.offers.domain.model.OffersModel
import com.android.studentnewsadmin.main.offers.domain.repository.OffersRepository
import com.android.studentnewsadmin.main.offers.domain.resource.OffersState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.roundToInt

class OffersRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
): OffersRepository {


    override val offersColRef: CollectionReference?
        get() = firestore.collection(FirestoreNodes.OFFERS_COL)

    override val storageRef: StorageReference?
        get() = storage.reference


    override fun onOfferUpload(
        offerName: String,
        offerDescription: String,
        offerImageUri: Uri,
        pointsWhenAbleToCollect: Double,
    ): Flow<OffersState<String>> {
        return callbackFlow {

            trySend(OffersState.Loading)

            try {

                val offerId = offersColRef?.document()?.id ?: ""

                val filePath = storageRef
                    ?.child("offers")
                    ?.child(offerId)
                    ?.child(StorageNodes.OFFERS_IMAGE_COL)

                val uploadTask = filePath
                    ?.child(offerImageUri.lastPathSegment ?: "")
                    ?.putFile(offerImageUri)

                uploadTask
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val downloadUri = task.result.storage.downloadUrl

                            downloadUri
                                .addOnSuccessListener { uri ->

                                    val offerModel = OffersModel(
                                        offerName = offerName,
                                        offerId = offerId,
                                        offerImageUrl = uri.toString(),
                                        offerDescription = offerDescription,
                                        pointsWhenAbleToCollect = pointsWhenAbleToCollect,
                                        timestamp = Timestamp.now()
                                    )

                                    offersColRef
                                        ?.document(offerId)
                                        ?.set(offerModel)
                                        ?.addOnSuccessListener {
                                            trySend(OffersState.Success("Offer Added to the Database"))
                                        }
                                        ?.addOnFailureListener { error ->
                                            trySend(OffersState.Failed(error))
                                        }
                                }
                                .addOnFailureListener { error ->
                                    trySend(OffersState.Failed(error))
                                }
                        } else {
                            trySend(OffersState.Failed(task.exception!!))
                        }
                    }
                    ?.addOnProgressListener { task ->
                        val progress = 1.0 * task.bytesTransferred / task.totalByteCount
                        trySend(OffersState.Progress(progress.toFloat()))
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                trySend(OffersState.Failed(e))
            }


            awaitClose {
                close()
            }
        }
    }
}