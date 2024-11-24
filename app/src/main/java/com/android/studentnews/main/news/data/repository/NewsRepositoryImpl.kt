package com.android.studentnews.news.data.repository

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.android.studentnews.main.news.NewsWorker
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.main.news.domain.model.CategoryModel
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.domain.resource.NewsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Duration
import java.util.concurrent.TimeUnit

class NewsRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val workManager: WorkManager,
) : NewsRepository {

    // References
    override val userDocRef: DocumentReference?
        get() = firestore
            .collection(FirestoreNodes.USERS_COL)
            .document(auth.currentUser?.uid.toString())

    override val newsColRef: CollectionReference?
        get() = firestore.collection(FirestoreNodes.NEWS_COL)

    override val categoriesColRef: CollectionReference?
        get() = firestore.collection(FirestoreNodes.CATEGORIES_COL)

    override val savedNewsColRef: CollectionReference?
        get() = userDocRef?.collection(FirestoreNodes.SAVED_NEWS_COL)


    // News
    override fun getNewsList(): Flow<NewsState<List<NewsModel>>> = callbackFlow {

        trySend(NewsState.Loading)

        newsColRef
            ?.orderBy("timestamp", Query.Direction.DESCENDING)
            ?.get()
            ?.addOnSuccessListener { documents -> //, error ->
                val news = documents.map {
                    it.toObject(NewsModel::class.java)
                }
                trySend(NewsState.Success(news))
            }
            ?.addOnFailureListener { error ->
                trySend(NewsState.Failed(error))
            }


        awaitClose {
            close()
        }
    }

    override suspend fun getNewsUpdates(): NewsModel? {

        val news = firestore
            .collection(FirestoreNodes.NEWS_COL)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toObject(NewsModel::class.java)

        return news

        if (news == null) {
            throw Exception()
        }
    }


    override fun onNewsSave(news: NewsModel): Flow<NewsState<String>> {
        return callbackFlow {

            savedNewsColRef
                ?.document(news.newsId ?: "")
                ?.set(news)
                ?.addOnSuccessListener { document ->
                    trySend(NewsState.Success(news.newsId ?: ""))
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

            savedNewsColRef
                ?.document(news.newsId.toString())
                ?.delete()
                ?.addOnSuccessListener { document ->
                    trySend(NewsState.Success("News Removed from Saved List"))
                }
                ?.addOnFailureListener { error ->
                    trySend(NewsState.Failed(error))
                }

            awaitClose {
                close()
            }
        }
    }

    override fun getSavedNewsList(): Flow<NewsState<List<NewsModel>>> {
        return callbackFlow {

            trySend(NewsState.Loading)

            val listener = savedNewsColRef
                ?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(NewsState.Failed(error))
                    }

                    if (value != null) {
                        val savedNews = value.map {
                            it.toObject(NewsModel::class.java)
                        }
                        trySend(NewsState.Success(savedNews))
                    }
                }

            awaitClose {
                listener?.remove()
            }
        }
    }


    // Category

    override fun getNewsListByCategory(category: String): Flow<NewsState<List<NewsModel>>> =
        callbackFlow {

            trySend(NewsState.Loading)

            val listener = newsColRef
                ?.whereEqualTo("category", category)
                ?.get()
                ?.addOnSuccessListener { documents ->
                    if (documents != null) {
                        val newsListByCategory = documents.map {
                            it.toObject(NewsModel::class.java)
                        }

                        trySend(NewsState.Success(newsListByCategory))
                    }
                }
                ?.addOnFailureListener { error ->
                    trySend(NewsState.Failed(error))
                }

            awaitClose {
                listener?.addOnCanceledListener {

                }
            }
        }

    override fun getCategoriesList(): Flow<NewsState<List<CategoryModel?>>> =
        callbackFlow {

            trySend(NewsState.Loading)

            val listener = categoriesColRef
                ?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.get()
                ?.addOnSuccessListener { documents ->
                    if (documents != null) {
                        val categories = documents.toObjects(CategoryModel::class.java)
                        trySend(NewsState.Success(categories))
                    }
                }
                ?.addOnFailureListener { error ->
                    trySend(NewsState.Failed(error))
                }

            awaitClose {
                listener?.addOnCanceledListener {

                }
            }
        }


    // Search
    override fun onSearch(
        query: String,
        currentSelectedCategory: String?,
    ): Flow<NewsState<List<NewsModel>>> =
        callbackFlow {

            trySend(NewsState.Loading)

            val listener = newsColRef
                ?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.get()
                ?.addOnSuccessListener { documents ->
                    if (documents != null) {

                        val news = documents.filter {
                            it.getString("title").toString()
                                .contains(query, ignoreCase = true)
                                    ||
                                    it.getString("description").toString()
                                        .contains(query, ignoreCase = true)
                        }.filter {
                            currentSelectedCategory?.let { category ->
                                it.getString("category").toString() == category
                            } ?: true
                        }
                            .map {
                                it.toObject(NewsModel::class.java)
                            }

                        trySend(NewsState.Success(news))
                    }
                }
                ?.addOnFailureListener { error ->
                    trySend(NewsState.Failed(error))
                }

            awaitClose {
                listener?.addOnCanceledListener {

                }
            }
        }

    override fun setupPeriodicNewsWorkRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequest.Builder(
            NewsWorker::class.java,
            repeatInterval = 5,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        ).setBackoffCriteria(
            backoffPolicy = BackoffPolicy.LINEAR,
            duration = Duration.ofMinutes(1)
        )
            // .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "news_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

    }

    override fun cancelPeriodicNewsWorkRequest() {
        workManager.cancelUniqueWork("news_work")
    }

}