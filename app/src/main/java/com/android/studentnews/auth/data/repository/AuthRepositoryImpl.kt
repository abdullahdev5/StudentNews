package com.android.studentnews.auth.data.repository

import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.android.studentnews.auth.domain.models.RegistrationData
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.auth.domain.repository.AuthRepository
import com.android.studentnews.auth.domain.resource.UserState
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.core.domain.constants.FirestoreNodes.Companion.USERS_COL
import com.android.studentnews.main.account.domain.resource.AccountState
import com.android.studentnews.news.domain.resource.NewsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.random.Random

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val userDocRef: DocumentReference?
        get() = firestore.collection(USERS_COL).document(currentUser?.uid.toString())


    override fun signUpUser(
        email: String,
        password: String,
        registrationData: RegistrationData,
    ): Flow<UserState<String>> = callbackFlow {

        trySend(UserState.Loading)

        auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val randomColor = Color.argb(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), 1f)

                    val currentUser = UserModel(
                        email = email,
                        password = password,
                        uid = currentUser?.uid.toString(),
                        registrationData = registrationData,
                        profilePicBgColor = randomColor
                    )
                    firestore
                        .collection(FirestoreNodes.USERS_COL)
                        .document(currentUser.uid.toString())
                        .set(currentUser)
                        .addOnSuccessListener {
                            trySend(UserState.Created("Your account has been Created"))
                        }
                        .addOnFailureListener {
                            trySend(UserState.Failed(it))
                        }

                } else {
                    trySend(UserState.Failed(task.exception!!))
                }
            }


        awaitClose {
            close()
        }
    }

    override fun signInUser(
        email: String,
        password: String,
    ): Flow<UserState<String>> = callbackFlow {
        trySend(UserState.Loading)

        auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(UserState.Created("Your account Verified Successfully"))
                } else {
                    trySend(UserState.Failed(task.exception!!))
                }
            }

        awaitClose {
            close()
        }
    }


    override fun signOut() {
        auth.signOut()
    }


    override fun getCurrentUser(): Flow<AccountState<UserModel?>> =
        callbackFlow {

            val snapshotListener = userDocRef
                ?.addSnapshotListener { snapshot, error ->

                    if (snapshot != null) {
                        val currentUser = snapshot.toObject(UserModel::class.java)
                        trySend(AccountState.Success(currentUser))
                    }
                }

            awaitClose {
                snapshotListener?.remove()
            }
    }

}