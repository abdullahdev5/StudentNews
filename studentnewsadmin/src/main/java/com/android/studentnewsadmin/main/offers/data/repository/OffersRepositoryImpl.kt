package com.android.studentnewsadmin.main.offers.data.repository

import android.net.Uri
import com.android.studentnewsadmin.core.domain.constants.FirestoreNodes
import com.android.studentnewsadmin.core.domain.constants.StorageNodes
import com.android.studentnewsadmin.main.offers.domain.constant.OfferTypes
import com.android.studentnewsadmin.main.offers.domain.model.OffersModel
import com.android.studentnewsadmin.main.offers.domain.repository.OffersRepository
import com.android.studentnewsadmin.main.offers.domain.resource.OffersState
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class OffersRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : OffersRepository {


    override val offersColRef: CollectionReference?
        get() = firestore.collection(FirestoreNodes.OFFERS_COL)

    override val storageRef: StorageReference?
        get() = storage.reference


    override fun onOfferUpload(
        offerName: String,
        offerDescription: String,
        offerImageUri: Uri,
        pointsRequired: Double,
        offerType: String,
        discountAmount: Double,
        offerTermsAndCondition: String,
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
                                        pointsRequired = pointsRequired,
                                        offerType = offerType,
                                        discountAmount = discountAmount,
                                        offerTermsAndCondition = offerTermsAndCondition,
                                        offerExpiryDate = if (offerType == OfferTypes.EXPIRED)
                                            Timestamp.now() else null
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


    override fun getOffersList(): Flow<OffersState<List<OffersModel>>> {
        return callbackFlow {

            offersColRef
                ?.addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(OffersState.Failed(error))
                    }

                    val offersList = value?.mapNotNull {
                        it.toObject(OffersModel::class.java)
                    } ?: emptyList()

                    trySend(OffersState.Success(offersList))
                }

            awaitClose {
                close()
            }
        }
    }

    override fun getOfferById(offerId: String): Flow<OffersState<OffersModel>> {
        return callbackFlow {

            offersColRef
                ?.document(offerId)
                ?.addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(OffersState.Failed(error))
                    }

                    if (value != null) {
                        val offerById = value.toObject(OffersModel::class.java)
                        trySend(OffersState.Success(offerById!!))
                    }
                }

            awaitClose {
                close()
            }
        }
    }

    override fun onOfferUpdate(
        offerId: String,
        offerName: String,
        offerDescription: String,
        prevImageUri: Uri,
        newOfferImageUri: Uri,
        pointsRequired: Double,
        offerType: String,
        discountAmount: Double,
        offerTermsAndCondition: String,
    ): Flow<OffersState<String>> {
        return callbackFlow {

            trySend(OffersState.Loading)

            try {

                if (newOfferImageUri != Uri.EMPTY) {

                    val destinationPath = storage
                        .getReferenceFromUrl(prevImageUri.toString())

                    val uploadTask = destinationPath
                        .putFile(newOfferImageUri)

                    uploadTask
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                val downloadUri = task.result.storage.downloadUrl

                                downloadUri
                                    .addOnSuccessListener { uri ->
                                        val offerModel = OffersModel(
                                            offerName = offerName,
                                            offerImageUrl = uri.toString(),
                                            offerDescription = offerDescription,
                                            pointsRequired = pointsRequired,
                                            offerType = offerType,
                                            discountAmount = discountAmount,
                                            offerTermsAndCondition = offerTermsAndCondition,
                                            offerExpiryDate = if (offerType == OfferTypes.EXPIRED)
                                                Timestamp.now() else null,
                                            updatedAt = Timestamp.now()
                                        )

                                        offersColRef
                                            ?.document(offerId)
                                            ?.set(
                                                offerModel, SetOptions.mergeFields(
                                                    "offerName",
                                                    "offerImageUrl",
                                                    "offerDescription",
                                                    "pointsRequired",
                                                    "offerType",
                                                    "discountAmount",
                                                    "offerTermsAndCondition",
                                                    "offerExpiryDate",
                                                    "updatedAt"
                                                )
                                            )
                                            ?.addOnSuccessListener {
                                                trySend(OffersState.Success("Offer Updated Successfully"))
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
                        .addOnProgressListener { task ->
                            val progress = 1.0 * task.bytesTransferred / task.totalByteCount
                            trySend(OffersState.Progress(progress.toFloat()))
                        }

                } else {
                    val offerModel = OffersModel(
                        offerName = offerName,
                        offerDescription = offerDescription,
                        pointsRequired = pointsRequired,
                        offerType = offerType,
                        discountAmount = discountAmount,
                        offerTermsAndCondition = offerTermsAndCondition,
                        offerExpiryDate = if (offerType == OfferTypes.EXPIRED)
                            Timestamp.now() else null,
                        updatedAt = Timestamp.now()
                    )

                    offersColRef
                        ?.document(offerId)
                        ?.set(
                            offerModel, SetOptions.mergeFields(
                                "offerName",
                                "offerDescription",
                                "pointsRequired",
                                "offerType",
                                "discountAmount",
                                "offerTermsAndCondition",
                                "offerExpiryDate",
                                "updatedAt"
                            )
                        )
                        ?.addOnSuccessListener {
                            trySend(OffersState.Success("Offer Updated Successfully"))
                        }
                        ?.addOnFailureListener { error ->
                            trySend(OffersState.Failed(error))
                        }
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