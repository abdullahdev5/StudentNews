package com.android.studentnewsadmin.main.news.domain.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.android.studentnewsadmin.core.domain.resource.CategoryState
import com.android.studentnewsadmin.core.domain.resource.NewsState
import com.android.studentnewsadmin.main.news.domain.model.CategoryModel
import com.android.studentnewsadmin.main.news.domain.model.NewsModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    val newsColRef: CollectionReference?

    val storageRef: StorageReference?

    fun onNewsAdd(
        title: String,
        description: String,
        uriList: List<Uri>,
        category: String,
        link: String,
        linkTitle: String,
        context: Context
    ): Flow<NewsState<String>>

    fun getNewsList(): Flow<NewsState<List<NewsModel>>>

    fun onNewsDelete(newsId: String): Flow<NewsState<String>>


    fun onCategoryAdd(
        category: String,
        imageBitmap: Bitmap
    ): Flow<CategoryState<String>>

    fun getCategoryList(): Flow<CategoryState<List<CategoryModel>>>

    fun startNewsAddingWorker(
        title: String,
        description: String,
        stringArray: Array<String>,
        category: String,
        link: String,
        linkTitle: String,
    )

}