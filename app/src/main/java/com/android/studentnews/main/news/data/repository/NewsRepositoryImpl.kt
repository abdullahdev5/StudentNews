package com.android.studentnews.news.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.android.studentnews.core.data.paginator.NewsListPagingSource
import com.android.studentnews.core.domain.constants.FirestoreNodes
import com.android.studentnews.main.news.NewsWorker
import com.android.studentnews.main.news.data.paging_sources.NewsCategoryListPagingSource
import com.android.studentnews.main.news.domain.model.CategoryModel
import com.android.studentnews.main.search.SearchListPagingSource
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

const val NEWS_LIST_PAGE_SIZE = 4
const val NEWS_CATEGORY_LIST_PAGE_SIZE = 2


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
    override fun getNewsList(category: String?): Flow<PagingData<NewsModel>> {

        val query = category?.let {
            newsColRef?.whereEqualTo("category", category)
                ?.limit(NEWS_LIST_PAGE_SIZE.toLong())
        } ?: newsColRef?.orderBy("timestamp", Query.Direction.DESCENDING)
            ?.limit(NEWS_LIST_PAGE_SIZE.toLong())

        return Pager(
            config = PagingConfig(
                pageSize = NEWS_LIST_PAGE_SIZE,
            ),
            pagingSourceFactory = {
                NewsListPagingSource(
                    query!!
                )
            }
        ).flow

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

    override fun getCategoriesList(limit: Int): Flow<PagingData<CategoryModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = limit
            )
        ) {
            NewsCategoryListPagingSource(
                categoriesColRef
                    ?.orderBy("timestamp", Query.Direction.DESCENDING)
                    ?.limit(limit.toLong())
                !!
            )
        }.flow

    }


    // Search
    override fun onSearch(
        query: String,
        currentSelectedCategory: String?,
        limit: Int,
    ): Flow<PagingData<NewsModel>> {

        val newsQuery = newsColRef
            ?.limit(limit.toLong())

        return Pager(
            config = PagingConfig(
                pageSize = limit
            ),
            pagingSourceFactory = {
                SearchListPagingSource(
                    newsQuery = newsQuery!!,
                    query = query,
                    currentCategory = currentSelectedCategory
                )
            }
        ).flow


//        val news = documents.filter {
//            it.getString("title").toString()
//                .contains(query, ignoreCase = true)
//                    ||
//                    it.getString("description").toString()
//                        .contains(query, ignoreCase = true)
//        }.filter {
//            currentSelectedCategory?.let { category ->
//                it.getString("category").toString() == category
//            } ?: true
//        }
//            .map {
//                it.toObject(NewsModel::class.java)
//            }

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