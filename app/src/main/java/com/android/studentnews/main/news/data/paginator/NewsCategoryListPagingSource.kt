package com.android.studentnews.main.news.data.paginator

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.studentnews.main.news.domain.model.CategoryModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class NewsCategoryListPagingSource(
    private val categoryQuery: Query,
) : PagingSource<QuerySnapshot, CategoryModel>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, CategoryModel>): QuerySnapshot? =
        null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, CategoryModel> {

        return try {

            delay(2000)

            val currentPage = params.key ?: categoryQuery.get().await()
            val lastPage = currentPage.documents[currentPage.size() - 1]
            val nextPage = categoryQuery.startAfter(lastPage).get().await()

            return LoadResult.Page(
                data = currentPage.toObjects(CategoryModel::class.java),
                prevKey = null,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }

    }

}