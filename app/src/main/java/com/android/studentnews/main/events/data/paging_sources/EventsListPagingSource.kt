package com.android.studentnews.main.events.data.paging_sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.studentnewsadmin.main.events.domain.models.EventsModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class EventsListPagingSource(
    private val query: Query,
    private val isForRegisteredEvents: Boolean = false,
    private val currentUid: String? = "",
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


            val filteredList = if (isForRegisteredEvents) {
                currentPage.filter {
                    val userIdOfBookings =
                        it.toObject(EventsModel::class.java).bookings?.map {
                            it.userId
                        }
                    if (
                        userIdOfBookings?.contains(currentUid)!!
                    ) return@filter true else return@filter false
                }
            } else currentPage


            return LoadResult.Page(
                data = filteredList.map {
                    it.toObject(EventsModel::class.java)
                },
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