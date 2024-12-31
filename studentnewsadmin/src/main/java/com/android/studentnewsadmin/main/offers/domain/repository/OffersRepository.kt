package com.android.studentnewsadmin.main.offers.domain.repository

import android.net.Uri
import com.android.studentnewsadmin.main.offers.domain.model.OffersModel
import com.android.studentnewsadmin.main.offers.domain.resource.OffersState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow

interface OffersRepository {

    val offersColRef: CollectionReference?
    val storageRef: StorageReference?


    fun onOfferUpload(
        offerName: String,
        offerDescription: String,
        offerImageUri: Uri,
        pointsRequired: Double,
        offerType: String,
        discountAmount: Double,
        offerTermsAndCondition: String,

    ): Flow<OffersState<String>>


    fun getOffersList(): Flow<OffersState<List<OffersModel>>>

    fun getOfferById(offerId: String): Flow<OffersState<OffersModel>>

    fun onOfferUpdate(
        offerId: String,
        offerName: String,
        offerDescription: String,
        prevImageUri: Uri,
        newOfferImageUri: Uri,
        pointsRequired: Double,
        offerType: String,
        discountAmount: Double,
        offerTermsAndCondition: String,
    ): Flow<OffersState<String>>

}