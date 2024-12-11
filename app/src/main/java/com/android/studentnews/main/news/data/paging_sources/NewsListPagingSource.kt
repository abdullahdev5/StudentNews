package com.android.studentnews.core.data.paginator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.android.studentnews.main.events.DESCRIPTION
import com.android.studentnews.main.events.TITLE
import com.android.studentnews.main.news.CATEGORY
import com.android.studentnews.news.domain.model.NewsModel
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import okio.IOException

const val LENGTH_ERROR = "length=0; index=-1"


class NewsListPagingSource(
    private val query: Query,
    private val isForSearchNews: Boolean = false,
    private val isForLikedNews: Boolean = false,
    private val searchQuery: String = "",
    private val currentCategory: String? = "",
    private val currentUid: String? = "",
) : PagingSource<QuerySnapshot, NewsModel>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, NewsModel>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, NewsModel> {
        return try {

            delay(2000)

            val currentPage = params.key ?: this@NewsListPagingSource.query.get(Source.DEFAULT).await()
            val lastVisiblePage = currentPage.documents[currentPage.size() - 1]
            val nextPage =
                this@NewsListPagingSource.query.startAfter(lastVisiblePage).get(Source.DEFAULT)
                    .await()


            val filteredList = if (isForSearchNews) {
                currentPage.filter {
                    it.getString(TITLE).toString().contains(searchQuery, ignoreCase = true)
                            || it.getString(DESCRIPTION).toString()
                        .contains(searchQuery, ignoreCase = true)
                }.filter {
                    currentCategory?.let { category ->
                        it.getString(CATEGORY).toString() == category
                    } ?: true
                }
            } else if (isForLikedNews) {
                currentPage.filter {
                    val userIdsFromLikes = it.toObject(NewsModel::class.java).likes?.map {
                        it
                    }
                    if (userIdsFromLikes?.contains(currentUid)!!)
                        return@filter true
                    else return@filter false
                }
            } else currentPage

            return LoadResult.Page(
                data = filteredList.map {
                  it.toObject(NewsModel::class.java)
                },
                prevKey = null,
                nextKey = nextPage,
            )

        } catch (e: FirebaseFirestoreException) {
            if (isForLikedNews) {
                LoadResult.Error(Throwable("Failed To load liked news. May be cause of Internet!"))
            } else {
                LoadResult.Error(Throwable("Failed To load news. May be cause of Internet!"))
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}