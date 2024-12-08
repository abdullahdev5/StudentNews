package com.android.studentnews.main.account.domain.repository

import android.graphics.Bitmap
import android.os.Bundle
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.main.account.domain.resource.AccountState
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AccountRepository {

    val storageRef: StorageReference?

    val userDocRef: DocumentReference?

    fun onUserImageSave(imageBitmap: Bitmap): Flow<AccountState<String>>

    fun onUsernameSave(username: String)

    suspend fun getCurrentUser(): Flow<AccountState<UserModel?>>

}