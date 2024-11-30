package com.android.studentnews.main.events.data.repository

import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.main.events.EventsWorker
import com.android.studentnews.main.events.domain.models.EventsBookingModel
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.getField
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Duration

class EventsRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val workManager: WorkManager,
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

    override fun onEventRegister(
        eventId: String,
        eventsBookingModel: EventsBookingModel,
    ): Flow<EventsState<String>> {
        return callbackFlow {
            trySend(EventsState.Loading)

            eventsColRef
                ?.document(eventId)
                ?.update("bookings", FieldValue.arrayUnion(eventsBookingModel))
                ?.addOnSuccessListener {
                    trySend(EventsState.Success("Event Registered Successfully"))
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

    override fun getEventsListByAvailableStatus(availableStatus: Boolean): Flow<EventsState<List<EventsModel>>> {
        return callbackFlow {

            trySend(EventsState.Loading)

            eventsColRef
                ?.whereEqualTo("isAvailable", availableStatus)
                ?.get()
                ?.addOnSuccessListener { documents ->
                    val eventsListByAvailableStatus = documents.map {
                        it.toObject(EventsModel::class.java)
                    }
                    trySend(EventsState.Success(eventsListByAvailableStatus))
                }
                ?.addOnFailureListener { error ->
                    trySend(EventsState.Failed(error))
                }

            awaitClose {
                close()
            }
        }
    }


    override fun getEventsUpdate(): Flow<EventsState<EventsModel?>> {

//        val event = eventsColRef
//            ?.whereEqualTo("isAvailable", true)
//            ?.orderBy("timestamp", Query.Direction.DESCENDING)
//            ?.get()
//            ?.await()
//            ?.documents
//            ?.firstOrNull()
//            ?.toObject(EventsModel::class.java)
//
//        if (event == null) {
//            throw Exception()
//        }
//
//        return event

        return callbackFlow {
            eventsColRef
                ?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { value, error ->
                    if (error != null) {
                        throw error
                    }

                    if (value != null) {
                        for (document in value.documentChanges) {
                            if (document.type == DocumentChange.Type.MODIFIED) {
                                val event = document.document.toObject(EventsModel::class.java)
                                trySend(EventsState.Success(event))
                            }
                        }
                    }
                }

            awaitClose {
                close()
            }
        }
    }

    override suspend fun getCurrentUserName(): String {

        val currentUserName = userDocRef
            ?.get()
            ?.await()
            ?.toObject(UserModel::class.java)
            ?.registrationData
            ?.name

        return currentUserName ?: ""
    }


    override fun startEventWorker() {

        val workRequest = PeriodicWorkRequest.Builder(
            EventsWorker::class.java,
            repeatInterval = Duration.ofMinutes(15),
        )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                duration = Duration.ofMinutes(10)
            )
            .build()

        workManager
            .enqueueUniquePeriodicWork(
                "events_work",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

    override fun cancelEventsWorker() {
        workManager
            .cancelUniqueWork("events_work")
    }

}