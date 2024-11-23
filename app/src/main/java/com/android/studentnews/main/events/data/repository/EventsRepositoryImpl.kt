package com.android.studentnews.main.events.data.repository

import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.main.events.domain.models.EventsBookingModel
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class EventsRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : EventsRepository {


    override val eventsColRef: CollectionReference?
        get() = firestore.collection(FirestoreNodes.EVENTS_COL)


    override fun getEventsList(): Flow<EventsState<List<EventsModel?>>> {
        return callbackFlow {

            trySend(EventsState.Loading)

            eventsColRef
                ?.orderBy("timestamp", Query.Direction.DESCENDING)
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

    override fun getEventById(eventId: String): Flow<EventsState<EventsModel?>> {
        return callbackFlow {

            trySend(EventsState.Loading)

            eventsColRef
                ?.document(eventId)
                ?.addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(EventsState.Failed(error))
                    }

                    if (value != null) {
                        val event = value.toObject(EventsModel::class.java)
                        trySend(EventsState.Success(event))
                    }
                }


            awaitClose {
                close()
            }
        }
    }

    override fun onEventBook(
        eventId: String,
        eventsBookingModel: EventsBookingModel
    ): Flow<EventsState<String>> {
        return callbackFlow {
            trySend(EventsState.Loading)

            eventsColRef
                ?.document(eventId)
                ?.update("bookings", FieldValue.arrayUnion(eventsBookingModel))
                ?.addOnSuccessListener {
                    trySend(EventsState.Success("Event Booked Successfully"))
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