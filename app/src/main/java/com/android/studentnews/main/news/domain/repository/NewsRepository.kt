package com.android.studentnews.news.domain.repository

import androidx.paging.PagingData
import com.android.studentnews.main.news.domain.model.CategoryModel
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.resource.NewsState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    // References
    val userDocRef: DocumentReference?
    val newsColRef: CollectionReference?
    val categoriesColRef: CollectionReference?
    val savedNewsColRef: CollectionReference?

    var lastNewsListVisibleItem: DocumentSnapshot?
    var isNewsListEndReached: Boolean

    // News
    fun getNewsList(): Flow<PagingData<NewsModel>>
    suspend fun getNewsUpdates(): NewsModel?
    fun onNewsSave(news: NewsModel): Flow<NewsState<String>>
    fun onNewsRemoveFromSave(news: NewsModel): Flow<NewsState<String>>
    fun getSavedNewsList(): Flow<NewsState<List<NewsModel>>>
    // Liked News
    fun getLikedNewsList(): Flow<NewsState<List<NewsModel>>>

    // Category
    fun getNewsListByCategory(category: String): Flow<PagingData<NewsModel>>
    fun getCategoriesList(): Flow<PagingData<CategoryModel>>

    // Search
    fun onSearch(query: String, currentSelectedCategory: String?): Flow<NewsState<List<NewsModel>>>

    fun setupPeriodicNewsWorkRequest()
    fun cancelPeriodicNewsWorkRequest()

}