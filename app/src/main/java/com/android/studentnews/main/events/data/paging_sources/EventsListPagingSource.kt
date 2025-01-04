package com.android.studentnews.main.events.data.paging_sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class EventsListPagingSource(
    private val query: Query,
): PagingSource<QuerySnapshot, EventsModel>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, EventsModel>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, EventsModel> {
        return try {

            delay(2000)

            val currentPage = params.key ?: this@EventsListPagingSource.query.get().await()
            val lastPage = currentPage.documents[currentPage.size() - 1]
            val nextPage = this@EventsListPagingSource.query.startAfter(lastPage).get().await()


            return LoadResult.Page(
                data = currentPage.map {
                    it.toObject(EventsModel::class.java)
                },
                prevKey = null,
                nextKey = nextPage
            )

        } catch (e: FirebaseFirestoreException) {
            LoadResult.Error(Throwable("Failed to load events, May be cause of Internet!"))
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}