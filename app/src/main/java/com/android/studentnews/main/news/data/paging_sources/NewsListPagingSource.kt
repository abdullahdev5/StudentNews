package com.android.studentnews.core.data.paginator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.android.studentnews.news.data.repository.NEWS_LIST_PAGE_SIZE
import com.android.studentnews.news.domain.model.NewsModel
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import okio.IOException

class NewsListPagingSource(
    private val newsQuery: Query,
): PagingSource<QuerySnapshot, NewsModel>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, NewsModel>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, NewsModel> {
        return try {

            delay(2000)
            val currentPage = params.key ?: newsQuery.get().await()
            val lastVisiblePage = currentPage.documents[currentPage.size() - 1]
            val nextPage = newsQuery.startAfter(lastVisiblePage).get().await()

            return LoadResult.Page(
                data = currentPage.toObjects(NewsModel::class.java),
                prevKey = null,
                nextKey = nextPage,
            )

        } catch (e: Exception) {
            return LoadResult.Error(e)
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }

}

@OptIn(ExperimentalPagingApi::class)
class NewsListRemoteMediator(
    private val newsQuery: Query,
) : RemoteMediator<QuerySnapshot, NewsModel>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<QuerySnapshot, NewsModel>,
    ): MediatorResult {
        val currentPage = newsQuery.get().await()

        when (loadType) {
            LoadType.REFRESH -> currentPage
            LoadType.PREPEND -> {
                MediatorResult.Success(
                    endOfPaginationReached = true
                )
            }

            LoadType.APPEND -> {
                val lastItem = currentPage.documents[currentPage.size() - 1]
                if (lastItem == null) {
                    currentPage
                } else {
                    newsQuery.startAfter(lastItem).get().await()
                }
            }
        }

        return MediatorResult.Success(
            endOfPaginationReached = true
        )
    }

}