package com.android.studentnewsadmin.main.offers.domain.repository

import android.net.Uri
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
        pointsWhenAbleToCollect: Double,
    ): Flow<OffersState<String>>

}