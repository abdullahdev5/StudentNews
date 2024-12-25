package com.android.studentnews.main.news.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import coil.imageLoader
import coil.request.ImageRequest
import com.android.studentnews.auth.domain.models.UserModel
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.main.news.LIKES
import com.android.studentnews.main.news.NEWS_ID
import com.android.studentnews.main.news.SHARE_COUNT
import com.android.studentnews.main.news.domain.repository.NewsDetailRepository
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.resource.NewsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.io.FileOutputStream

private val TAG = "NewsDetailRepositoryImpl"

class NewsDetailRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : NewsDetailRepository {


    override val userDocRef: DocumentReference?
        get() = firestore.collection(FirestoreNodes.USERS_COL)
            .document(auth.currentUser?.uid.toString())

    override val newsColRef: CollectionReference?
        get() = firestore.collection(FirestoreNodes.NEWS_COL)

    override val savedNewsColRef: CollectionReference?
        get() = userDocRef?.collection(FirestoreNodes.SAVED_NEWS_COL)


    override fun getNewsById(newsId: String): Flow<NewsState<NewsModel?>> =
        callbackFlow {

            trySend(NewsState.Loading)

            newsColRef
                ?.document(newsId)
                ?.addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(NewsState.Failed(error))
                    }

                    if (value != null) {
                        val newsById = value.toObject(NewsModel::class.java)
                        trySend(NewsState.Success(newsById))
                    }
                }

            awaitClose {
                close()
            }
        }

    override fun getIsNewsSaved(newsId: String): Flow<NewsState<Boolean>> {
        return callbackFlow {

            savedNewsColRef
                ?.document(newsId)
                ?.addSnapshotListener { value, error ->

                    if (error != null) {
                        trySend(NewsState.Failed(error))
                        Log.e(TAG, "getIsNewsSaved: Failed to get News Saved Or Not: ", error)
                    }

                    val newsId = value?.get(NEWS_ID)
                    val newsSaved = newsId != null == true

                    trySend(NewsState.Success(newsSaved))
                    Log.d(TAG, "getIsNewsSaved: IsNewsSaved: $newsSaved")
                }

            awaitClose {
                close()
            }
        }
    }

    override fun onNewsSave(news: NewsModel): Flow<NewsState<String>> {
        return callbackFlow {

            savedNewsColRef
                ?.document(news.newsId.toString())
                ?.set(news)
                ?.addOnSuccessListener { document ->
                    trySend(NewsState.Success("News Saved"))
                }
                ?.addOnFailureListener { error ->
                    trySend(NewsState.Failed(error))
                }

            awaitClose {
                close()
            }
        }
    }

    override fun onNewsRemoveFromSave(news: NewsModel): Flow<NewsState<String>> {
        return callbackFlow {

            try {
                savedNewsColRef
                    ?.document(news.newsId.toString())
                    ?.delete()
                    ?.addOnSuccessListener {
                        trySend(NewsState.Success("News Removed from Saved List"))
                    }
                    ?.addOnCanceledListener {
                        trySend(NewsState.Failed(error("Canceled to Removing News from Saved List!")))
                    }

            } catch (e: Exception) {
                trySend(NewsState.Failed(e))
            }

            awaitClose {
                close()
            }
        }
    }


    override fun onNewsShare(
        imageUrl: String,
        context: Context,
        onShare: (Uri?) -> Unit,
    ) {
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .target { drawable ->
                val bitmap = (drawable as BitmapDrawable).bitmap
                val tempFile = File.createTempFile("shared_IMG", ".jpg", context.cacheDir)

                FileOutputStream(tempFile)
                    .use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }


                val fileUri = try {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        tempFile
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Failed to Get File Uri: $e")
                    null
                }

                onShare.invoke(fileUri)

            }
            .build()
        context.imageLoader.enqueue(request)
    }

    override fun onNewsLike(newsId: String): Flow<NewsState<String>> {
        return callbackFlow {

            newsColRef
                ?.document(newsId)
                ?.update("likes", FieldValue.arrayUnion(auth.currentUser?.uid.toString()))
                ?.addOnSuccessListener {
                    Log.d("TAG", "onNewsLike: News Liked Successfully")
                }
                ?.addOnFailureListener { error ->
                    trySend(NewsState.Failed(error))
                    Log.e("TAG", "onNewsLike: Failed to Like the News. Error:- $error")
                }

            awaitClose {
                close()
            }
        }
    }

    override fun onNewsUnlike(newsId: String): Flow<NewsState<String>> {
        return callbackFlow {

            newsColRef
                ?.document(newsId)
                ?.update(LIKES, FieldValue.arrayRemove(auth.currentUser?.uid.toString()))
                ?.addOnSuccessListener {
                    Log.d("TAG", "onNewsRemoveFromLike: News UnLike Successfully")
                }
                ?.addOnFailureListener { error ->
                    Log.e("TAG", "onNewsUnlike: Failed to Unlike the News")
                }

            awaitClose {
                close()
            }
        }
    }

    override fun onCompletelyShared(newsId: String) {
        try {

            userDocRef
                ?.update(
                    "isUserShareTheNews", true,
                    "referralBonus.isUserCollectThePoints", false,
                )

            newsColRef
                ?.document(newsId)
                ?.update(SHARE_COUNT, FieldValue.increment(1))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onReferralPointsCollect(newsId: String) {
        try {

            userDocRef
                ?.update(
                    "isUserShareTheNews", false,
                    "referralBonus.totalPoints", FieldValue.increment(1.5),
                    "referralBonus.isUserCollectThePoints", true,
                )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onReferralPointsCollectDismiss() {
        try {

            userDocRef
                ?.update(
                    "isUserShareTheNews", false,
                    "referralBonus.isUserCollectThePoints", false,
                    "referralBonus.unCollectedPoints", FieldValue.increment(1.5)
                )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}