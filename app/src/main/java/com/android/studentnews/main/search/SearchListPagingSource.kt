package com.android.studentnews.main.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.studentnews.main.events.DESCRIPTION
import com.android.studentnews.main.events.TITLE
import com.android.studentnews.main.news.CATEGORY
import com.android.studentnews.news.domain.model.NewsModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import okio.IOException

class SearchListPagingSource(
    private val newsQuery: Query,
    private val query: String,
    private val currentCategory: String?,
) : PagingSource<QuerySnapshot, NewsModel>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, NewsModel>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, NewsModel> {

        return try {

            delay(2000)

            val currentPage = params.key ?: newsQuery.get().await()
            val lastPage = currentPage.documents[currentPage.size() - 1]
            val nextPage = newsQuery.startAfter(lastPage).get().await()

            val filteredList = currentPage.filter {
                it.getString(TITLE).toString().contains(query, ignoreCase = true)
                        || it.getString(DESCRIPTION).toString()
                    .contains(query, ignoreCase = true)
            }.filter {
                currentCategory?.let { category ->
                    it.getString(CATEGORY).toString() == category
                } ?: true
            }

            return LoadResult.Page(
                data = filteredList
                    .map {
                        it.toObject(NewsModel::class.java)
                    },
                prevKey = null,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        } catch (e: IOException) {
            LoadResult.Error(e)
        }

    }

}