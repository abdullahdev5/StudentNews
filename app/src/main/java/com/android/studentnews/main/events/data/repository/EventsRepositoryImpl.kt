package com.android.studentnews.main.events.data.repository

import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class EventsRepositoryImpl(
    private val firestore: FirebaseFirestore
): EventsRepository {


    override val eventsColRef: CollectionReference?
        get() = firestore.collection(FirestoreNodes.EVENTS_COL)


    override fun getEventsList(): Flow<EventsState<List<EventsModel>>> {
        return callbackFlow {

            trySend(EventsState.Loading)

            eventsColRef
                ?.get()
                ?.addOnSuccessListener { documents ->
                    val eventsList = documents.toObjects(EventsModel::class.java)
                    trySend(EventsState.Success(eventsList))
                }
                ?.addOnFailureListener { error ->
                    trySend(EventsState.Failed(error))
                }

            awaitClose {
                close()
            }
        }
    }

}