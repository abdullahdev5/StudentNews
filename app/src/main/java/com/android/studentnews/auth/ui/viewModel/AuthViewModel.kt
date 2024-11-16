package com.android.studentnews.auth.ui.viewModel

import androidx.lifecycle.ViewModel
import com.android.studentnews.auth.domain.models.RegistrationData
import com.android.studentnews.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    val currentUser: FirebaseUser?
        get() = authRepository.currentUser


    fun SignUpUser(
        email: String,
        password: String,
        registrationData: RegistrationData,
    ) = authRepository.signUpUser(email, password, registrationData)


    fun SignInUser(
        email: String,
        password: String,
    ) = authRepository.signInUser(email, password)


    fun signOut() = authRepository.signOut()


}