package com.android.studentnews.core.data.paginator

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.studentnews.news.domain.model.NewsModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class NewsListPagingSource(
    private val newsQuery: Query
): PagingSource<QuerySnapshot, NewsModel>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, NewsModel>): QuerySnapshot?  = null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, NewsModel> {
        return try {

            delay(2000)
            val currentPage = params.key ?: newsQuery.get().await()
            val lastVisiblePage = currentPage.documents[currentPage.size() - 1]
            val nextPage = newsQuery.startAfter(lastVisiblePage).get().await()

            return LoadResult.Page(
                data = currentPage.toObjects(NewsModel::class.java),
                prevKey = null,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}