package com.android.studentnews.auth.domain.repository

import com.android.studentnews.auth.domain.models.RegistrationData
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.auth.domain.resource.UserState
import com.android.studentnews.main.account.domain.resource.AccountState
import com.android.studentnews.news.domain.resource.NewsState
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUser: FirebaseUser?

    val userDocRef: DocumentReference?


    fun signUpUser(
        email: String,
        password: String,
        registrationData: RegistrationData,
    ): Flow<UserState<String>>

    fun signInUser(
        email: String,
        password: String,
    ): Flow<UserState<String>>

    fun signOut()


    fun getCurrentUser(): Flow<AccountState<UserModel?>>


}