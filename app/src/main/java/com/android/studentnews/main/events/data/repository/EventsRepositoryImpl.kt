package com.android.studentnews.main.events.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.android.studentnews.auth.domain.models.RegistrationData
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.main.events.EVENT_ID
import com.android.studentnews.main.events.EventsWorker
import com.android.studentnews.main.events.IS_AVAILABLE
import com.android.studentnews.main.events.data.paging_sources.EventsListPagingSource
import com.android.studentnews.main.events.domain.models.RegisteredEventsModelForUser
import com.android.studentnews.main.events.domain.models.RegistrationsOfEventsModel
import com.android.studentnews.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.util.nextAlphanumericString
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.random.Random

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

    override val registeredEventsCol: CollectionReference?
        get() = userDocRef?.collection(FirestoreNodes.REGISTERED_EVENTS_COL)


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

                    val eventIdFromValue = value?.getString(EVENT_ID)

                    if (error != null) {
                        trySend(EventsState.Failed(error))
                    }
                    if (eventIdFromValue == null) {
                        trySend(EventsState.Failed(Error("No Event Found")))
                    }

                    if (value != null && eventIdFromValue != null) {
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
        registrationData: RegistrationData,
    ): Flow<EventsState<String>> {
        return callbackFlow {
            trySend(EventsState.Loading)

            try {

                val registrationCode = Random.nextAlphanumericString(10)

                val registrationsSubCol = eventsColRef
                    ?.document(eventId)
                    ?.collection(FirestoreNodes.REGISTRATIONS_OF_EVENT)

                val registrationOfEventsModel = RegistrationsOfEventsModel(
                    eventId = eventId,
                    userId = auth.currentUser?.uid.toString(),
                    registrationCode = registrationCode,
                    registeredAt = Timestamp.now()
                )

                registrationsSubCol
                    ?.document(auth.currentUser?.uid.toString())
                    ?.set(registrationOfEventsModel)
                    ?.addOnSuccessListener {

                        val registeredEventsModelForUser = RegisteredEventsModelForUser(
                            eventId = eventId,
                            userId = auth.currentUser?.uid.toString(),
                            registrationData = registrationData,
                            registrationCode = registrationCode,
                            registeredAt = Timestamp.now(),
                        )

                        // Registered Events Collection From Current User Document
                        registeredEventsCol
                            ?.document(eventId)
                            ?.set(registeredEventsModelForUser)
                            ?.addOnSuccessListener {
                                trySend(EventsState.Success("Event Registered Successfully"))
                            }
                            ?.addOnFailureListener { error ->
                                trySend(EventsState.Failed(error))
                            }

                    }
                    ?.addOnFailureListener { error ->
                        trySend(EventsState.Failed(error))
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                trySend(EventsState.Failed(e))
            }

            awaitClose {
                close()
            }
        }
    }

    override fun getIsEventRegistered(eventId: String): Flow<EventsState<Boolean>> {
        return callbackFlow {
            try {

                registeredEventsCol
                    ?.document(eventId)
                    ?.addSnapshotListener { value, error ->
                        if (error != null) {
                            trySend(EventsState.Failed(error))
                        }

                        val eventIdFromRegisteredEvents = value?.getString(EVENT_ID)

                        trySend(EventsState.Success(
                            data = eventIdFromRegisteredEvents != null == true)
                        )
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                trySend(EventsState.Failed(e))
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

    override fun getIsEventSaved(eventId: String): Flow<EventsState<Boolean>> {
        return callbackFlow {

            savedEventsColRef
                ?.document(eventId)
                ?.addSnapshotListener { value, error ->

                    val eventIdFromSavedList = value?.getString(EVENT_ID)

                    trySend(EventsState.Success(eventIdFromSavedList != null == true))
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


    override suspend fun getRegisteredEventsList(limit: Int): Flow<PagingData<EventsModel>> {

        val registeredEventsIds = registeredEventsCol
            ?.orderBy("registeredAt", Query.Direction.DESCENDING)
            ?.get()
            ?.await()
            ?.mapNotNull {
                it.getString(EVENT_ID)
            } ?: emptyList()

        val query = eventsColRef
            ?.whereIn(EVENT_ID, registeredEventsIds)
            ?.limit(limit.toLong())

        return Pager(
            config = PagingConfig(
                pageSize = limit
            ),
            pagingSourceFactory = {
                EventsListPagingSource(
                    query = query!!,
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