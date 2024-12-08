package com.android.studentnews.main.events.data.paging_sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class EventsListPagingSource(
    private val eventsQuery: Query,
): PagingSource<QuerySnapshot, EventsModel>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, EventsModel>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, EventsModel> {
        return try {

            delay(2000)

            val currentPage = params.key ?: eventsQuery.get().await()
            val lastPage = currentPage.documents[currentPage.size() - 1]
            val nextPage = eventsQuery.startAfter(lastPage).get().await()

            return LoadResult.Page(
                data = currentPage.toObjects(EventsModel::class.java),
                prevKey = null,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}