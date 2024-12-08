package com.android.studentnews.main.events.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.main.events.EventsWorker
import com.android.studentnews.main.events.IS_AVAILABLE
import com.android.studentnews.main.events.data.paging_sources.EventsListPagingSource
import com.android.studentnews.main.events.domain.models.EventsBookingModel
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Duration
import java.util.concurrent.TimeUnit

const val EVENTS_LIST_PAGE_SIZE = 4
const val SAVED_EVENTS_LIST_PAGE_SIZE = 4
const val REGISTERED_EVENTS_LIST_PAGE_SIZE = 4


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


    override fun getEventsList(
        availableStatus: Boolean?,
        limit: Int,
    ): Flow<PagingData<EventsModel>> {

        val query = availableStatus?.let { available ->
            eventsColRef
                ?.whereEqualTo(IS_AVAILABLE, available)
                ?.limit(limit.toLong())
        } ?: eventsColRef
            ?.orderBy("timestamp", Query.Direction.DESCENDING)
            ?.limit(limit.toLong())

        return Pager(
            config = PagingConfig(
                pageSize = limit
            ),
            pagingSourceFactory = {
                EventsListPagingSource(
                    query!!
                )
            }
        ).flow

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

    override fun getSavedEventsList(limit: Int): Flow<PagingData<EventsModel>> {

        val query = savedEventsColRef
            ?.orderBy("timestamp", Query.Direction.DESCENDING)
            ?.limit(limit.toLong())

        return Pager(
            config = PagingConfig(
                pageSize = limit
            ),
            pagingSourceFactory = {
                EventsListPagingSource(
                    query = query!!
                )
            }
        ).flow

    }


    override fun getRegisteredEventsList(limit: Int): Flow<PagingData<EventsModel>> {

        val query = eventsColRef
            ?.orderBy("timestamp", Query.Direction.DESCENDING)
            ?.limit(limit.toLong())

        return Pager(
            config = PagingConfig(
                pageSize = limit
            ),
            pagingSourceFactory = {
                EventsListPagingSource(
                    query = query!!,
                    isForRegisteredEvents = true,
                    currentUid = auth.currentUser?.uid.toString()
                )
            }
        ).flow

//        val bookedEventsOfCurrentUser = documents.filter {
//            val userIdOfBookings =
//                it.toObject(EventsModel::class.java).bookings?.map {
//                    it.userId
//                }
//            if (
//                userIdOfBookings?.contains(auth.currentUser?.uid.toString())!!
//            ) return@filter true else return@filter false
//        }
//            .map {
//                it.toObject(EventsModel::class.java)
//            }
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


    override suspend fun getEventsUpdates(): EventsModel? {

        val event = eventsColRef
            ?.orderBy("timestamp", Query.Direction.DESCENDING)
            ?.get()
            ?.await()
            ?.documents
            ?.firstOrNull()
            ?.toObject(EventsModel::class.java)

        if (event == null) {
            throw Exception()
        }

        return event
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
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                duration = Duration.ofHours(4)
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