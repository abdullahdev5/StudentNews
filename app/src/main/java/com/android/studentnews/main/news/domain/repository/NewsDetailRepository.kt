package com.android.studentnews.main.news.domain.repository

import android.content.Context
import android.net.Uri
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.resource.NewsState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface NewsDetailRepository {

    val userDocRef: DocumentReference?
    val newsColRef: CollectionReference?
    val savedNewsColRef: CollectionReference?


    fun getNewsById(newsId: String): Flow<NewsState<NewsModel?>>
    fun onNewsShare(
        imageUrl: String,
        context: Context,
        onShare: (Uri?) -> Unit
    )
    // Save
    fun getIsNewsSaved(newsId: String): Flow<NewsState<Boolean>>
    fun onNewsSave(news: NewsModel): Flow<NewsState<String>>
    fun onNewsRemoveFromSave(news: NewsModel): Flow<NewsState<String>>

    // Like
    fun onNewsLike(newsId: String): Flow<NewsState<String>>
    fun onNewsUnlike(newsId: String): Flow<NewsState<String>>
    fun onCompletelyShared(newsId: String)

    fun onReferralPointsCollect(newsId: String)
    fun onReferralPointsCollectDismiss()

}