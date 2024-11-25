package com.android.studentnews.main.events.data.repository

import androidx.compose.ui.util.fastJoinToString
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.main.events.domain.models.EventsBookingModel
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.getField
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.internal.filterList

class EventsRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : EventsRepository {


    override val userDocRef: DocumentReference?
        get() = firestore.collection(FirestoreNodes.USERS_COL)
            .document(auth.currentUser?.uid.toString())

    override val eventsColRef: CollectionReference?
        get() = firestore.collection(FirestoreNodes.EVENTS_COL)

    override val savedEventsColRef: CollectionReference?
        get() = userDocRef?.collection(FirestoreNodes.SAVED_EVENTS)


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
        eventsBookingModel: EventsBookingModel,
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


    override fun onEventSave(event: EventsModel): Flow<EventsState<String>> {
        return callbackFlow {

            trySend(EventsState.Loading)

            savedEventsColRef
                ?.document(event.eventId.toString())
                ?.set(event)
                ?.addOnSuccessListener {
                    trySend(EventsState.Success("Event Saved"))
                }
                ?.addOnFailureListener { error ->
                    trySend(EventsState.Failed(error))
                }

            awaitClose {
                close()
            }
        }
    }

    override fun onEventRemoveFromSave(event: EventsModel): Flow<EventsState<String>> {
        return callbackFlow {

            trySend(EventsState.Loading)

            savedEventsColRef
                ?.document(event.eventId.toString())
                ?.delete()
                ?.addOnSuccessListener {
                    trySend(EventsState.Success("Event Removed From Saved List"))
                }
                ?.addOnFailureListener { error ->
                    trySend(EventsState.Failed(error))
                }

            awaitClose {
                close()
            }
        }
    }

    override fun getSavedEventById(eventId: String): Flow<EventsState<EventsModel?>> {
        return callbackFlow {

            savedEventsColRef
                ?.document(eventId)
                ?.addSnapshotListener { value, error ->

                    if (value != null) {
                        val savedEvent = value.toObject(EventsModel::class.java)
                        trySend(EventsState.Success(savedEvent))
                    }
                }

            awaitClose {
                close()
            }
        }
    }

    override fun getSavedEventsList(): Flow<EventsState<List<EventsModel?>>> {
        return callbackFlow {

            trySend(EventsState.Loading)

            savedEventsColRef
                ?.addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(EventsState.Failed(error))
                    }

                    if (value != null) {
                        val savedEventsList = value.map {
                            it.toObject(EventsModel::class.java)
                        }
                        trySend(EventsState.Success(savedEventsList))
                    }
                }

            awaitClose {
                close()
            }
        }
    }


    override fun getRegisteredEventsList(): Flow<EventsState<List<EventsModel?>>> {
        return callbackFlow {

            trySend(EventsState.Loading)


            eventsColRef
                ?.get()
                ?.addOnSuccessListener { documents ->

                    val bookedEventsOfCurrentUser = documents.filter {
                        val userIdOfBookings =
                            it.toObject(EventsModel::class.java).bookings?.map {
                                it.userId
                            }
                        if (
                            userIdOfBookings?.contains(auth.currentUser?.uid.toString())!!
                        ) return@filter true else return@filter false
                    }
                        .map {
                            it.toObject(EventsModel::class.java)
                        }

                    trySend(EventsState.Success(bookedEventsOfCurrentUser))
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