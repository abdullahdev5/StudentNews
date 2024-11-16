package com.android.studentnewsadmin.core.data.module

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import org.koin.dsl.module


val firebaseModule = module {

    single<FirebaseFirestore> { Firebase.firestore }

    single<FirebaseStorage> { Firebase.storage }

}