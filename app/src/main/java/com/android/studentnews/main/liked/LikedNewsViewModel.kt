package com.android.studentnews.main.settings.liked

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.studentnews.core.data.snackbar_controller.SnackBarActions
import com.android.studentnews.core.data.snackbar_controller.SnackBarController
import com.android.studentnews.core.data.snackbar_controller.SnackBarEvents
import com.android.studentnews.core.domain.constants.Status
import com.android.studentnews.news.data.repository.LIKED_NEWS_LIST_PAGE_SIZE
import com.android.studentnews.news.domain.model.NewsModel
import com.android.studentnews.news.domain.repository.NewsRepository
import com.android.studentnews.news.domain.resource.NewsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LikedNewsViewModel(
    private val newsRepository: NewsRepository
): ViewModel() {

    private val _likedNewsList = MutableStateFlow<PagingData<NewsModel>>(PagingData.empty())
    val likedNewsList: StateFlow<PagingData<NewsModel>> = _likedNewsList


    init {
        getLikedNewsList()
    }


    fun getLikedNewsList() {
        viewModelScope.launch {
            newsRepository.getLikedNewsList(LIKED_NEWS_LIST_PAGE_SIZE)
                .cachedIn(this)
                .collectLatest { pagingData ->
                    _likedNewsList.value = pagingData
                }
        }
    }

}