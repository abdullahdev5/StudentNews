package com.android.studentnews.core.data.paginator

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Class

inline suspend fun <T> DefaultPaginator(
    collectionReference: CollectionReference,
    lastItem: DocumentSnapshot? = null,
    onLoading: () -> Unit,
    crossinline onSuccess: (DocumentSnapshot, List<T>) -> Unit,
    crossinline onError: (Throwable) -> Unit,
    myClassToObject: Class<T>,
    crossinline isEndReached: (Boolean) -> Unit,
    limit: Long,
) {
    onLoading()

    delay(1000)

    collectionReference
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .startAfter(lastItem)
//        .limit(limit)
        .get()
        .addOnSuccessListener { documents ->
            isEndReached(documents != null)
            val thisLastItem = documents.documents[documents.size() - 1]
            if (documents != null) {
                val nextList = documents.map {
                    it.toObject(myClassToObject)
                }
                onSuccess(thisLastItem, nextList)
            }
        }
        .addOnFailureListener { error ->
            onError(error)
        }
}

interface Paginator<Item> {

    suspend fun loadNextItems()

    fun getItems()

    fun reset()

}

class MyPaginator<Item>(
    private val initialKey: DocumentSnapshot?,
    private val firstTimeLimit: Long,
    private val collectionReference: CollectionReference?,
    private val onLoading: () -> Unit,
    private val onSuccess: (
        endReaced: Boolean, /* End Reached */
        nextItem: DocumentSnapshot?,
        items: List<Item>,
    ) -> Unit,
    private val onError: (Throwable?) -> Unit,
    private val onReset: () -> Unit,
    private val myClassToObject: Class<Item>,
) : Paginator<Item> {

    private var currentKey = initialKey
    private var isMakingRequest = false

    override fun getItems() {
        collectionReference
            ?.limit(firstTimeLimit)
            ?.get()
            ?.addOnSuccessListener { documents ->
                val items = documents.toObjects(myClassToObject)
                onSuccess(
                    documents == null,
                    documents.documents[documents.size() - 1],
                    items
                )
            }
    }

    override suspend fun loadNextItems() {

        onLoading()

        delay(2000)

        if (currentKey != null) {
            collectionReference
                ?.startAfter(currentKey)
                ?.get()
                ?.addOnSuccessListener { documents ->
                    val nextItems = documents.toObjects(myClassToObject)
                    onSuccess(
                        documents == null,
                        documents.documents[documents.size() - 1],
                        nextItems
                    )
                }
        }

    }

    override fun reset() {
        onReset()
    }

}