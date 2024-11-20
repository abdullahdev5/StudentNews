package com.android.studentnewsadmin.main.events.data.repository

import com.android.studentnewsadmin.core.domain.constants.FirestoreNodes
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.repository.EventsRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class EventsRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
): EventsRepository {

    override val eventsColRef: CollectionReference?
        get() = firestore.collection(FirestoreNodes.EVENTS_COL)

    override val storageRef: StorageReference?
        get() = storage.reference

    override fun onEventAdd(): Flow<EventsState<String>> {
        return callbackFlow {

        }
    }

}