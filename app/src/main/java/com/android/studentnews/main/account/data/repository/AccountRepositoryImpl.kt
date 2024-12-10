package com.android.studentnews.main.account.data.repository

import android.graphics.Bitmap
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.auth.domain.repository.AuthRepository
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.core.domain.constants.StorageNodes
import com.android.studentnews.main.account.domain.repository.AccountRepository
import com.android.studentnews.main.account.domain.resource.AccountState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import java.io.ByteArrayOutputStream

class AccountRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
): AccountRepository {


    override val storageRef: StorageReference?
        get() = storage.reference

    override val userDocRef: DocumentReference?
        get() = firestore.collection(FirestoreNodes.USERS_COL).document(auth.currentUser?.uid.toString())


    override fun onUsernameSave(username: String) {
        userDocRef
            ?.update("registrationData.name", username)
    }


    override fun onSave(username: String, imageBitmap: Bitmap?): Flow<AccountState<String>> {
        return callbackFlow {

            trySend(AccountState.Loading)

            if (username.isNotEmpty()) {
                onUsernameSave(username)
            }

            if (imageBitmap != null) {
                val baos = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val imageInByteArray = baos.toByteArray()

                val imageRef = storageRef
                    ?.child(StorageNodes.USER_IMAGES)
                    ?.child(auth.currentUser?.uid.toString())

                val uploadImage = imageRef?.putBytes(imageInByteArray)


                uploadImage
                    ?.addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            val uri = task.result.storage.downloadUrl

                            uri
                                .addOnSuccessListener { imageUri ->

                                    userDocRef
                                        ?.update("profilePic", imageUri.toString())
                                        ?.addOnSuccessListener {
                                            trySend(AccountState.Success("Changes Saved Successfully"))
                                        }

                                }
                                .addOnFailureListener {
                                    trySend(AccountState.Failure(it))
                                }

                        } else {
                            trySend(AccountState.Failure(task.exception!!))
                        }
                    }
                    ?.addOnCanceledListener {
                        uploadImage.cancel()
                    }
                    ?.addOnPausedListener {
                        uploadImage.pause()
                    }
            } else {
                trySend(AccountState.Success("Changes Saved Successfully"))
            }


            awaitClose {
                close()
            }
        }
    }

}