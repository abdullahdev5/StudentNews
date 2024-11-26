package com.android.studentnewsadmin.main.events.data.repository

import android.content.Context
import android.icu.util.TimeUnit
import android.net.Uri
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.android.studentnewsadmin.core.domain.constants.FirestoreNodes
import com.android.studentnewsadmin.core.domain.resource.EventsState
import com.android.studentnewsadmin.main.events.data.worker.ADDRESS
import com.android.studentnewsadmin.main.events.data.worker.ENDING_DATE
import com.android.studentnewsadmin.main.events.data.worker.ENDING_TIME_HOUR
import com.android.studentnewsadmin.main.events.data.worker.ENDING_TIME_MINUTES
import com.android.studentnewsadmin.main.events.data.worker.ENDING_TIME_STATUS
import com.android.studentnewsadmin.main.events.data.worker.IS_AVAILABLE
import com.android.studentnewsadmin.main.events.data.worker.STARTING_DATE
import com.android.studentnewsadmin.main.events.data.worker.STARTING_TIME_HOUR
import com.android.studentnewsadmin.main.events.data.worker.STARTING_TIME_MINUTES
import com.android.studentnewsadmin.main.events.data.worker.STARTING_TIME_STATUS
import com.android.studentnewsadmin.main.events.data.worker.UploadEventSuccessWorker
import com.android.studentnewsadmin.main.events.data.worker.UploadEventsWorker
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.android.studentnewsadmin.main.events.domain.repository.EventsRepository
import com.android.studentnewsadmin.main.news.data.worker.DESCRIPTION
import com.android.studentnewsadmin.main.news.data.worker.TITLE
import com.android.studentnewsadmin.main.news.data.worker.URI_LIST
import com.android.studentnewsadmin.main.news.domain.model.UrlList
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.time.Duration

class EventsRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val workManager: WorkManager,
) : EventsRepository {

    override val eventsColRef: CollectionReference?
        get() = firestore.collection(FirestoreNodes.EVENTS_COL)

    override val storageRef: StorageReference?
        get() = storage.reference

    override fun onEventUpload(
        title: String,
        description: String,
        address: String,
        startingDate: Long,
        startingTimeHour: Int,
        startingTimeMinutes: Int,
        startingTimeStatus: String,
        endingDate: Long,
        endingTimeHour: Int,
        endingTimeMinutes: Int,
        endingTimeStatus: String,
        uriList: List<Uri>,
        isAvailable: Boolean,
        context: Context,
    ): Flow<EventsState<String>> {
        return callbackFlow {

            val eventId = eventsColRef?.document()?.id.toString()
            val urlList = mutableStateListOf<UrlList>()
            val eventsFilesRef = storageRef?.child("events")?.child(eventId)?.child("files")
            val int = mutableIntStateOf(0)

            uriList.forEach { file ->

                val inputBytes = context
                    .contentResolver
                    .openInputStream(file)
                    .use {
                        it?.readBytes()
                    }

                val uploadTask = inputBytes?.let { inputBytes ->
                    eventsFilesRef
                        ?.child(file.lastPathSegment ?: "lastPathSegment")?.putBytes(inputBytes)
                }

                uploadTask
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uri = task.result.storage.downloadUrl

                            uri.addOnSuccessListener { imageUri ->

                                urlList.add(
                                    UrlList(
                                        url = imageUri.toString(),
                                        contentType = task.result.metadata?.contentType ?: "",
                                        sizeBytes = task.result.metadata?.sizeBytes ?: 0L,
                                        lastPathSegment = file.lastPathSegment ?: ""
                                    )
                                )

                                if (urlList.size == uriList.size) {

                                    val event = EventsModel(
                                        title = title,
                                        description = description,
                                        eventId = eventId,
                                        startingDate = startingDate,
                                        startingTimeHour = startingTimeHour,
                                        startingTimeMinutes = startingTimeMinutes,
                                        startingTimeStatus = startingTimeStatus,
                                        endingDate = endingDate,
                                        endingTimeHour = endingTimeHour,
                                        endingTimeMinutes = endingTimeMinutes,
                                        endingTimeStatus = endingTimeStatus,
                                        timestamp = Timestamp.now(),
                                        urlList = urlList,
                                        isAvailable = isAvailable
                                    )

                                    eventsColRef
                                        ?.document(eventId)
                                        ?.set(event)
                                        ?.addOnSuccessListener {
                                            trySend(EventsState.Success("Event Added Successfully"))
                                        }
                                        ?.addOnFailureListener {
                                            trySend(EventsState.Failed(it))
                                        }
                                }
                            }


                        } else {
                            trySend(EventsState.Failed(task.exception!!))
                        }
                    }
                    ?.addOnPausedListener {
                        uploadTask.pause()
                    }
                    ?.addOnCanceledListener {
                        uploadTask.cancel()
                    }

            }

            awaitClose {
                close()
            }

        }
    }

    override fun onEventEdit(
        eventId: String,
        title: String,
        description: String,
        address: String,
        startingDate: Long,
        startingTimeHour: Int,
        startingTimeMinutes: Int,
        startingTimeStatus: String,
        endingDate: Long,
        endingTimeHour: Int,
        endingTimeMinutes: Int,
        endingTimeStatus: String,
        isAvailable: Boolean,
    ): Flow<EventsState<String>> {
        return callbackFlow {

            trySend(EventsState.Loading)

            eventsColRef
                ?.document(eventId)
                ?.update(
                    TITLE, title,
                    DESCRIPTION, description,
                    ADDRESS, address,
                    STARTING_DATE, startingDate,
                    STARTING_TIME_HOUR, startingTimeHour,
                    STARTING_TIME_MINUTES, startingTimeMinutes,
                    STARTING_TIME_STATUS, startingTimeStatus,
                    ENDING_DATE, endingDate,
                    ENDING_TIME_HOUR, endingTimeHour,
                    ENDING_TIME_MINUTES, endingTimeMinutes,
                    ENDING_TIME_STATUS, endingTimeStatus,
                    IS_AVAILABLE, isAvailable
                )
                ?.addOnSuccessListener {
                    trySend(EventsState.Success("Event Updated Successfully"))
                }
                ?.addOnFailureListener { error ->
                    trySend(EventsState.Failed(error))
                }


            awaitClose {
                close()
            }
        }
    }


    override fun getEventsList(): Flow<EventsState<List<EventsModel>>> {
        return callbackFlow {

            trySend(EventsState.Loading)

            eventsColRef
                ?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(EventsState.Failed(error))
                    }

                    if (value != null) {
                        val eventsList = value.map {
                            it.toObject(EventsModel::class.java)
                        }
                        trySend(EventsState.Success(eventsList))
                    }
                }


            awaitClose {
                close()
            }
        }
    }

    override fun onEventDelete(eventId: String): Flow<EventsState<String>> {
        return callbackFlow {
            storageRef
                ?.child("events")
                ?.child(eventId)
                ?.child("files")
                ?.listAll()
                ?.addOnSuccessListener { filesList ->
                    filesList.items.forEach { item ->
                        item.delete()
                            .addOnSuccessListener {
                                eventsColRef
                                    ?.document(eventId)
                                    ?.delete()
                                    ?.addOnSuccessListener {
                                        trySend(EventsState.Success("Event Deleted Successfully"))
                                    }
                            }
                            .addOnFailureListener { error ->
                                trySend(EventsState.Failed(error))
                            }
                    }
                }
                ?.addOnFailureListener { error ->
                    trySend(EventsState.Failed(error))
                }

            awaitClose {
                close()
            }
        }
    }


    override fun onUploadEventWorkerStart(
        title: String,
        description: String,
        address: String,
        startingDate: Long,
        startingTimeHour: Int,
        startingTimeMinutes: Int,
        startingTimeStatus: String,
        endingDate: Long,
        endingTimeHour: Int,
        endingTimeMinutes: Int,
        endingTimeStatus: String,
        stringArray: Array<String>,
        isAvailable: Boolean,
    ) {

        val inputData = Data.Builder()
            .putString(TITLE, title)
            .putString(DESCRIPTION, description)
            .putString(ADDRESS, address)
            .putLong(STARTING_DATE, startingDate)
            .putInt(STARTING_TIME_HOUR, startingTimeHour)
            .putInt(STARTING_TIME_MINUTES, startingTimeMinutes)
            .putString(STARTING_TIME_STATUS, startingTimeStatus)
            .putLong(ENDING_DATE, endingDate)
            .putInt(ENDING_TIME_HOUR, endingTimeHour)
            .putInt(ENDING_TIME_MINUTES, endingTimeMinutes)
            .putString(ENDING_TIME_STATUS, endingTimeStatus)
            .putStringArray(URI_LIST, stringArray)
            .putBoolean(IS_AVAILABLE, isAvailable)
            .build()

        val workRequest = OneTimeWorkRequest.Builder(
            UploadEventsWorker::class.java
        )
            .setInputData(inputData)
            .build()

        val successRequest = OneTimeWorkRequest.Builder(
            UploadEventSuccessWorker::class.java
        )
            .build()


        workManager
            .beginWith(workRequest)
            .then(successRequest)
            .enqueue()
    }

}