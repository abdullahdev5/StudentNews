package com.android.studentnews.core.data.paginator

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

fun <T> DefaultPaginator(
    collectionReference: CollectionReference?,
    lastItem: DocumentSnapshot? = null,
    onLoading: () -> Unit,
    onSuccess: (List<T>) -> Unit,
    onError: (Throwable) -> Unit,
    myClassToObject: Class<T>,
    isExistReturn: (Boolean) -> Unit = {},
    isExists: Boolean,
) {
    if (isExists) {
        onLoading()

        collectionReference
            ?.orderBy("timestamp", Query.Direction.DESCENDING)
            ?.startAfter(lastItem)
            ?.get()
            ?.addOnSuccessListener { documents ->
                isExistReturn(documents == null)
                if (documents != null) {
                    val nextList = documents.map {
                        it.toObject(myClassToObject)
                    }
                    onSuccess(nextList)
                }
            }
            ?.addOnFailureListener { error ->
                onError(error)
            }
    }
}