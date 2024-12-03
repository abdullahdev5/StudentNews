package com.android.studentnews.news.data.repository

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.android.studentnews.core.data.paginator.DefaultPaginator
import com.android.studentnews.main.news.NewsWorker
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.main.news.domain.model.CategoryModel
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.domain.resource.NewsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
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

//    private var lastVisibleItem: DocumentSnapshot? = null

    override var lastNewsListVisibleItem: DocumentSnapshot? = null


    // News
    override fun getNewsList(): Flow<NewsState<List<NewsModel>>> = callbackFlow {

        trySend(NewsState.Loading)

        newsColRef
            ?.orderBy("timestamp", Query.Direction.DESCENDING)
            ?.limit(4)
            ?.get()
            ?.addOnSuccessListener { documents -> //, error ->

                lastNewsListVisibleItem = documents.documents[documents.size() - 1]
                println("Last Visible Item Index in getNewsList(): $lastNewsListVisibleItem")

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

    override fun <T> getNextList(
        collectionReference: CollectionReference?,
        lastItem: DocumentSnapshot?,
        myClassToObject: Class<T>,
        isExists: Boolean,
    ): Flow<NewsState<List<T>>> {
        return callbackFlow {

            if (lastNewsListVisibleItem != null) {
                if (lastNewsListVisibleItem!!.exists()) {

                    DefaultPaginator(
                        collectionReference = collectionReference,
                        lastItem = lastItem,
                        onLoading = {
                            trySend(NewsState.Loading)
                        },
                        onSuccess = { nextList ->
                            trySend(NewsState.Success(nextList))
                        },
                        onError = { error ->
                            trySend(NewsState.Failed(error))
                        },
                        myClassToObject = myClassToObject,
                        isExistReturn = { isExists ->
                            trySend(NewsState.IsAfterPaginateDocumentsExist(isExists))
                        },
                        isExists = isExists
                    )
                }
            }

            awaitClose {
                close()
            }
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

    // Liked News
    override fun getLikedNewsList(): Flow<NewsState<List<NewsModel>>> {
        return callbackFlow {

            trySend(NewsState.Loading)

            newsColRef
                ?.get()
                ?.addOnSuccessListener { documents ->

                    val likedNewsList = documents.filter {
                        val userIdsFromLikes = it.toObject(NewsModel::class.java).likes?.map {
                            it
                        }
                        if (userIdsFromLikes?.contains(auth.currentUser?.uid.toString())!!)
                            return@filter true
                        else return@filter false
                    }
                        .map {
                            it.toObject(NewsModel::class.java)
                        }
                    trySend(NewsState.Success(likedNewsList))
                }
                ?.addOnFailureListener { error ->
                    trySend(NewsState.Failed(error))
                }


            awaitClose {
                close()
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
            duration = Duration.ofHours(2)
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