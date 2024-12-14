package com.android.studentnews.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.studentnews.news.domain.destination.MainDestination
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SampleViewModel : ViewModel() {

    val auth = Firebase.auth
    val db = Firebase.firestore

    private val _userById = MutableStateFlow<User?>(null)
    val userById = _userById.asStateFlow()

    fun getUserById() {
        db.collection("Users")
            .document("userId Here")
            .get()
            .addOnSuccessListener { document ->

                val userById = document.toObject(User::class.java)
                _userById.value = userById

            }
    }

}

@Composable
fun Screen(
    viewModel: SampleViewModel,
    navController: NavController,
) {

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {


    }
}


data class User(
    val email: String,
    val password: String,
    val uid: String,
) {
    constructor() : this("", "", "")
}